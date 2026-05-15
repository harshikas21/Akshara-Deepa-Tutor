package com.aksharadeepa.tutor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aksharadeepa.tutor.data.model.Chapter
import com.aksharadeepa.tutor.viewmodel.LocalRepository
import com.aksharadeepa.tutor.viewmodel.SyllabusTrackerViewModel
import com.aksharadeepa.tutor.viewmodel.TutorViewModelFactory

@Composable
fun SyllabusTrackerScreen(
    onChapterSelected: (Chapter) -> Unit,
    onViewNotes: (Chapter) -> Unit,
    viewModel: SyllabusTrackerViewModel = viewModel(
        factory = TutorViewModelFactory(LocalRepository.current!!)
    )
) {
    val chapters by viewModel.chapters.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val subjects = viewModel.getSubjects()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4EF))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A2517))
                .padding(20.dp)
        ) {
            Text(
                text = "Syllabus Tracker",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Subject Selector (Tabs)
        ScrollableTabRow(
            selectedTabIndex = subjects.indexOf(selectedSubject),
            containerColor = Color(0xFF1A2517),
            contentColor = Color(0xFFACC8A2),
            edgePadding = 16.dp,
            divider = {}
        ) {
            subjects.forEach { subject ->
                Tab(
                    selected = selectedSubject == subject,
                    onClick = { viewModel.selectSubject(subject) },
                    text = {
                        Text(
                            text = subject,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                )
            }
        }

        // Chapters List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(chapters) { chapter ->
                ChapterItem(
                    chapter = chapter,
                    onClick = { onChapterSelected(chapter) },
                    onViewNotes = { onViewNotes(chapter) }
                )
            }
        }
    }
}

@Composable
fun ChapterItem(
    chapter: Chapter,
    onClick: () -> Unit,
    onViewNotes: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Chapter ${chapter.chapterNumber}",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7A69),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = chapter.chapterName,
                        fontSize = 16.sp,
                        color = Color(0xFF1A2517),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color(0xFFACC8A2)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Progress
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = chapter.completionPercentage / 100f,
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp),
                    color = Color(0xFF1A2517),
                    trackColor = Color(0xFFE8F0E8)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${chapter.completionPercentage}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A2517)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            TextButton(
                onClick = onViewNotes,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = "📄 View Notes",
                    color = Color(0xFF1A2517),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}