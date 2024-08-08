package com.example.sakhcasttv.ui.series_player

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
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import com.example.sakhcasttv.R
import com.example.sakhcasttv.data.formatMinSec
import com.example.sakhcasttv.ui.general.handleDPadKeyEvents
import com.example.sakhcasttv.ui.movie_player.components.AudioTrackSelectionDialog
import com.example.sakhcasttv.ui.movie_player.components.QualitySelectionDialog
import com.example.sakhcasttv.ui.movie_player.components.SubtitleSelectionDialog
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerControlsIcon
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerMainFrame
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerMediaTitle
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerOverlay
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerSeeker
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerState
import com.example.sakhcasttv.ui.movie_player.components.rememberVideoPlayerState
import kotlinx.coroutines.delay
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@OptIn(UnstableApi::class)
@Composable
fun SeriesPlayer(
    seasonId: String,
    seriesTitle: String,
    episodeIndex: Int,
    rgChosen: String,
    navigateUp: () -> Boolean,
    seriesPlayerViewModel: SeriesPlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var isAppInForeground by remember { mutableStateOf(true) }

    val isDataLoaded by seriesPlayerViewModel.isDataLoaded.collectAsStateWithLifecycle()
    LaunchedEffect(isDataLoaded) {
        if (isDataLoaded) seriesPlayerViewModel.startPlayer()
    }

    val seriesState by seriesPlayerViewModel.seriesWatchState.collectAsStateWithLifecycle()
    val isPlaying by seriesPlayerViewModel.isPlaying.collectAsStateWithLifecycle()
    val currentPosition by seriesPlayerViewModel.currentPosition.collectAsStateWithLifecycle()
    val duration by seriesPlayerViewModel.duration.collectAsStateWithLifecycle()

    var lifecycle by remember { mutableStateOf(Lifecycle.Event.ON_CREATE) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val playerState = rememberVideoPlayerState(hideSeconds = 4)
    val focusRequester = remember { FocusRequester() }

    val availableQualities by seriesPlayerViewModel.availableQualities.collectAsStateWithLifecycle()
    val currentQuality by seriesPlayerViewModel.currentQuality.collectAsStateWithLifecycle()

    val availableSubtitles by seriesPlayerViewModel.availableSubtitles.collectAsStateWithLifecycle()
    val currentSubtitle by seriesPlayerViewModel.currentSubtitle.collectAsStateWithLifecycle()

    val availableAudioTracks by seriesPlayerViewModel.availableAudioTracks.collectAsStateWithLifecycle()
    val currentAudioTrack by seriesPlayerViewModel.currentAudioTrack.collectAsStateWithLifecycle()

    val currentResizeModeIndex by seriesPlayerViewModel.currentResizeModeIndex.collectAsStateWithLifecycle()
    val currentResizeModeName by seriesPlayerViewModel.currentResizeModeName.collectAsStateWithLifecycle()
    var showResizeModeMessage by remember { mutableStateOf(false) }

    val showContinueDialog by seriesPlayerViewModel.showContinueDialog.collectAsStateWithLifecycle()

    var showExitSnackbar by remember { mutableStateOf(false) }

    var currentDialog by remember { mutableStateOf(DialogType.NONE) }

    LaunchedEffect(Unit) {
        seriesPlayerViewModel.setSeriesData(seasonId, seriesTitle, episodeIndex, rgChosen)
    }

    DisposableEffect(key1 = lifecycleOwner) {
        val window = (context as? Activity)?.window
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    isAppInForeground = false
                    seriesPlayerViewModel.player.pause()
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
        }
    }

    fun switchDialog(direction: Int) {
        val dialogs = DialogType.entries.filter { it != DialogType.NONE }
        val currentIndex = dialogs.indexOf(currentDialog)
        val newIndex = (currentIndex + direction + dialogs.size) % dialogs.size
        currentDialog = dialogs[newIndex]
    }

    Box(
        modifier = Modifier
            .dPadEvents(
                player = seriesPlayerViewModel.player,
                videoPlayerState = playerState,
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
                    player = seriesPlayerViewModel.player
                    useController = false
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundColor(0xFF000000.toInt())
                    resizeMode = seriesPlayerViewModel.resizeModes[currentResizeModeIndex]
                }
            },
            update = { playerView ->
                playerView.resizeMode = seriesPlayerViewModel.resizeModes[currentResizeModeIndex]
            }
        )

        VideoPlayerOverlay(
            isPlaying = isPlaying,
            state = playerState,
            focusRequester = focusRequester,
            controls = {
                VideoPlayerMainFrame(
                    mediaActions = {
                        Row {
                            VideoPlayerControlsIcon(
                                icon = ImageVector.vectorResource(id = R.drawable.ic_aspect_ratio),
                                contentDescription = "Режим растягивания",
                                onClick = {
                                    seriesPlayerViewModel.toggleResizeMode()
                                    showResizeModeMessage = true
                                },
                                state = playerState,
                                isPlaying = isPlaying
                            )
                            Spacer(modifier = Modifier.width(16.dp))

                            VideoPlayerControlsIcon(
                                icon = ImageVector.vectorResource(id = R.drawable.ic_cc),
                                contentDescription = "Субтитры",
                                onClick = { currentDialog = DialogType.SUBTITLE },
                                state = playerState,
                                isPlaying = isPlaying
                            )
                            Spacer(modifier = Modifier.width(16.dp))

                            VideoPlayerControlsIcon(
                                icon = ImageVector.vectorResource(id = R.drawable.ic_audio_list),
                                contentDescription = "Аудиодорожки",
                                onClick = { currentDialog = DialogType.AUDIO_TRACK },
                                state = playerState,
                                isPlaying = isPlaying
                            )
                            Spacer(modifier = Modifier.width(16.dp))

                            VideoPlayerControlsIcon(
                                icon = Icons.Default.Settings,
                                contentDescription = "Качество видео",
                                onClick = { currentDialog = DialogType.QUALITY },
                                state = playerState,
                                isPlaying = isPlaying
                            )
                        }
                    },
                    mediaTitle = {
                        VideoPlayerMediaTitle(
                            title = seriesState.seriesTitle,
                            secondaryText = "Сезон ${seriesState.seasonId}",
                            tertiaryText = "Эпизод ${seriesState.currentEpisodeIndex + 1}"
                        )
                    },
                    seeker = {
                        VideoPlayerSeeker(
                            focusRequester = focusRequester,
                            state = playerState,
                            isPlaying = isPlaying,
                            onPlayPauseToggle = {
                                seriesPlayerViewModel.togglePlayPause()
                            },
                            onSeek = { progress ->
                                val newPosition = (progress * duration).toLong()
                                seriesPlayerViewModel.seekTo(newPosition)
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
                        containerColor = MaterialTheme.colorScheme.onSurface
                    ),
                    tonalElevation = 2.dp
                ) {
                    Text(
                        text = "Для выхода из плеера повторно нажмите кнопку \"Назад\"",
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.surface,
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

        when (currentDialog) {
            DialogType.QUALITY -> {
                QualitySelectionDialog(
                    qualities = availableQualities,
                    currentQuality = currentQuality,
                    onQualitySelected = { quality ->
                        seriesPlayerViewModel.setQuality(quality)
                    },
                    onDismiss = { currentDialog = DialogType.NONE },
                    onNavigate = { direction -> switchDialog(direction) }
                )
            }

            DialogType.SUBTITLE -> {
                SubtitleSelectionDialog(
                    subtitles = availableSubtitles,
                    currentSubtitle = currentSubtitle,
                    onSubtitleSelected = { subtitle ->
                        seriesPlayerViewModel.setSubtitle(subtitle)
                    },
                    onDismiss = { currentDialog = DialogType.NONE },
                    onNavigate = { direction -> switchDialog(direction) }
                )
            }

            DialogType.AUDIO_TRACK -> {
                AudioTrackSelectionDialog(
                    audioTracks = availableAudioTracks,
                    currentAudioTrack = currentAudioTrack,
                    onAudioTrackSelected = { audioTrack ->
                        seriesPlayerViewModel.setAudioTrack(audioTrack)
                    },
                    onDismiss = { currentDialog = DialogType.NONE },
                    onNavigate = { direction -> switchDialog(direction) }
                )
            }

            DialogType.NONE -> {}
        }

        if (showContinueDialog) {
            AlertDialog(
                containerColor = MaterialTheme.colorScheme.surface,
                textContentColor = MaterialTheme.colorScheme.onSurface,
                onDismissRequest = { seriesPlayerViewModel.playFromBeginning() },
                text = { Text("Продолжить с ${seriesState.lastWatchedTime.formatMinSec()}?") },
                confirmButton = {
                    Button(onClick = { seriesPlayerViewModel.continuePlaying() }) {
                        Text("Да")
                    }
                },
                dismissButton = {
                    Button(onClick = { seriesPlayerViewModel.playFromBeginning() }) {
                        Text("Начать сначала")
                    }
                }
            )
        }
    }
}

enum class DialogType {
    QUALITY, SUBTITLE, AUDIO_TRACK, NONE
}

private fun Modifier.dPadEvents(
    player: Player,
    videoPlayerState: VideoPlayerState,
    showExitSnackbar: Boolean,
    onShowExitSnackbar: () -> Unit,
    onHideExitSnackbar: () -> Unit,
    navigateUp: () -> Boolean
): Modifier = this.handleDPadKeyEvents(
    onLeft = {
        videoPlayerState.showControls()
    },
    onRight = {
        videoPlayerState.showControls()
    },
    onUp = {
        videoPlayerState.showControls()
    },
    onDown = {
        videoPlayerState.showControls()
    },
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
        } else {
            onShowExitSnackbar()
            true
        }
    }
)