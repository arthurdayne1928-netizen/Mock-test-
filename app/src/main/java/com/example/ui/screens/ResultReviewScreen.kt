package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.ui.QuizItem
import com.example.ui.theme.KasavuGold
import com.example.ui.theme.KasavuGoldContainer
import com.example.ui.theme.KasavuGoldDark
import com.example.ui.theme.KasavuGoldLight
import com.example.ui.theme.KeralaGreen
import com.example.ui.theme.KeralaGreenDark
import com.example.ui.theme.KeralaGreenLight
import com.example.ui.theme.KeralaInk
import com.example.ui.theme.KeralaLine
import com.example.ui.theme.KeralaMuted
import com.example.ui.theme.KeralaRed
import com.example.ui.theme.KeralaRedLight
import com.example.ui.theme.KeralaSurface

enum class ReviewFilter {
    ALL,
    INCORRECT,
    SKIPPED,
    CORRECT
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResultReviewScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val summary by viewModel.lastQuizSummary.collectAsStateWithLifecycle()

    BackHandler {
        viewModel.navigateTo(AppScreen.SETUP)
    }

    if (summary == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No test result available", color = KeralaMuted)
        }
        return
    }

    val res = summary!!
    var selectedFilter by remember { mutableStateOf(ReviewFilter.ALL) }

    val filteredItems = remember(selectedFilter, res) {
        when (selectedFilter) {
            ReviewFilter.ALL -> res.items
            ReviewFilter.INCORRECT -> res.items.filter { it.isWrong }
            ReviewFilter.SKIPPED -> res.items.filter { it.isSkipped }
            ReviewFilter.CORRECT -> res.items.filter { it.isCorrect }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Score Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = KeralaSurface),
                border = BorderStroke(1.dp, KeralaLine)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val statusText = when {
                        res.percentage >= 70 -> "Outstanding Performance! 🏆"
                        res.percentage >= 50 -> "Good Effort! On Track for PSC 👍"
                        res.percentage >= 35 -> "Fair Attempt. Keep Practicing 📚"
                        else -> "Needs Revision. Try Again! 💪"
                    }

                    Text(
                        text = statusText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (res.percentage >= 50) KeralaGreenDark else KasavuGoldDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Final Score
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format("%.2f", res.finalScore),
                            fontSize = 44.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = KeralaGreen,
                            lineHeight = 46.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "/ ${res.totalQuestions}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = KeralaMuted,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    Text(
                        text = "${String.format("%.1f", res.percentage)}% score • ${String.format("%.1f", res.accuracyPercentage)}% accuracy",
                        fontSize = 13.sp,
                        color = KeralaMuted,
                        fontWeight = FontWeight.Medium
                    )

                    if (res.negativeMarkRate > 0 && res.wrongCount > 0) {
                        val deducted = res.wrongCount * res.negativeMarkRate
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "(${res.correctCount} correct marks - ${String.format("%.2f", deducted)} negative marks)",
                            fontSize = 11.sp,
                            color = KeralaRed
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Stats Grid
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatTile(label = "Attempted", value = "${res.attemptedCount}", color = KeralaInk)
                        StatTile(label = "Correct", value = "${res.correctCount}", color = KeralaGreen)
                        StatTile(label = "Wrong", value = "${res.wrongCount}", color = KeralaRed)
                        StatTile(label = "Skipped", value = "${res.skippedCount}", color = KeralaMuted)
                        val timeStr = if (res.timeTakenSeconds > 0) {
                            val mins = res.timeTakenSeconds / 60
                            val secs = res.timeTakenSeconds % 60
                            "${mins}m ${secs}s"
                        } else "Untimed"
                        StatTile(label = "Time Taken", value = timeStr, color = KeralaInk)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.retakeCurrentTest() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = KeralaGreen,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("retake_test_button")
                        ) {
                            Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Retake Test")
                        }

                        OutlinedButton(
                            onClick = { viewModel.navigateTo(AppScreen.SETUP) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = KeralaGreenDark),
                            border = BorderStroke(1.dp, KeralaLine),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("new_mock_test_button")
                        ) {
                            Text("New Test")
                        }
                    }
                }
            }
        }

        // Section Title & Filters
        item {
            Column {
                Text(
                    text = "Question Review & Explanations",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = KeralaInk
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == ReviewFilter.ALL,
                        onClick = { selectedFilter = ReviewFilter.ALL },
                        label = { Text("All (${res.items.size})", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = KeralaGreenLight,
                            selectedLabelColor = KeralaGreenDark
                        )
                    )
                    FilterChip(
                        selected = selectedFilter == ReviewFilter.INCORRECT,
                        onClick = { selectedFilter = ReviewFilter.INCORRECT },
                        label = { Text("Wrong (${res.wrongCount})", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = KeralaRedLight,
                            selectedLabelColor = KeralaRed
                        )
                    )
                    FilterChip(
                        selected = selectedFilter == ReviewFilter.SKIPPED,
                        onClick = { selectedFilter = ReviewFilter.SKIPPED },
                        label = { Text("Skipped (${res.skippedCount})", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = KeralaLine,
                            selectedLabelColor = KeralaInk
                        )
                    )
                    FilterChip(
                        selected = selectedFilter == ReviewFilter.CORRECT,
                        onClick = { selectedFilter = ReviewFilter.CORRECT },
                        label = { Text("Correct (${res.correctCount})", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = KeralaGreenLight,
                            selectedLabelColor = KeralaGreenDark
                        )
                    )
                }
            }
        }

        // Questions List
        itemsIndexed(filteredItems) { idx, item ->
            ReviewQuestionCard(
                index = idx + 1,
                item = item
            )
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun StatTile(
    label: String,
    value: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, KeralaLine),
        color = Color.White,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = KeralaMuted
            )
        }
    }
}

