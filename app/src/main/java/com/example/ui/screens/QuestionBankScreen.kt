package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.QuestionEntity
import com.example.parser.QuestionParser
import com.example.ui.MainViewModel
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

@Composable
fun QuestionBankScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val allQuestions by viewModel.allQuestions.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    var showImportJsonDialog by remember { mutableStateOf(false) }
    var questionToEdit by remember { mutableStateOf<QuestionEntity?>(null) }

    val clipboardManager = LocalClipboardManager.current

    val filteredQuestions = remember(allQuestions, searchQuery, selectedCategoryFilter) {
        allQuestions.filter { q ->
            val matchCat = selectedCategoryFilter == "All" || q.category.equals(selectedCategoryFilter, ignoreCase = true)
            val matchSearch = searchQuery.isBlank() ||
                    q.question.contains(searchQuery, ignoreCase = true) ||
                    q.optionA.contains(searchQuery, ignoreCase = true) ||
                    q.optionB.contains(searchQuery, ignoreCase = true) ||
                    q.optionC.contains(searchQuery, ignoreCase = true) ||
                    q.optionD.contains(searchQuery, ignoreCase = true) ||
                    q.explanation.contains(searchQuery, ignoreCase = true)
            matchCat && matchSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Question Bank",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = KeralaGreenDark,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    text = "${allQuestions.size} questions total",
                    fontSize = 13.sp,
                    color = KeralaMuted
                )
            }

            // Load samples if low
            if (allQuestions.size < 10) {
                OutlinedButton(
                    onClick = { viewModel.loadSampleQuestions() },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = KeralaGreenDark),
                    border = BorderStroke(1.dp, KeralaGreen)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Load Samples", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search questions, options or explanations...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = KeralaMuted) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = KeralaMuted)
                    }
                }
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("bank_search_field"),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category filter chips
        val allCats = listOf("All") + categories.filter { it.isNotBlank() }
        val catScrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(catScrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            allCats.forEach { cat ->
                FilterChip(
                    selected = selectedCategoryFilter == cat,
                    onClick = { selectedCategoryFilter = cat },
                    label = { Text(cat, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = KeralaGreenLight,
                        selectedLabelColor = KeralaGreenDark
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedCategoryFilter == cat,
                        borderColor = if (selectedCategoryFilter == cat) KeralaGreen else KeralaLine
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bank Action Bar (Copy JSON, Import JSON, Delete All)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    val json = QuestionParser.toJson(allQuestions)
                    clipboardManager.setText(AnnotatedString(json))
                    viewModel.postNotification("All ${allQuestions.size} questions copied as JSON")
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = KeralaGreenDark),
                border = BorderStroke(1.dp, KeralaLine),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Export JSON", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = { showImportJsonDialog = true },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = KeralaGreenDark),
                border = BorderStroke(1.dp, KeralaLine),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Import JSON", fontSize = 12.sp)
            }

            if (allQuestions.isNotEmpty()) {
                IconButton(
                    onClick = { showDeleteAllDialog = true },
                    modifier = Modifier.testTag("delete_all_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete All",
                        tint = KeralaRed
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Question List
        if (filteredQuestions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No questions match '$searchQuery'" else "No questions in this category",
                        color = KeralaMuted,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredQuestions, key = { it.id }) { q ->
                    BankQuestionCard(
                        question = q,
                        onEdit = { questionToEdit = q },
                        onDelete = { viewModel.deleteQuestion(q) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }

    // Delete All Confirmation Dialog
    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = { Text("Delete All Questions?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove all ${allQuestions.size} questions from the bank? You can re-import or load the default sample questions at any time.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAllDialog = false
                        viewModel.clearAllQuestions()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KeralaRed)
                ) {
                    Text("Delete Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Import JSON Dialog
    if (showImportJsonDialog) {
        var jsonText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showImportJsonDialog = false },
            title = { Text("Import JSON Questions", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Paste a JSON array of questions:",
                        fontSize = 12.sp,
                        color = KeralaMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = jsonText,
                        onValueChange = { jsonText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        placeholder = {
                            Text(
                                "[{\"question\":\"...\",\"options\":[\"A\",\"B\",\"C\",\"D\"],\"answer\":\"B\"}]",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = QuestionParser.parseJson(jsonText)
                        if (parsed.questions.isNotEmpty()) {
                            viewModel.importQuestions(parsed.questions)
                            showImportJsonDialog = false
                        } else {
                            viewModel.postNotification("Could not parse JSON. Please check format.")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KeralaGreen)
                ) {
                    Text("Import")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportJsonDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit Question Dialog
    if (questionToEdit != null) {
        EditQuestionDialog(
            question = questionToEdit!!,
            onDismiss = { questionToEdit = null },
            onSave = { updated ->
                viewModel.updateQuestion(updated)
                questionToEdit = null
            }
        )
    }
}

@Composable
private fun BankQuestionCard(
    question: QuestionEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val letters = listOf("A", "B", "C", "D")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = KeralaSurface),
        border = BorderStroke(1.dp, KeralaLine)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = KeralaGreenLight
                ) {
                    Text(
                        text = question.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = KeralaGreenDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = KeralaGreenDark, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = KeralaRed, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = question.question,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = KeralaInk,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Options summary
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                question.getOptionsList().forEachIndexed { idx, opt ->
                    val isCorrect = idx == question.correctAnswerIndex
                    val letter = letters.getOrElse(idx) { "?" }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isCorrect) KeralaGreenLight else Color.Transparent,
                        border = if (isCorrect) BorderStroke(1.dp, KeralaGreen) else null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$letter)",
                                fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                color = if (isCorrect) KeralaGreenDark else KeralaMuted
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = opt,
                                fontSize = 12.sp,
                                color = if (isCorrect) KeralaGreenDark else KeralaInk,
                                fontWeight = if (isCorrect) FontWeight.SemiBold else FontWeight.Normal,
                                modifier = Modifier.weight(1f)
                            )
                            if (isCorrect) {
                                Icon(Icons.Default.Check, contentDescription = "Correct", tint = KeralaGreen, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            if (question.explanation.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = KasavuGoldContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Exp: ${question.explanation}",
                        fontSize = 11.sp,
                        color = Color(0xFF241A00),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EditQuestionDialog(
    question: QuestionEntity,
    onDismiss: () -> Unit,
    onSave: (QuestionEntity) -> Unit
) {
    var qText by remember { mutableStateOf(question.question) }
    var optA by remember { mutableStateOf(question.optionA) }
    var optB by remember { mutableStateOf(question.optionB) }
    var optC by remember { mutableStateOf(question.optionC) }
    var optD by remember { mutableStateOf(question.optionD) }
    var correctIdx by remember { mutableIntStateOf(question.correctAnswerIndex) }
    var cat by remember { mutableStateOf(question.category) }
    var exp by remember { mutableStateOf(question.explanation) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Question", fontWeight = FontWeight.Bold) },
        text = {
            val scroll = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .verticalScroll(scroll),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = qText,
                    onValueChange = { qText = it },
                    label = { Text("Question") },
                    modifier = Modifier.fillMaxWidth()
                )

                listOf(
                    0 to "Option A",
                    1 to "Option B",
                    2 to "Option C",
                    3 to "Option D"
                ).forEach { (idx, label) ->
                    val value = when (idx) {
                        0 -> optA
                        1 -> optB
                        2 -> optC
                        else -> optD
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = correctIdx == idx,
                            onClick = { correctIdx = idx },
                            colors = RadioButtonDefaults.colors(selectedColor = KeralaGreen)
                        )
                        OutlinedTextField(
                            value = value,
                            onValueChange = {
                                when (idx) {
                                    0 -> optA = it
                                    1 -> optB = it
                                    2 -> optC = it
                                    else -> optD = it
                                }
                            },
                            label = { Text(label) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }

                OutlinedTextField(
                    value = cat,
                    onValueChange = { cat = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = exp,
                    onValueChange = { exp = it },
                    label = { Text("Explanation") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        question.copy(
                            question = qText.trim(),
                            optionA = optA.trim(),
                            optionB = optB.trim(),
                            optionC = optC.trim(),
                            optionD = optD.trim(),
                            correctAnswerIndex = correctIdx,
                            category = if (cat.isBlank()) "General" else cat.trim(),
                            explanation = exp.trim()
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = KeralaGreen)
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
