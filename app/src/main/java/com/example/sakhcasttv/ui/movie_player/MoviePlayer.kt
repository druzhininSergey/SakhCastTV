package com.example.sakhcasttv.ui.movie_player

import android.app.Activity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import com.example.sakhcasttv.R
import com.example.sakhcasttv.ui.general.handleDPadKeyEvents
import com.example.sakhcasttv.ui.movie_player.components.AudioTrackSelectionDialog
import com.example.sakhcasttv.ui.movie_player.components.QualitySelectionDialog
import com.example.sakhcasttv.ui.movie_player.components.SubtitleSelectionDialog
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerControlsIcon
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerMainFrame
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerMediaTitle
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerOverlay
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerPulse
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerPulseState
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerSeeker
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerState
import com.example.sakhcasttv.ui.movie_player.components.rememberVideoPlayerPulseState
import com.example.sakhcasttv.ui.movie_player.components.rememberVideoPlayerState
import kotlinx.coroutines.delay
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@OptIn(UnstableApi::class)
@Composable
fun MoviePlayer(
    hls: String,
    title: String,
    position: Int,
    movieAlphaId: String,
    navigateUp: () -> Boolean,
    playerViewModel: MoviePlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var isAppInForeground by remember { mutableStateOf(true) }

    val movieState by playerViewModel.movieWatchState.collectAsStateWithLifecycle()
    val isPlaying by playerViewModel.isPlaying.collectAsStateWithLifecycle()
    val currentPosition by playerViewModel.currentPosition.collectAsStateWithLifecycle()
    val duration by playerViewModel.duration.collectAsStateWithLifecycle()

    var lifecycle by remember { mutableStateOf(Lifecycle.Event.ON_CREATE) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val playerState = rememberVideoPlayerState(hideSeconds = 4)
    val focusRequester = remember { FocusRequester() }
    val pulseState = rememberVideoPlayerPulseState()

    val availableQualities by playerViewModel.availableQualities.collectAsStateWithLifecycle()
    val currentQuality by playerViewModel.currentQuality.collectAsStateWithLifecycle()
    var showQualityDialog by remember { mutableStateOf(false) }

    val availableSubtitles by playerViewModel.availableSubtitles.collectAsStateWithLifecycle()
    val currentSubtitle by playerViewModel.currentSubtitle.collectAsStateWithLifecycle()
    var showSubtitleDialog by remember { mutableStateOf(false) }

    val availableAudioTracks by playerViewModel.availableAudioTracks.collectAsStateWithLifecycle()
    val currentAudioTrack by playerViewModel.currentAudioTrack.collectAsStateWithLifecycle()
    var showAudioTrackDialog by remember { mutableStateOf(false) }

    val currentResizeModeIndex by playerViewModel.currentResizeModeIndex.collectAsStateWithLifecycle()
    val currentResizeModeName by playerViewModel.currentResizeModeName.collectAsStateWithLifecycle()
    var showResizeModeMessage by remember { mutableStateOf(false) }

    val showContinueDialog by playerViewModel.showContinueDialog.collectAsStateWithLifecycle()

    var showExitSnackbar by remember { mutableStateOf(false) }

    LaunchedEffect(hls) {
        playerViewModel.setHlsManifest(hls)
    }

    LaunchedEffect(Unit) {
        playerViewModel.setMovieData(hls, title, position, movieAlphaId)
        playerViewModel.startPlayer()
    }

    DisposableEffect(key1 = lifecycleOwner) {
        val window = (context as? Activity)?.window
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    isAppInForeground = false
                    playerViewModel.player.pause()
                }

                Lifecycle.Event.ON_RESUME -> {
                    isAppInForeground = true
                }

                else -> Unit
            }
        }
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            lifecycleOwner.lifecycle.removeObserver(observer)
            playerViewModel.savePosition()
        }
    }

    Box(
        modifier = Modifier
            .dPadEvents(
                player = playerViewModel.player,
                videoPlayerState = playerState,
                pulseState = pulseState,
                showExitSnackbar = showExitSnackbar,
                onShowExitSnackbar = { showExitSnackbar = true },
                onHideExitSnackbar = { showExitSnackbar = false },
                navigateUp = navigateUp
            )
            .focusable()
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                PlayerView(it).apply {
                    player = playerViewModel.player
                    useController = false
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundColor(0xFF000000.toInt())
                    resizeMode = playerViewModel.resizeModes[currentResizeModeIndex]
                }
            },
            update = { playerView ->
                playerView.resizeMode = playerViewModel.resizeModes[currentResizeModeIndex]
            }
        )

        VideoPlayerOverlay(
            isPlaying = isPlaying,
            state = playerState,
            focusRequester = focusRequester,
            centerButton = {
                VideoPlayerPulse(state = pulseState)
            },
            controls = {
                VideoPlayerMainFrame(
                    mediaActions = {
                        Row {
                            VideoPlayerControlsIcon(
                                icon = ImageVector.vectorResource(id = R.drawable.ic_aspect_ratio),
                                contentDescription = "Режим растягивания",
                                onClick = {
                                    playerViewModel.toggleResizeMode()
                                    showResizeModeMessage = true
                                },
                                state = playerState,
                                isPlaying = isPlaying
                            )
                            Spacer(modifier = Modifier.width(16.dp))

                            VideoPlayerControlsIcon(
                                icon = ImageVector.vectorResource(id = R.drawable.ic_cc),
                                contentDescription = "Субтитры",
                                onClick = { showSubtitleDialog = true },
                                state = playerState,
                                isPlaying = isPlaying
                            )
                            Spacer(modifier = Modifier.width(16.dp))

                            VideoPlayerControlsIcon(
                                icon = ImageVector.vectorResource(id = R.drawable.ic_audio_list),
                                contentDescription = "Аудиодорожки",
                                onClick = { showAudioTrackDialog = true },
                                state = playerState,
                                isPlaying = isPlaying
                            )
                            Spacer(modifier = Modifier.width(16.dp))

                            VideoPlayerControlsIcon(
                                icon = Icons.Default.Settings,
                                contentDescription = "Качество видео",
                                onClick = { showQualityDialog = true },
                                state = playerState,
                                isPlaying = isPlaying
                            )
                        }
                    },
                    mediaTitle = {
                        VideoPlayerMediaTitle(
                            title = movieState.title,
                            secondaryText = "",
                            tertiaryText = ""
                        )
                    },
                    seeker = {
                        VideoPlayerSeeker(
                            focusRequester = focusRequester,
                            state = playerState,
                            isPlaying = isPlaying,
                            onPlayPauseToggle = {
                                playerViewModel.togglePlayPause()
                            },
                            onSeek = { progress ->
                                val newPosition = (progress * duration).toLong()
                                playerViewModel.seekTo(newPosition)
                            },
                            contentProgress = currentPosition.toDuration(DurationUnit.MILLISECONDS),
                            contentDuration = duration.toDuration(DurationUnit.MILLISECONDS)
                        )
                    }
                )
            }
        )

        if (showExitSnackbar) {
            LaunchedEffect(Unit) {
                delay(1500)
                showExitSnackbar = false
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Surface(
                    modifier = Modifier
                        .padding(16.dp)
                        .alpha(0.8f)
                        .wrapContentSize(),
                    shape = MaterialTheme.shapes.small,
                    colors = SurfaceDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(
                            alpha = 0.9f
                        )
                    ),
                    tonalElevation = 2.dp
                ) {
                    Text(
                        text = "Для выхода из плеера повторно нажмите кнопку \"Назад\"",
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (showResizeModeMessage) {
            LaunchedEffect(currentResizeModeName) {
                delay(2000)
                showResizeModeMessage = false
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = SurfaceDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(
                        text = currentResizeModeName,
                        modifier = Modifier
                            .padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        if (showQualityDialog) {
            QualitySelectionDialog(
                qualities = availableQualities,
                currentQuality = currentQuality,
                onQualitySelected = { quality ->
                    playerViewModel.setQuality(quality)
                    showQualityDialog = false
                },
                onDismiss = { showQualityDialog = false }
            )
        }
        if (showSubtitleDialog) {
            SubtitleSelectionDialog(
                subtitles = availableSubtitles,
                currentSubtitle = currentSubtitle,
                onSubtitleSelected = { subtitle ->
                    playerViewModel.setSubtitle(subtitle)
                    showSubtitleDialog = false
                },
                onDismiss = { showSubtitleDialog = false }
            )
        }
        if (showAudioTrackDialog) {
            AudioTrackSelectionDialog(
                audioTracks = availableAudioTracks,
                currentAudioTrack = currentAudioTrack,
                onAudioTrackSelected = { audioTrack ->
                    playerViewModel.setAudioTrack(audioTrack)
                    showAudioTrackDialog = false
                },
                onDismiss = { showAudioTrackDialog = false }
            )
        }
        if (showContinueDialog) {
            AlertDialog(
                containerColor = MaterialTheme.colorScheme.surface,
                textContentColor = MaterialTheme.colorScheme.onSurface,
                onDismissRequest = { playerViewModel.playFromBeginning() },
                text = { Text("Продолжить с ${movieState.position / 60} минут?") },
                confirmButton = {
                    TextButton(onClick = { playerViewModel.continuePlaying(position = position) }) {
                        Text("Да")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { playerViewModel.playFromBeginning() }) {
                        Text("Нет, начать сначала")
                    }
                }
            )
        }
    }
}

private fun Modifier.dPadEvents(
    player: Player,
    videoPlayerState: VideoPlayerState,
    pulseState: VideoPlayerPulseState,
    showExitSnackbar: Boolean,
    onShowExitSnackbar: () -> Unit,
    onHideExitSnackbar: () -> Unit,
    navigateUp: () -> Boolean
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
    },
    onBack = {
        if (videoPlayerState.controlsVisible) {
            videoPlayerState.hideControls()
            true
        } else if (showExitSnackbar) {
            onHideExitSnackbar()
            navigateUp()
            true
        } else {
            onShowExitSnackbar()
            true
        }
    }
)