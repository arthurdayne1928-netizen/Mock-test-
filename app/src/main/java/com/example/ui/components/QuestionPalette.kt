package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.QuizItem
import com.example.ui.theme.KasavuGold
import com.example.ui.theme.KasavuGoldLight
import com.example.ui.theme.KeralaGreen
import com.example.ui.theme.KeralaInk
import com.example.ui.theme.KeralaLine
import com.example.ui.theme.KeralaMuted
import com.example.ui.theme.KeralaSurface

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuestionPalette(
    items: List<QuizItem>,
    currentIndex: Int,
    onSelectQuestion: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = KeralaSurface),
        border = BorderStroke(1.dp, KeralaLine)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Question Palette",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = KeralaInk
                )
                Text(
                    text = "${items.count { it.isAnswered }} / ${items.size} Answered",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = KeralaMuted
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Palette buttons grid
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items.forEachIndexed { index, item ->
                    val isCurrent = index == currentIndex
                    val isAnswered = item.isAnswered
                    val isFlagged = item.isMarkedForReview

                    val bgColor = when {
                        isAnswered -> KeralaGreen
                        else -> Color.White
                    }

                    val textColor = when {
                        isAnswered -> Color.White
                        else -> KeralaInk
                    }

                    val borderStroke = when {
                        isCurrent -> BorderStroke(2.5.dp, KeralaInk)
                        isFlagged -> BorderStroke(2.dp, KasavuGold)
                        else -> BorderStroke(1.dp, KeralaLine)
                    }

                    Surface(
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("palette_item_$index"),
                        shape = RoundedCornerShape(8.dp),
                        color = bgColor,
                        border = borderStroke,
                        onClick = { onSelectQuestion(index) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${index + 1}",
                                fontSize = 13.sp,
                                fontWeight = if (isCurrent || isAnswered) FontWeight.Bold else FontWeight.Medium,
                                color = textColor
                            )

                            if (isFlagged) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(2.dp)
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(KasavuGold)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(
                    color = KeralaGreen,
                    border = null,
                    label = "Answered"
                )
                LegendItem(
                    color = Color.White,
                    border = BorderStroke(1.5.dp, KasavuGold),
                    label = "Review"
                )
                LegendItem(
                    color = Color.White,
                    border = BorderStroke(1.dp, KeralaLine),
                    label = "Pending"
                )
            }
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    border: BorderStroke?,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(14.dp),
            shape = RoundedCornerShape(3.dp),
            color = color,
            border = border
        ) {}
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = KeralaMuted,
            fontWeight = FontWeight.Medium
        )
    }
}
