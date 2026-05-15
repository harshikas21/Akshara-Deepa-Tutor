package com.aksharadeepa.tutor.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

// Theme colors used in the app
private val BgDark = Color(0xFFF0F4EF)
private val SurfaceDark = Color.White
private val TextPrimary = Color(0xFF1A2517)
private val TextSecond = Color(0xFF6B7A69)
private val DeepBlue = Color(0xFF1A2517)
private val WarnRed = Color(0xFFB00020)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
    navController: NavController,
    pdfFileName: String,
    title: String
) {
    val context = LocalContext.current
    var pages by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(pdfFileName) {
        isLoading = true
        errorMessage = null
        withContext(Dispatchers.IO) {
            try {
                val tempFile = File(context.cacheDir, pdfFileName)
                
                // Copy from assets to temp file
                try {
                    context.assets.open("study_notes/$pdfFileName").use { input ->
                        FileOutputStream(tempFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                } catch (e: java.io.FileNotFoundException) {
                    errorMessage = "Study notes for this chapter are not available yet. Please check back later!"
                    isLoading = false
                    return@withContext
                }

                // Open and render PDF
                ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY).use { pfd ->
                    val pdfRenderer = PdfRenderer(pfd)
                    val bitmapList = mutableListOf<Bitmap>()

                    try {
                        for (i in 0 until pdfRenderer.pageCount) {
                            pdfRenderer.openPage(i).use { page ->
                                val bitmap = Bitmap.createBitmap(
                                    page.width * 2,
                                    page.height * 2,
                                    Bitmap.Config.ARGB_8888
                                )
                                // Fill with white background before rendering
                                val canvas = Canvas(bitmap)
                                canvas.drawColor(AndroidColor.WHITE)
                                
                                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                                bitmapList.add(bitmap)
                            }
                        }
                        pages = bitmapList
                        isLoading = false
                    } finally {
                        pdfRenderer.close()
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Something went wrong while opening the PDF: ${e.localizedMessage}"
                isLoading = false
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        containerColor = BgDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A2517))
            )
        }
    ) { padding ->
        when {
            isLoading -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = DeepBlue)
                        Spacer(Modifier.height(16.dp))
                        Text("Loading PDF...", color = TextSecond, fontSize = 14.sp)
                    }
                }
            }

            errorMessage != null -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⚠️", fontSize = 40.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(errorMessage ?: "Unknown error", color = WarnRed, fontSize = 14.sp)
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(BgDark),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        Text(
                            "Total Pages: ${pages.size}",
                            color = TextSecond,
                            fontSize = 13.sp
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    items(pages.size) { index ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(4.dp)) {
                                Text(
                                    "Page ${index + 1}",
                                    color = TextSecond,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                )
                                Image(
                                    bitmap = pages[index].asImageBitmap(),
                                    contentDescription = "Page ${index + 1}",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
