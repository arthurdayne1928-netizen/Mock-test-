package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.TestResultEntity
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.KasavuGold
import com.example.ui.theme.KeralaGreen
import com.example.ui.theme.KeralaGreenDark
import com.example.ui.theme.KeralaGreenLight
import com.example.ui.theme.KeralaInk
import com.example.ui.theme.KeralaLine
import com.example.ui.theme.KeralaMuted
import com.example.ui.theme.KeralaRed
import com.example.ui.theme.KeralaSurface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val results by viewModel.testResults.collectAsStateWithLifecycle()
    var showClearConfirm by remember { mutableStateOf(false) }

    BackHandler {
        viewModel.navigateTo(AppScreen.SETUP)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateTo(AppScreen.SETUP) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = KeralaGreenDark
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Mock Test History",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = KeralaGreenDark
                    )
                    Text(
                        text = "${results.size} past attempts recorded",
                        fontSize = 12.sp,
                        color = KeralaMuted
                    )
                }
            }

            if (results.isNotEmpty()) {
                IconButton(
                    onClick = { showClearConfirm = true },
                    modifier = Modifier.testTag("clear_history_button")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear History", tint = KeralaRed)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (results.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No mock tests taken yet.",
                        fontSize = 15.sp,
                        color = KeralaMuted,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.SETUP) },
                        colors = ButtonDefaults.buttonColors(containerColor = KeralaGreen)
                    ) {
                        Text("Start a Mock Test")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(results, key = { it.id }) { result ->
                    HistoryItemCard(result)
                }
                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Clear Test History?") },
            text = { Text("Are you sure you want to delete all past mock test results?") },
            confirmButton = {
                Button(
                    onClick = {
                        showClearConfirm = false
                        viewModel.clearHistory()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KeralaRed)
                ) {
                    Text("Clear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun HistoryItemCard(result: TestResultEntity) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val dateStr = remember(result.timestamp) { dateFormat.format(Date(result.timestamp)) }

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
                        text = result.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = KeralaGreenDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = dateStr,
                    fontSize = 11.sp,
                    color = KeralaMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Score: ${String.format("%.2f", result.finalScore)} / ${result.totalQuestions}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = KeralaGreen
                    )
                    Text(
                        text = "${String.format("%.1f", result.percentage)}% score • ${result.correctCount} correct • ${result.wrongCount} wrong • ${result.skippedCount} skipped",
                        fontSize = 12.sp,
                        color = KeralaMuted
                    )
                }

                val mins = result.timeTakenSeconds / 60
                val secs = result.timeTakenSeconds % 60
                val timeStr = if (result.timeTakenSeconds > 0) "${mins}m ${secs}s" else "Untimed"

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF4F7F4),
                    border = BorderStroke(1.dp, KeralaLine)
                ) {
                    Text(
                        text = timeStr,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = KeralaInk,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
