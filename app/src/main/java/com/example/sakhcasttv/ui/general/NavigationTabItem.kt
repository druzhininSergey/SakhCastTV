package com.example.sakhcasttv.ui.general

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Tab
import androidx.tv.material3.TabDefaults
import androidx.tv.material3.TabRowScope
import androidx.tv.material3.Text

@Composable
fun TabRowScope.NavigationTabItem(
    item: MenuItem,
    isSelected: Boolean,
    enabled: Boolean = true,
    onMenuSelected: ((menuItem: MenuItem) -> Unit)?,
) {
    val mutableInteractionSource = remember { MutableInteractionSource() }
    val isFocused by mutableInteractionSource.collectIsFocusedAsState()

    Tab(
        selected = isSelected,
        enabled = enabled,
        onClick = {
            onMenuSelected?.invoke(item)
        },
        onFocus = {},
        interactionSource = mutableInteractionSource,
        colors = TabDefaults.pillIndicatorTabColors(
            selectedContentColor = MaterialTheme.colorScheme.onSurface,
            focusedContentColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .background(
                color = when {
                    isFocused -> MaterialTheme.colorScheme.inverseSurface
                    isSelected -> Color.DarkGray
                    else -> Color.Transparent
                },
                shape = MaterialTheme.shapes.small,
            )
    ) {
        Text(
            text = item.text,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            color = when {
                isFocused -> Color.Black
                isSelected -> MaterialTheme.colorScheme.onBackground
                else -> MaterialTheme.colorScheme.onBackground
            },
            fontSize = 12.sp,
        )
    }
}

data class MenuItem(
    val id: String,
    val text: String,
    val icon: ImageVector? = null
)