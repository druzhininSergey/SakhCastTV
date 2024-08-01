package com.example.sakhcasttv.ui.movie_player.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import com.example.sakhcasttv.R
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerPulse.Type.NONE
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlin.time.Duration.Companion.seconds

object VideoPlayerPulse {
    enum class Type { FORWARD, BACK, NONE }
}

@Composable
fun VideoPlayerPulse(
    state: VideoPlayerPulseState = rememberVideoPlayerPulseState()
) {
    val icon = when (state.type) {
        VideoPlayerPulse.Type.FORWARD -> painterResource(id = R.drawable.ic_forward_10)
        VideoPlayerPulse.Type.BACK -> painterResource(id = R.drawable.ic_replay_5)
        NONE -> null
    }
    if (icon != null) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                .size(88.dp)
                .wrapContentSize()
                .size(48.dp)
        )
    }
}

class VideoPlayerPulseState {
    private var _type by mutableStateOf(NONE)
    val type: VideoPlayerPulse.Type get() = _type

    private val channel = Channel<Unit>(Channel.CONFLATED)

    @OptIn(FlowPreview::class)
    suspend fun observe() {
        channel.consumeAsFlow()
            .debounce(2.seconds)
            .collect { _type = NONE }
    }

    fun setType(type: VideoPlayerPulse.Type) {
        _type = type
        channel.trySend(Unit)
    }
}

@Composable
fun rememberVideoPlayerPulseState() =
    remember { VideoPlayerPulseState() }.also { LaunchedEffect(it) { it.observe() } }
