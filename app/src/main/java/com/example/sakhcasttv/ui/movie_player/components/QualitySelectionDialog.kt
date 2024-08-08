package com.example.sakhcasttv.ui.movie_player.components

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.media3.common.util.UnstableApi
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.example.sakhcasttv.ui.movie_player.MoviePlayerViewModel

@OptIn(UnstableApi::class)
@Composable
fun QualitySelectionDialog(
    qualities: List<MoviePlayerViewModel.VideoQuality>,
    currentQuality: MoviePlayerViewModel.VideoQuality?,
    onQualitySelected: (MoviePlayerViewModel.VideoQuality) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Выберите качество", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn {
                    items(qualities) { quality ->
                        ListItem(
                            selected = quality == currentQuality,
                            onClick = { onQualitySelected(quality) },
                            headlineContent = {
                                Text(
                                    text = if (quality.resolution == "Авто") "Авто"
                                    else "${quality.resolution} (${quality.bandwidth / 1000} Kbps)"
                                )
                            },
                            trailingContent = if (quality == currentQuality) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Выбрано"
                                    )
                                }
                            } else null
                        )
                    }
                }
            }
        }
    }
}