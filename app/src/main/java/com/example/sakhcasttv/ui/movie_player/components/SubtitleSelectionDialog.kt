package com.example.sakhcasttv.ui.movie_player.components

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.media3.common.util.UnstableApi
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.example.sakhcasttv.ui.general.handleDPadKeyEvents
import com.example.sakhcasttv.ui.movie_player.MoviePlayerViewModel

@OptIn(UnstableApi::class)
@Composable
fun SubtitleSelectionDialog(
    subtitles: List<MoviePlayerViewModel.Subtitle>,
    currentSubtitle: MoviePlayerViewModel.Subtitle?,
    onSubtitleSelected: (MoviePlayerViewModel.Subtitle) -> Unit,
    onDismiss: () -> Unit,
    onNavigate: (Int) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .handleDPadKeyEvents(
                    onLeft = { onNavigate(-1) },
                    onRight = { onNavigate(1) },
                    onUp = null,
                    onDown = null,
                    onEnter = null,
                    onBack = null
                )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                        contentDescription = null
                    )
                    Text("Выберите субтитры", style = MaterialTheme.typography.bodyMedium)
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn {
                    items(subtitles) { subtitle ->
                        ListItem(
                            selected = subtitle == currentSubtitle,
                            onClick = { onSubtitleSelected(subtitle) },
                            headlineContent = {
                                Text(subtitle.name)
                            },
                            trailingContent = if (subtitle == currentSubtitle) {
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