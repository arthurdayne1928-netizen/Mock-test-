package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parser.QuestionParser
import com.example.ui.MainViewModel
import com.example.ui.theme.KasavuGold
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

private const val SAMPLE_BULK_INPUT = """1. A straight line that touches a circle at only one point is called:
A) Chord
B) Tangent
C) Secant
D) Radius
Answer: B) Tangent
Explanation: A tangent is a line that touches a circle at exactly one point.

2. Capital of Kerala?
A) Kochi
B) Thiruvananthapuram
C) Kozhikode
D) Thrissur
Answer: B
Explanation: Thiruvananthapuram is the capital city of Kerala.

3. Who led the historic Vaikom Satyagraha in 1924?
A) K. Kelappan
B) C.V. Raman Pillai
C) T.K. Madhavan
D) Both A and C
Answer: D
Explanation: T.K. Madhavan initiated the movement and K. Kelappan led the first batch of satyagrahis."""

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddQuestionsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "Add Questions",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = KeralaGreenDark,
                letterSpacing = (-0.3).sp
            )
            Text(
                text = "Paste multiple questions at once or add them one by one.",
                fontSize = 13.sp,
                color = KeralaMuted
            )
        }

        // Sub-tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = KeralaSurface,
            contentColor = KeralaGreenDark,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = KeralaGreen
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Bulk Add", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_bulk_add")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("One by One", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_single_add")
            )
        }

        if (selectedTab == 0) {
            // BULK ADD VIEW
            BulkAddView(viewModel = viewModel, availableCategories = categories)
        } else {
            // ONE BY ONE VIEW
            SingleAddView(viewModel = viewModel, availableCategories = categories)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BulkAddView(
    viewModel: MainViewModel,
    availableCategories: List<String>
) {
    var bulkText by remember { mutableStateOf("") }
    var batchCategory by remember { mutableStateOf("Kerala History") }
    val clipboardManager = LocalClipboardManager.current

    val parseResult = remember(bulkText, batchCategory) {
        if (bulkText.trim().startsWith("[")) {
            QuestionParser.parseJson(bulkText, batchCategory)
        } else {
            QuestionParser.parsePlainText(bulkText, batchCategory)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = KeralaSurface),
        border = BorderStroke(1.dp, KeralaLine)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Paste Questions Text or JSON",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = KeralaInk
                )

                OutlinedButton(
                    onClick = { bulkText = SAMPLE_BULK_INPUT },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = KeralaGreenDark),
                    border = BorderStroke(1.dp, KeralaGreen),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Load Example", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = bulkText,
                onValueChange = { bulkText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .testTag("bulk_input_field"),
                placeholder = {
                    Text(
                        text = "1. Question text here\nA) Option 1\nB) Option 2\nC) Option 3\nD) Option 4\nAnswer: B\nExplanation: Optional explanation",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = KeralaMuted
                    )
                },
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Batch Category Tag
            Text(
                text = "Category for these questions:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = KeralaInk
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = batchCategory,
                onValueChange = { batchCategory = it },
                placeholder = { Text("e.g. Kerala History, Maths, Malayalam") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            // Category Suggestion chips
            if (availableCategories.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    availableCategories.take(6).forEach { cat ->
                        FilterChip(
                            selected = batchCategory == cat,
                            onClick = { batchCategory = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = KeralaGreenLight,
                                selectedLabelColor = KeralaGreenDark
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Parse Summary Status
            if (bulkText.isNotBlank()) {
                val qCount = parseResult.questions.size
                val errCount = parseResult.errors.size

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (qCount > 0) KeralaGreenLight else KeralaRedLight,
                    border = BorderStroke(1.dp, if (qCount > 0) KeralaGreen else KeralaRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (qCount > 0) Icons.Default.Check else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (qCount > 0) KeralaGreen else KeralaRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (qCount > 0) "Parsed $qCount valid question(s)${if (errCount > 0) " ($errCount skipped due to formatting)" else ""}" else "No valid questions detected yet. Ensure A), B), C), D) and Answer: lines are included.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (qCount > 0) KeralaGreenDark else KeralaRed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Actions row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        if (parseResult.questions.isNotEmpty()) {
                            viewModel.importQuestions(parseResult.questions)
                            bulkText = ""
                        }
                    },
                    enabled = parseResult.questions.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KeralaGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("import_parsed_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Import (${parseResult.questions.size})")
                }

                OutlinedButton(
                    onClick = {
                        val json = QuestionParser.toJson(parseResult.questions)
                        clipboardManager.setText(AnnotatedString(json))
                        viewModel.postNotification("JSON copied to clipboard")
                    },
                    enabled = parseResult.questions.isNotEmpty(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = KeralaGreenDark)
                ) {
                    Text("Copy JSON")
                }

                OutlinedButton(
                    onClick = { bulkText = "" },
                    enabled = bulkText.isNotEmpty(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = KeralaRed)
                ) {
                    Text("Clear")
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SingleAddView(
    viewModel: MainViewModel,
    availableCategories: List<String>
) {
    var questionText by remember { mutableStateOf("") }
    var optA by remember { mutableStateOf("") }
    var optB by remember { mutableStateOf("") }
    var optC by remember { mutableStateOf("") }
    var optD by remember { mutableStateOf("") }
    var correctIndex by remember { mutableIntStateOf(0) }
    var category by remember { mutableStateOf("Kerala History") }
    var explanation by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = KeralaSurface),
        border = BorderStroke(1.dp, KeralaLine)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "New Question",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = KeralaInk
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Question text
            Text(text = "Question", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = KeralaInk)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = questionText,
                onValueChange = { questionText = it },
                placeholder = { Text("Type question text here...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("single_question_input"),
                shape = RoundedCornerShape(8.dp),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Options with radio buttons
            Text(
                text = "Options — tick the radio button for the correct one",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = KeralaInk
            )
            Spacer(modifier = Modifier.height(6.dp))

            listOf(
                0 to "Option A",
                1 to "Option B",
                2 to "Option C",
                3 to "Option D"
            ).forEach { (idx, label) ->
                val currentText = when (idx) {
                    0 -> optA
                    1 -> optB
                    2 -> optC
                    else -> optD
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = correctIndex == idx,
                        onClick = { correctIndex = idx },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = KeralaGreen,
                            unselectedColor = KeralaMuted
                        ),
                        modifier = Modifier.testTag("radio_option_$idx")
                    )

                    OutlinedTextField(
                        value = currentText,
                        onValueChange = {
                            when (idx) {
                                0 -> optA = it
                                1 -> optB = it
                                2 -> optC = it
                                else -> optD = it
                            }
                        },
                        placeholder = { Text(label) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_option_$idx"),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category
            Text(text = "Category", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = KeralaInk)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                placeholder = { Text("e.g. Kerala History, Maths") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            // Category Suggestion chips
            if (availableCategories.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    availableCategories.take(6).forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = KeralaGreenLight,
                                selectedLabelColor = KeralaGreenDark
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Explanation
            Text(text = "Explanation (Optional)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = KeralaInk)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = explanation,
                onValueChange = { explanation = it },
                placeholder = { Text("Notes shown to candidate in result review...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Save Button
            val canSave = questionText.isNotBlank() && optA.isNotBlank() && optB.isNotBlank() && optC.isNotBlank() && optD.isNotBlank()

            Button(
                onClick = {
                    if (canSave) {
                        viewModel.insertQuestion(
                            question = questionText,
                            optA = optA,
                            optB = optB,
                            optC = optC,
                            optD = optD,
                            correctIdx = correctIndex,
                            category = category,
                            explanation = explanation
                        )
                        // Reset fields
                        questionText = ""
                        optA = ""
                        optB = ""
                        optC = ""
                        optD = ""
                        explanation = ""
                    }
                },
                enabled = canSave,
                colors = ButtonDefaults.buttonColors(
                    containerColor = KeralaGreen,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_single_question_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Question to Bank", fontWeight = FontWeight.Bold)
            }
        }
    }
}
