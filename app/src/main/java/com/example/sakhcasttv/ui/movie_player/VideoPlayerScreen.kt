package com.example.sakhcasttv.ui.movie_player

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import com.example.sakhcasttv.R
import com.example.sakhcasttv.ui.general.StringConstants
import com.example.sakhcasttv.ui.general.handleDPadKeyEvents
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerControlsIcon
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerMainFrame
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerMediaTitle
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerMediaTitleType
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerOverlay
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerPulse
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerPulseState
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerSeeker
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerState
import com.example.sakhcasttv.ui.movie_player.components.rememberVideoPlayerPulseState
import com.example.sakhcasttv.ui.movie_player.components.rememberVideoPlayerState
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun VideoPlayerScreen(
    hls: String,
    title: String,
    position: Int,
    movieAlphaId: String,
    onBackPressed: () -> Unit,
    videoPlayerViewModel: VideoPlayerViewModel = hiltViewModel()
) {
    val movieWatchState = videoPlayerViewModel.movieWatchState.collectAsStateWithLifecycle()
//    val isPlayerPlaying by videoPlayerViewModel.isPlayerPlaying.collectAsStateWithLifecycle()
//    val contentCurrentPosition by videoPlayerViewModel.contentCurrentPosition.collectAsStateWithLifecycle()

    LaunchedEffect(hls) {
        videoPlayerViewModel.preparePlayer(hls)
    }

//    DisposableEffect(Unit) {
//        onDispose {
//            videoPlayerViewModel.releasePlayer()
//            Log.i("!!!", "videoPlayerViewModel.releasePlayer() in VIEW")
//        }
//    }

    VideoPlayerScreenContent(
        hls = hls,
        title = title,
        position = position,
        movieAlphaId = movieAlphaId,
//        isPlaying = isPlayerPlaying,
//        contentCurrentPosition = contentCurrentPosition,
        player = videoPlayerViewModel.player,

        onBackPressed = onBackPressed,
        onPlayPauseToggle = videoPlayerViewModel::onPlayPauseToggle,
        onSeek = videoPlayerViewModel::onSeek,

        )
}

@Composable
fun VideoPlayerScreenContent(
    hls: String,
    title: String,
    position: Int,
    movieAlphaId: String,
//    isPlaying: Boolean,
//    contentCurrentPosition: Long,
    player: Player,

    onBackPressed: () -> Unit,
    onPlayPauseToggle: () -> Unit,
    onSeek: (Float) -> Unit,

    ) {
    val context = LocalContext.current
    val videoPlayerState = rememberVideoPlayerState(hideSeconds = 4)
    Log.i("!!!", "hls = $hls")

    // TODO: Move to ViewModel for better reuse
//    val exoPlayer = rememberExoPlayer(context)
//    LaunchedEffect(exoPlayer, hls) {
//        val mediaItem = MediaItem.fromUri(hls)
//        exoPlayer.setMediaItem(mediaItem)
//        exoPlayer.prepare()
//    }

// TODO: Если стейты в ViewModel не будут отрабатывать вернуть во view как было

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    var contentCurrentPosition by remember { mutableLongStateOf(0L) }
    var isPlaying: Boolean by remember { mutableStateOf(player.isPlaying) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(300)
            contentCurrentPosition = player.currentPosition
            isPlaying = player.isPlaying
        }
    }

    BackHandler(onBack = onBackPressed)

    val pulseState = rememberVideoPlayerPulseState()

    Box(
        Modifier
            .dPadEvents(
                player,
                videoPlayerState,
                pulseState
            )
            .focusable()
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            factory = {
                PlayerView(context).apply { useController = false }
            },
            update = { it.player = player },
            onRelease = { player.release() }
        )

        val focusRequester = remember { FocusRequester() }
        VideoPlayerOverlay(
            modifier = Modifier.align(Alignment.BottomCenter),
            focusRequester = focusRequester,
            state = videoPlayerState,
            isPlaying = isPlaying,
            centerButton = { VideoPlayerPulse(pulseState) },
            subtitles = { /* TODO Implement subtitles */ },
            controls = {
                VideoPlayerControls(
                    isPlaying = isPlaying,
                    contentCurrentPosition = contentCurrentPosition,
                    player = player,
                    state = videoPlayerState,
                    focusRequester = focusRequester,
                    onPlayPauseToggle = onPlayPauseToggle,
                    onSeek = onSeek,

                    )
            }
        )
    }
}

@Composable
fun VideoPlayerControls(
    isPlaying: Boolean,
    contentCurrentPosition: Long,
    player: Player,
    state: VideoPlayerState,
    focusRequester: FocusRequester,
    onPlayPauseToggle: () -> Unit,
    onSeek: (Float) -> Unit,

    ) {
    VideoPlayerMainFrame(
        mediaTitle = {
            VideoPlayerMediaTitle(
                title = "Название",
                secondaryText = "2024",
                tertiaryText = "Director",
                type = VideoPlayerMediaTitleType.DEFAULT
            )
        },
        mediaActions = {
            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                VideoPlayerControlsIcon(
                    icon = ImageVector.vectorResource(id = R.drawable.ic_auto_awesome_motion),
                    state = state,
                    isPlaying = isPlaying,
                    contentDescription = StringConstants
                        .Composable
                        .VideoPlayerControlPlaylistButton
                )
                VideoPlayerControlsIcon(
                    modifier = Modifier.padding(start = 12.dp),
                    icon = ImageVector.vectorResource(id = R.drawable.ic_cc),
                    state = state,
                    isPlaying = isPlaying,
                    contentDescription = StringConstants
                        .Composable
                        .VideoPlayerControlClosedCaptionsButton
                )
                VideoPlayerControlsIcon(
                    modifier = Modifier.padding(start = 12.dp),
                    icon = ImageVector.vectorResource(id = R.drawable.ic_audio_list),
                    state = state,
                    isPlaying = isPlaying,
                    contentDescription = StringConstants
                        .Composable
                        .VideoPlayerControlClosedCaptionsButton
                )
                VideoPlayerControlsIcon(
                    modifier = Modifier.padding(start = 12.dp),
                    icon = Icons.Default.Settings,
                    state = state,
                    isPlaying = isPlaying,
                    contentDescription = StringConstants
                        .Composable
                        .VideoPlayerControlSettingsButton
                )
            }
        },
        seeker = {
            VideoPlayerSeeker(
                focusRequester = focusRequester,
                state = state,
                isPlaying = isPlaying,
                onPlayPauseToggle = onPlayPauseToggle,
                onSeek = onSeek,
                contentProgress = contentCurrentPosition.milliseconds,
                contentDuration = player.duration.milliseconds
            )
        },
        more = null
    )
}

fun Modifier.dPadEvents(
    player: Player,
    videoPlayerState: VideoPlayerState,
    pulseState: VideoPlayerPulseState
): Modifier = this.handleDPadKeyEvents(
    onLeft = {
        if (!videoPlayerState.controlsVisible) {
            player.seekBack()
            pulseState.setType(VideoPlayerPulse.Type.BACK)
        }
    },
    onRight = {
        if (!videoPlayerState.controlsVisible) {
            player.seekForward()
            pulseState.setType(VideoPlayerPulse.Type.FORWARD)
        }
    },
    onUp = { videoPlayerState.showControls() },
    onDown = { videoPlayerState.showControls() },
    onEnter = {
        player.pause()
        videoPlayerState.showControls()
    }
)
