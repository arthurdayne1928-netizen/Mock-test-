package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KasavuGold
import com.example.ui.theme.KasavuGoldLight
import com.example.ui.theme.KeralaInk
import com.example.ui.theme.KeralaRed
import com.example.ui.theme.KeralaRedLight

@Composable
fun TimerDisplay(
    remainingSeconds: Int,
    isTimerRunning: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isTimerRunning && remainingSeconds <= 0) {
        Surface(
            modifier = modifier.testTag("untimed_badge"),
            shape = RoundedCornerShape(8.dp),
            color = KasavuGoldLight,
            border = BorderStroke(1.dp, KasavuGold)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.HourglassBottom,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF241A00)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Untimed",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF241A00)
                )
            }
        }
        return
    }

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)
    val isLowTime = remainingSeconds in 1..120

    val bgColor by animateColorAsState(
        targetValue = if (isLowTime) KeralaRedLight else KasavuGoldLight,
        label = "timer_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isLowTime) KeralaRed else KasavuGold,
        label = "timer_border"
    )
    val textColor by animateColorAsState(
        targetValue = if (isLowTime) KeralaRed else Color(0xFF241A00),
        label = "timer_text"
    )

    Surface(
        modifier = modifier.testTag("timer_display"),
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = "Timer",
                modifier = Modifier.size(16.dp),
                tint = textColor
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = timeFormatted,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = textColor
            )
        }
    }
}