@Composable
private fun ReviewQuestionCard(
    index: Int,
    item: QuizItem
) {
    val letters = listOf("A", "B", "C", "D")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = KeralaSurface),
        border = BorderStroke(1.dp, KeralaLine)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Status Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Q$index.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = KeralaInk
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = KeralaGreenLight
                    ) {
                        Text(
                            text = item.entity.category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = KeralaGreenDark,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                val (badgeText, badgeBg, badgeTextColor) = when {
                    item.isCorrect -> Triple("Correct (+1)", KeralaGreenLight, KeralaGreenDark)
                    item.isWrong -> Triple("Wrong", KeralaRedLight, KeralaRed)
                    else -> Triple("Not Answered", Color(0xFFF0F0F0), KeralaMuted)
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeBg
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeTextColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Question
            Text(
                text = item.entity.question,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = KeralaInk,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Options with comparison
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item.options.forEachIndexed { optIdx, optText ->
                    val isCorrectOpt = optIdx == item.correctIndex
                    val isUserSelection = optIdx == item.selectedIndex

                    val optBg = when {
                        isCorrectOpt -> KeralaGreenLight
                        isUserSelection && !isCorrectOpt -> KeralaRedLight
                        else -> Color.White
                    }

                    val optBorder = when {
                        isCorrectOpt -> BorderStroke(1.5.dp, KeralaGreen)
                        isUserSelection && !isCorrectOpt -> BorderStroke(1.5.dp, KeralaRed)
                        else -> BorderStroke(1.dp, KeralaLine)
                    }

                    val letter = letters.getOrElse(optIdx) { "?" }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = optBg,
                        border = optBorder,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$letter)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isCorrectOpt) KeralaGreenDark else KeralaInk
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = optText,
                                fontSize = 13.sp,
                                color = KeralaInk,
                                modifier = Modifier.weight(1f)
                            )

                            if (isCorrectOpt) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Correct Answer",
                                    tint = KeralaGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else if (isUserSelection) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Your Selection",
                                    tint = KeralaRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Explanation
            if (item.entity.explanation.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = KasavuGoldContainer,
                    border = BorderStroke(1.dp, KasavuGoldLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Explanation",
                            tint = KasavuGoldDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Explanation:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF241A00)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.entity.explanation,
                                fontSize = 12.sp,
                                color = Color(0xFF241A00),
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
