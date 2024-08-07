package com.example.sakhcasttv.ui.movie_player.components

import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.media3.common.util.UnstableApi
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.example.sakhcasttv.ui.movie_player.MoviePlayerViewModel

@OptIn(UnstableApi::class)
@Composable
fun AudioTrackSelectionDialog(
    audioTracks: List<MoviePlayerViewModel.AudioTrackReceived>,
    currentAudioTrack: MoviePlayerViewModel.AudioTrackReceived?,
    onAudioTrackSelected: (MoviePlayerViewModel.AudioTrackReceived) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Выберите аудиодорожку", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                audioTracks.forEach { audioTrack ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAudioTrackSelected(audioTrack) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = audioTrack.name,
                            modifier = Modifier.weight(1f)
                        )
                        if (audioTrack == currentAudioTrack) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Выбрано"
                            )
                        }
                    }
                }
            }
        }
    }
}