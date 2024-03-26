package com.cyphermoon.tictaczone.presentation.main.components

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage


@Composable
fun UserAvatar(imageUrl: String?, name: String, id: String, modifier: Modifier = Modifier) {
    val seed = name + id
   val avatarUrl = "https://api.dicebear.com/8.x/pixel-art/png?seed=${seed}&hair=long01,long02,long03,long04,long05"

    // Use the CoilImage composable to load the image from the URL
    val painter = imageUrl ?: avatarUrl


    AsyncImage(
        model = painter,
        contentDescription = name,
        modifier = modifier
            .size(200.dp)
            .clip(CircleShape)
    )
}