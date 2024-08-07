package com.example.sakhcasttv.ui.movie_player.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import com.example.sakhcasttv.ui.general.handleDPadKeyEvents
import com.example.sakhcasttv.ui.general.ifElse
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun RowScope.VideoPlayerControllerIndicator(
    progress: Float,
    onSeek: (seekProgress: Float) -> Unit,
    state: VideoPlayerState,
    contentDuration: Duration
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isSelected by remember { mutableStateOf(false) }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val color by rememberUpdatedState(
        newValue = if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurface
    )
    val animatedIndicatorHeight by animateDpAsState(
        targetValue = 4.dp.times((if (isFocused) 2.5f else 1f)), label = ""
    )
    var seekProgress by remember { mutableFloatStateOf(0f) }

    val seekTime = remember(seekProgress, contentDuration) {
        val seekMillis = (contentDuration.inWholeMilliseconds * seekProgress).toLong()
        seekMillis.milliseconds.toComponents { hours, minutes, seconds, _ ->
            if (hours > 0) {
                "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
            } else {
                "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
            }
        }
    }

    LaunchedEffect(isSelected) {
        if (isSelected) {
            state.showControls(seconds = Int.MAX_VALUE)
        } else {
            state.showControls()
        }
    }

    val handleSeekEventModifier = Modifier.handleDPadKeyEvents(
        onEnter = {
            isSelected = !isSelected
            onSeek(seekProgress)
        },
        onLeft = {
            seekProgress = (seekProgress - 0.01f).coerceAtLeast(0f)
        },
        onRight = {
            seekProgress = (seekProgress + 0.01f).coerceAtMost(1f)
        },
    )

    val handleDpadCenterClickModifier = Modifier.handleDPadKeyEvents(
        onEnter = {
            seekProgress = progress
            isSelected = !isSelected
        }
    )
    AnimatedVisibility(visible = isSelected) {
        Text(
            text = seekTime,
            color = color,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )
    }

    LinearProgressIndicator(
        progress = { if (isSelected) seekProgress else progress },
        modifier = Modifier
            .weight(1f)
            .height(animatedIndicatorHeight)
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(10.dp))
            .ifElse(
                condition = isSelected,
                ifTrueModifier = handleSeekEventModifier,
                ifFalseModifier = handleDpadCenterClickModifier
            )
            .focusable(interactionSource = interactionSource),
        color = color,
        trackColor = color.copy(alpha = 0.24f),
    )
}