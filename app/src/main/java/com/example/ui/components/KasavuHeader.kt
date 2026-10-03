package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Badge
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.theme.KasavuGold
import com.example.ui.theme.KasavuGoldDark
import com.example.ui.theme.KasavuGoldLight
import com.example.ui.theme.KeralaGreenDark
import com.example.ui.theme.KeralaGreenLight

@Composable
fun KasavuHeader(
    currentScreen: AppScreen,
    questionBankCount: Int,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(KeralaGreenDark)
    ) {
        // Top Brand Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Kerala PSC Mock Test",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.2).sp
                )
                Text(
                    text = "Practice. Shuffle. Repeat.",
                    color = Color.White.copy(alpha = 0.78f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            IconButton(
                onClick = { onNavigate(AppScreen.HISTORY) },
                modifier = Modifier.testTag("history_nav_button")
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Test History",
                    tint = if (currentScreen == AppScreen.HISTORY) KasavuGoldLight else Color.White
                )
            }
        }

        // Navigation Tabs Bar
        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTabButton(
                title = "Mock Test",
                isSelected = currentScreen == AppScreen.SETUP || currentScreen == AppScreen.QUIZ || currentScreen == AppScreen.RESULT,
                onClick = { onNavigate(AppScreen.SETUP) },
                testTag = "nav_test"
            )

            NavTabButton(
                title = "Add Questions",
                isSelected = currentScreen == AppScreen.ADD_QUESTIONS,
                onClick = { onNavigate(AppScreen.ADD_QUESTIONS) },
                testTag = "nav_add"
            )

            NavTabButton(
                title = "Question Bank",
                badgeCount = questionBankCount,
                isSelected = currentScreen == AppScreen.QUESTION_BANK,
                onClick = { onNavigate(AppScreen.QUESTION_BANK) },
                testTag = "nav_bank"
            )
        }

        // Kerala Kasavu Gold Stripe Pattern at the bottom of header
        KasavuBorderStripe()
    }
}

@Composable
private fun NavTabButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeCount: Int? = null,
    testTag: String
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) Color.White else Color.Transparent,
        modifier = modifier.testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) KeralaGreenDark else Color.White.copy(alpha = 0.85f)
            )

            if (badgeCount != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isSelected) KeralaGreenDark else KasavuGold)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "$badgeCount",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) KasavuGoldLight else Color(0xFF241A00)
                    )
                }
            }
        }
    }
}

@Composable
fun KasavuBorderStripe(modifier: Modifier = Modifier) {
    // Beautiful alternating Kerala gold & deep green Kasavu woven ribbon pattern
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        KasavuGold, KasavuGold,
                        KeralaGreenDark, KeralaGreenDark,
                        KasavuGold, KasavuGold,
                        KeralaGreenDark,
                        KasavuGoldDark, KasavuGold,
                        KeralaGreenDark,
                        KasavuGold
                    )
                )
            )
    )
}
