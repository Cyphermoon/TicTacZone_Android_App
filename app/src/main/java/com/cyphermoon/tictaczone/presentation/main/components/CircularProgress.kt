package com.cyphermoon.tictaczone.presentation.main.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CircularProgress(percentage: Float) {
    // Animate the progress value with a linear easing over a duration of 1000 milliseconds
    val animatedProgress = animateFloatAsState(
        label="Circular Progress for Profile Stats",
        targetValue = percentage,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
    )

    // Box composable is used to overlay the progress text on top of the circular progress indicator
    Box(
        contentAlignment = Alignment.Center,
    ) {
        // CircularProgressIndicator is a pre-built composable for creating circular progress indicators
        CircularProgressIndicator(
            progress = animatedProgress.value,
            strokeWidth = 10.dp,
            color = when {
                // Different colors are used based on the progress value
                percentage < 0.2 -> Color.Red
                percentage < 0.4 -> Color.Red.copy(alpha = 0.8f)
                percentage < 0.6 -> Color.Gray
                percentage < 0.8 -> Color.Gray.copy(alpha = 0.8f)
                else -> Color.Green
            },
            modifier = Modifier.size(200.dp)
        )
        // Text composable is used to display the current progress as a percentage
        Text(
            text = "${(animatedProgress.value * 100).toInt()}%",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}