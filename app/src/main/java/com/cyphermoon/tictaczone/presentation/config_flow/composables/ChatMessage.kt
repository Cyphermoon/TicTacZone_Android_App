package com.cyphermoon.tictaczone.presentation.config_flow.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cyphermoon.tictaczone.presentation.main.components.UserAvatar
import com.cyphermoon.tictaczone.ui.theme.LightSecondary

@Composable
fun ChatMessage(
    text: String,
    id: String,
    name: String,
    right: Boolean
) {
    val backgroundColor = if (right) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary
    val textColor = if (right) Color.White else Color.Black

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (right) Arrangement.End else Arrangement.Start
    ) {
        if (!right) {
            UserAvatar(id = id, name = name, imageUrl = null, modifier = Modifier.requiredSize(15.dp))
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            ),
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text(
                text = text,
                color = textColor,
                modifier = Modifier
                    .padding(13.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChatMessage() {
    ChatMessage(
        text = "Sample Message",
        id = "sampleId",
        name = "Sample Name",
        right = false
    )
}

