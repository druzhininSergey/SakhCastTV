package com.example.sakhcasttv.ui.general

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.sakhcasttv.CustomListItemColors

@Composable
fun CustomListItem(
    text: String? = null,
    onClick: (() -> Unit)? = null,
    isSeries: Boolean,
    onClickWithParam: ((String, String) -> Unit)? = null,
    category: Map.Entry<String, String>? = null,
) {
    ListItem(
        colors = CustomListItemColors.listItemColors(),
        shape = ListItemDefaults.shape(RoundedCornerShape(10.dp)),
        selected = false,
        onClick = {
            if (isSeries) {
                if (onClick != null) onClick()
                else onClickWithParam?.let {
                    it("${category?.value}.favorite", category?.key ?: "")
                }
            } else {
                if (onClick != null) onClick()
                else onClickWithParam?.let {
                    it(category?.key ?: "", category?.value ?: "")
                }
            }
        },
        headlineContent = {
            Text(
                text = text ?: category?.key ?: "",
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 20.dp)
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(end = 20.dp)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(MaterialTheme.colorScheme.background)
            .clip(RoundedCornerShape(10.dp))
    )
}