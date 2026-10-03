package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.QuestionPalette
import com.example.ui.components.TimerDisplay
import com.example.ui.theme.KasavuGold
import com.example.ui.theme.KasavuGoldLight
import com.example.ui.theme.KeralaGreen
import com.example.ui.theme.KeralaGreenDark
import com.example.ui.theme.KeralaGreenLight
import com.example.ui.theme.KeralaInk
import com.example.ui.theme.KeralaLine
import com.example.ui.theme.KeralaMuted
import com.example.ui.theme.KeralaRed
import com.example.ui.theme.KeralaSurface

@Composable
fun LiveQuizScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val items by viewModel.quizItems.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()
    val remainingSeconds by viewModel.remainingSeconds.collectAsStateWithLifecycle()
    val isTimerRunning by viewModel.isTimerRunning.collectAsStateWithLifecycle()

    var showSubmitDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    // Intercept hardware/gesture back press to prevent accidental test loss
    BackHandler {
        showExitDialog = true
    }

    if (items.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No test active", color = KeralaMuted)
        }
        return
    }

    val currentItem = items.getOrElse(currentIndex) { items.first() }
    val progress = (currentIndex + 1).toFloat() / items.size.toFloat()
    val letters = listOf("A", "B", "C", "D")

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFEDF2EE))
    ) {
        // Sticky Top Quiz Control Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFEDF2EE),
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Question ${currentIndex + 1} of ${items.size}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = KeralaInk
                        )
                        Text(
                            text = "${items.count { it.isAnswered }} answered",
                            fontSize = 11.sp,
                            color = KeralaMuted
                        )
                    }

                    TimerDisplay(
                        remainingSeconds = remainingSeconds,
                        isTimerRunning = isTimerRunning
                    )

                    Button(
                        onClick = { showSubmitDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KeralaRed,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("submit_test_top_button")
                    ) {
                        Text("Submit", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = KeralaGreen,
                    trackColor = KeralaLine
                )
            }
        }

        // Scrollable Question Content & Options
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Question Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = KeralaSurface),
                border = BorderStroke(1.dp, KeralaLine)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Category & Review Tag
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = KeralaGreenLight
                        ) {
                            Text(
                                text = currentItem.entity.category,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = KeralaGreenDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        if (currentItem.isMarkedForReview) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = KasavuGoldLight,
                                border = BorderStroke(1.dp, KasavuGold)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bookmark,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = Color(0xFF241A00)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Review",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF241A00)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Question Text
                    Text(
                        text = currentItem.entity.question,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = KeralaInk,
                        lineHeight = 24.sp,
                        modifier = Modifier.testTag("question_text")
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 4 Options A, B, C, D
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        currentItem.options.forEachIndexed { optIndex, optText ->
                            val isSelected = currentItem.selectedIndex == optIndex
                            val letter = letters.getOrElse(optIndex) { "?" }

                            Surface(
                                onClick = { viewModel.selectOption(optIndex) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) KeralaGreenLight else Color.White,
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) KeralaGreen else KeralaLine
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("option_${letter}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Letter circle
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) KeralaGreen else Color.White)
                                            .border(
                                                width = 1.5.dp,
                                                color = if (isSelected) KeralaGreen else Color(0xFFB9C7BE),
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = letter,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else KeralaInk
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Text(
                                        text = optText,
                                        fontSize = 15.sp,
                                        color = KeralaInk,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Question Action Controls (Prev, Mark for Review, Clear, Next)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { viewModel.previousQuestion() },
                    enabled = currentIndex > 0,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = KeralaInk),
                    border = BorderStroke(1.dp, KeralaLine)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Prev")
                }

                OutlinedButton(
                    onClick = { viewModel.toggleMarkForReview() },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (currentItem.isMarkedForReview) Color(0xFF241A00) else KeralaInk,
                        containerColor = if (currentItem.isMarkedForReview) KasavuGoldLight else Color.Transparent
                    ),
                    border = BorderStroke(1.dp, if (currentItem.isMarkedForReview) KasavuGold else KeralaLine),
                    modifier = Modifier.testTag("review_toggle_button")
                ) {
                    Icon(
                        imageVector = if (currentItem.isMarkedForReview) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (currentItem.isMarkedForReview) "Marked" else "Review")
                }

                if (currentItem.isAnswered) {
                    TextButton(
                        onClick = { viewModel.clearCurrentSelection() },
                        colors = ButtonDefaults.textButtonColors(contentColor = KeralaRed),
                        modifier = Modifier.testTag("clear_button")
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Clear", fontSize = 12.sp)
                    }
                }

                Button(
                    onClick = {
                        if (currentIndex < items.size - 1) {
                            viewModel.nextQuestion()
                        } else {
                            showSubmitDialog = true
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KeralaGreen,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.testTag("next_or_submit_button")
                ) {
                    Text(if (currentIndex < items.size - 1) "Next" else "Done")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (currentIndex < items.size - 1) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Interactive Question Palette
            QuestionPalette(
                items = items,
                currentIndex = currentIndex,
                onSelectQuestion = { viewModel.goToQuestion(it) }
            )
        }
    }

    // Submit Confirmation Dialog
    if (showSubmitDialog) {
        val total = items.size
        val answered = items.count { it.isAnswered }
        val unanswered = total - answered
        val flagged = items.count { it.isMarkedForReview }

        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            title = {
                Text(
                    text = "Submit Mock Test?",
                    fontWeight = FontWeight.Bold,
                    color = KeralaInk
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Are you sure you want to finish and submit your test answers?",
                        fontSize = 14.sp,
                        color = KeralaInk
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Answered:", color = KeralaMuted, fontSize = 13.sp)
                        Text("$answered", fontWeight = FontWeight.Bold, color = KeralaGreen, fontSize = 13.sp)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Unanswered:", color = KeralaMuted, fontSize = 13.sp)
                        Text("$unanswered", fontWeight = FontWeight.Bold, color = KeralaRed, fontSize = 13.sp)
                    }
                    if (flagged > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Marked for Review:", color = KeralaMuted, fontSize = 13.sp)
                            Text("$flagged", fontWeight = FontWeight.Bold, color = KasavuGold, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitDialog = false
                        viewModel.submitTest(autoSubmitted = false)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KeralaGreen),
                    modifier = Modifier.testTag("confirm_submit_button")
                ) {
                    Text("Yes, Submit Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitDialog = false }) {
                    Text("Continue Test", color = KeralaMuted)
                }
            }
        )
    }

    // Exit Test Dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Leave Mock Test?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Your current test session will be ended. Would you like to submit your answers so far or exit without saving?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        viewModel.submitTest(autoSubmitted = false)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KeralaGreen)
                ) {
                    Text("Submit Answers")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        viewModel.navigateTo(AppScreen.SETUP)
                    }
                ) {
                    Text("Discard & Exit", color = KeralaRed)
                }
            }
        )
    }
}
