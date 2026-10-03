package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import com.example.ui.theme.KasavuGold
import com.example.ui.theme.KasavuGoldDark
import com.example.ui.theme.KasavuGoldLight
import com.example.ui.theme.KeralaGreen
import com.example.ui.theme.KeralaGreenDark
import com.example.ui.theme.KeralaGreenLight
import com.example.ui.theme.KeralaInk
import com.example.ui.theme.KeralaLine
import com.example.ui.theme.KeralaMuted
import com.example.ui.theme.KeralaSurface

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MockTestSetupScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val totalCount by viewModel.questionCount.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val selectedCat by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val numQuestions by viewModel.questionCountInput.collectAsStateWithLifecycle()
    val minutes by viewModel.timeMinutesInput.collectAsStateWithLifecycle()
    val negRate by viewModel.negativeMarkRate.collectAsStateWithLifecycle()
    val shuffleOpts by viewModel.shuffleOptions.collectAsStateWithLifecycle()
    val testHistory by viewModel.testResults.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = KeralaSurface),
            border = BorderStroke(1.dp, KeralaLine)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Start a mock test",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = KeralaGreenDark,
                        letterSpacing = (-0.3).sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Questions and options are randomized every time to ensure true mastery.",
                        fontSize = 13.sp,
                        color = KeralaMuted,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Big Counter
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$totalCount",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = KeralaGreen,
                        lineHeight = 40.sp
                    )
                    Text(
                        text = "questions in bank",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = KeralaMuted
                    )
                }
            }
        }

        // Empty Bank Callout
        if (totalCount == 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = KasavuGoldLight),
                border = BorderStroke(1.dp, KasavuGold)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Your question bank is empty!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF241A00)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Add your own questions or load the bundled Kerala PSC sample set to start testing immediately.",
                        fontSize = 13.sp,
                        color = Color(0xFF3E2D00)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { viewModel.loadSampleQuestions() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = KeralaGreenDark,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("load_samples_button")
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Load Sample Set")
                        }

                        OutlinedButton(
                            onClick = { viewModel.navigateTo(AppScreen.ADD_QUESTIONS) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = KeralaGreenDark),
                            border = BorderStroke(1.dp, KeralaGreenDark)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Questions")
                        }
                    }
                }
            }
        }

        // Test Setup Configuration Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = KeralaSurface),
            border = BorderStroke(1.dp, KeralaLine)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Exam Settings",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = KeralaInk
                )
                Spacer(modifier = Modifier.height(16.dp))

                // 1. Category Selector
                Text(
                    text = "Category",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = KeralaInk
                )
                Spacer(modifier = Modifier.height(6.dp))

                var catExpanded by remember { mutableStateOf(false) }
                val allCats = listOf("All") + categories.filter { it.isNotBlank() }

                ExposedDropdownMenuBox(
                    expanded = catExpanded,
                    onExpandedChange = { catExpanded = !catExpanded }
                ) {
                    OutlinedTextField(
                        value = if (selectedCat == "All") "All Categories (Full Syllabus)" else selectedCat,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .testTag("category_dropdown"),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = catExpanded,
                        onDismissRequest = { catExpanded = false }
                    ) {
                        allCats.forEach { cat ->
                            DropdownMenuItem(
                                text = {
                                    Text(if (cat == "All") "All Categories (Full Syllabus)" else cat)
                                },
                                onClick = {
                                    viewModel.selectedCategory.value = cat
                                    catExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 2. Number of questions
                val maxAllowed = if (totalCount > 0) totalCount else 20
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Number of Questions",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = KeralaInk
                    )
                    Text(
                        text = "$numQuestions Qs",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = KeralaGreen
                    )
                }

                Slider(
                    value = numQuestions.coerceIn(5, maxAllowed).toFloat(),
                    onValueChange = { viewModel.questionCountInput.value = it.toInt() },
                    valueRange = 5f..maxAllowed.coerceAtLeast(5).toFloat(),
                    steps = if (maxAllowed > 5) (maxAllowed - 5) / 5 else 0,
                    colors = SliderDefaults.colors(
                        thumbColor = KeralaGreen,
                        activeTrackColor = KeralaGreen,
                        inactiveTrackColor = KeralaLine
                    ),
                    modifier = Modifier.testTag("questions_slider")
                )

                // Quick preset pills
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(5, 10, 15, 20, 30, maxAllowed).distinct().filter { it <= maxAllowed }.forEach { preset ->
                        FilterChip(
                            selected = numQuestions == preset,
                            onClick = { viewModel.questionCountInput.value = preset },
                            label = { Text("$preset Qs", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = KeralaGreenLight,
                                selectedLabelColor = KeralaGreenDark
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = numQuestions == preset,
                                borderColor = if (numQuestions == preset) KeralaGreen else KeralaLine
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 3. Time Duration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Time Limit",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = KeralaInk
                    )
                    Text(
                        text = if (minutes == 0) "No Timer (Practice Mode)" else "$minutes Minutes",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (minutes == 0) KasavuGoldDark else KeralaGreen
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(0 to "Untimed", 5 to "5 min", 10 to "10 min", 15 to "15 min", 20 to "20 min", 30 to "30 min").forEach { (mins, label) ->
                        FilterChip(
                            selected = minutes == mins,
                            onClick = { viewModel.timeMinutesInput.value = mins },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = KasavuGoldLight,
                                selectedLabelColor = Color(0xFF241A00)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = minutes == mins,
                                borderColor = if (minutes == mins) KasavuGold else KeralaLine
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 4. Negative Marking
                Text(
                    text = "Negative Marking",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = KeralaInk
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val markingOptions = listOf(
                        0f to "None (No negative marks)",
                        0.25f to "¼ mark (-0.25 per wrong answer)",
                        0.33333334f to "⅓ mark (-0.33 per wrong answer — Standard PSC)"
                    )
                    markingOptions.forEach { (rate, desc) ->
                        val isSelected = Math.abs(negRate - rate) < 0.01f
                        Surface(
                            onClick = { viewModel.negativeMarkRate.value = rate },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) KeralaGreenLight else Color.Transparent,
                            border = BorderStroke(1.dp, if (isSelected) KeralaGreen else KeralaLine),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) KeralaGreen else Color.Transparent)
                                        .border(1.5.dp, if (isSelected) KeralaGreen else KeralaMuted, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Color.White)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = desc,
                                    fontSize = 13.sp,
                                    color = if (isSelected) KeralaGreenDark else KeralaInk,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5. Shuffle options checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.shuffleOptions.value = !shuffleOpts }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = shuffleOpts,
                        onCheckedChange = { viewModel.shuffleOptions.value = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = KeralaGreen,
                            checkmarkColor = Color.White
                        ),
                        modifier = Modifier.testTag("shuffle_checkbox")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Also shuffle options (A–D) in each question",
                        fontSize = 13.sp,
                        color = KeralaInk,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Start Test Button
                Button(
                    onClick = { viewModel.startTest() },
                    enabled = totalCount > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KasavuGold,
                        contentColor = Color(0xFF241A00)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("start_test_button")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Start Mock Test",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Recent Tests Card
        if (testHistory.isNotEmpty()) {
            val latest = testHistory.first()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(AppScreen.HISTORY) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = KeralaSurface),
                border = BorderStroke(1.dp, KeralaLine)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Last Mock Test Score",
                            fontSize = 12.sp,
                            color = KeralaMuted,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${latest.finalScore} / ${latest.totalQuestions} (${String.format("%.1f", latest.percentage)}%)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = KeralaGreen
                        )
                        Text(
                            text = "${latest.category} • ${latest.correctCount} correct, ${latest.wrongCount} wrong",
                            fontSize = 12.sp,
                            color = KeralaMuted
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "View History",
                        tint = KeralaMuted
                    )
                }
            }
        }
    }
}
