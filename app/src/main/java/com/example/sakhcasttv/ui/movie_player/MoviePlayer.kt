package com.example.sakhcasttv.ui.movie_player

import android.app.Activity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.example.sakhcasttv.R
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerControlsIcon
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerMainFrame
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerMediaTitle
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerOverlay
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerPulse
import com.example.sakhcasttv.ui.movie_player.components.VideoPlayerSeeker
import com.example.sakhcasttv.ui.movie_player.components.rememberVideoPlayerPulseState
import com.example.sakhcasttv.ui.movie_player.components.rememberVideoPlayerState
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@OptIn(UnstableApi::class)
@Composable
fun MoviePlayer(
    hls: String,
    title: String,
    position: Int,
    movieAlphaId: String,
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
                playerViewModel.player,
                playerState,
                pulseState
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
                }
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
                                icon = ImageVector.vectorResource(id = R.drawable.ic_cc),
                                contentDescription = "Субтитры",
                                onClick = { showSubtitleDialog = true },
                                state = playerState,
                                isPlaying = isPlaying
                            )

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
    }
}

@Composable
fun QualitySelectionDialog(
    qualities: List<MoviePlayerViewModel.VideoQuality>,
    currentQuality: MoviePlayerViewModel.VideoQuality?,
    onQualitySelected: (MoviePlayerViewModel.VideoQuality) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Выберите качество", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                qualities.forEach { quality ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onQualitySelected(quality) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (quality.resolution == "Авто") "Авто" else "${quality.resolution} (${quality.bandwidth / 1000} Kbps)",
                            modifier = Modifier.weight(1f)
                        )
                        if (quality == currentQuality) {
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

@Composable
fun SubtitleSelectionDialog(
    subtitles: List<MoviePlayerViewModel.Subtitle>,
    currentSubtitle: MoviePlayerViewModel.Subtitle?,
    onSubtitleSelected: (MoviePlayerViewModel.Subtitle) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Выберите субтитры", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                subtitles.forEach { subtitle ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSubtitleSelected(subtitle) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = subtitle.name,
                            modifier = Modifier.weight(1f)
                        )
                        if (subtitle == currentSubtitle) {
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