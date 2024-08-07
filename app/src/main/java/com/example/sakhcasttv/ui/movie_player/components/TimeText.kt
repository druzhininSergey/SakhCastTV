package com.example.sakhcasttv.ui.movie_player.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.tv.material3.Text

@Composable
fun TimeText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        fontFamily = FontFamily.Monospace,  // Шрифт с фиксированной шириной
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Visible
    )
}
