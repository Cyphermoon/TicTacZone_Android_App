package com.cyphermoon.tictaczone.presentation.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

@Composable
fun Logo(name: String?) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {

        Button(
            onClick = { /* Logic */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)

        ) {
            Row(

                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.wrapContentSize()
            ) {
                // This is where  back arrow icon would go, if needed
                // Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")

                // Concactenated text with different styles
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.DarkGray
                            )
                        ) {
                            append("TicTac ")
                        }
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.Red
                            )
                        ) {
                            append("Zone")
                        }
                    }
                )
            }
        }

        if(name != null){
            Text(
                text = name, // replace with actual player name
                color = Color.Gray,
                fontSize = 12.sp,
                textDecoration = TextDecoration.Underline,

                )
        }
    }
}