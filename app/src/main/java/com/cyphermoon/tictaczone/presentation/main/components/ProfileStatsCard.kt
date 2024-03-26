package com.cyphermoon.tictaczone.presentation.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun ProfileStatsCard(matches: Int, name: String, win: Int, loss: Int, handleChallenge: (() -> Unit)?, online: Boolean, id:String) {
  var percentage = (win.toDouble() / matches.toDouble() * 100).roundToInt()

  if (percentage.toDouble().isNaN()){
    percentage = 0
  }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .clip(RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                UserAvatar(imageUrl = null, name = name, id = id)
                Text(text = name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            CircularProgress(percentage = percentage.toFloat() / 100)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatsInfo(title = "Matches", value = matches.toString())
            StatsInfo(title = "Wins", value = win.toString())
            StatsInfo(title = "Loss", value = loss.toString())
        }
        if (handleChallenge != null) {
            Button(
                onClick = handleChallenge,
                enabled = online,
                modifier = Modifier.fillMaxWidth(),
                colors= ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text(text = "Challenge")
            }
        }
    }
}

@Composable
fun StatsInfo(title: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = title, fontWeight = FontWeight.Medium, fontSize = 18.sp)
        Text(text = value, fontSize = 14.sp)
    }
}

@Preview(showBackground=true)
@Composable
fun ProfileStatsCardPreview() {
    ProfileStatsCard(
        matches = 10,
        name = "John",
        win = 5,
        loss = 4,
        handleChallenge = null,
        online = true,
        id = "adamq"
    )
}



