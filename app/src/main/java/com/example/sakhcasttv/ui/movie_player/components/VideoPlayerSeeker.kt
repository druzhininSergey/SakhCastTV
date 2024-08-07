package com.example.sakhcasttv.ui.movie_player.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.sakhcasttv.R
import com.example.sakhcasttv.ui.general.StringConstants
import kotlin.time.Duration

@Composable
fun VideoPlayerSeeker(
    focusRequester: FocusRequester,
    state: VideoPlayerState,
    isPlaying: Boolean,
    onPlayPauseToggle: () -> Unit,
    onSeek: (Float) -> Unit,
    contentProgress: Duration,
    contentDuration: Duration
) {
    val contentProgressString =
        contentProgress.toComponents { h, m, s, _ ->
            if (h > 0) {
                "$h:${m.padStartWith0()}:${s.padStartWith0()}"
            } else {
                "${m.padStartWith0()}:${s.padStartWith0()}"
            }
        }
    val contentDurationString =
        contentDuration.toComponents { h, m, s, _ ->
            if (h > 0) {
                "$h:${m.padStartWith0()}:${s.padStartWith0()}"
            } else {
                "${m.padStartWith0()}:${s.padStartWith0()}"
            }
        }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        VideoPlayerControlsIcon(
            modifier = Modifier.focusRequester(focusRequester),
            icon = if (!isPlaying) Icons.Default.PlayArrow else ImageVector.vectorResource(id = R.drawable.ic_pause),
            onClick = { onPlayPauseToggle() },
            state = state,
            isPlaying = isPlaying,
            contentDescription = StringConstants
                .Composable
                .VideoPlayerControlPlayPauseButton
        )
        TimeText(text = contentProgressString, modifier = Modifier.width(80.dp))
        VideoPlayerControllerIndicator(
            progress = (contentProgress / contentDuration).toFloat(),
            onSeek = onSeek,
            state = state
        )
        TimeText(text = contentDurationString, modifier = Modifier.width(80.dp))
    }
}

private fun Number.padStartWith0() = this.toString().padStart(2, '0')
