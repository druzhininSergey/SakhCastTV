package com.example.sakhcasttv

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ListItemDefaults
import androidx.tv.material3.MaterialTheme

object Dimens {
    val mainPadding = 16.dp
    val mainPaddingHalf = 8.dp
}
object Colors{
    val errorColor = Color(0xFF4A252B)
    val blueColor = Color(0xFF007AFF)
    val proDaysCountGreenColor = Color(0xFF30D158)
}

object CustomListItemColors {
    @Composable
    fun listItemColors() = ListItemDefaults.colors(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        focusedContainerColor = Color.DarkGray,
        focusedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        pressedContainerColor = Color.DarkGray,
        pressedContentColor = MaterialTheme.colorScheme.onPrimary,
        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        selectedContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        disabledContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
        disabledContentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
        focusedSelectedContainerColor = MaterialTheme.colorScheme.primary,
        focusedSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
        pressedSelectedContainerColor = Color.DarkGray,
        pressedSelectedContentColor = MaterialTheme.colorScheme.onPrimary
    )
}