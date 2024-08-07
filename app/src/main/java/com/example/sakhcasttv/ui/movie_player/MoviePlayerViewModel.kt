package com.example.sakhcasttv.ui.movie_player

import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.AspectRatioFrameLayout
import com.example.sakhcasttv.data.repository.SakhCastRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@UnstableApi
@HiltViewModel
class MoviePlayerViewModel @Inject constructor(
    private val sakhCastRepository: SakhCastRepository,
    val player: Player,
) : ViewModel() {

    val resizeModes = listOf(
        AspectRatioFrameLayout.RESIZE_MODE_FIT,
        AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH,
        AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT,
        AspectRatioFrameLayout.RESIZE_MODE_FILL,
        AspectRatioFrameLayout.RESIZE_MODE_ZOOM
    )

    private val _movieWatchState = MutableStateFlow(MovieWatchState())
    val movieWatchState: StateFlow<MovieWatchState> = _movieWatchState.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private var positionUpdateJob: Job? = null

    private val _availableQualities = MutableStateFlow<List<VideoQuality>>(emptyList())
    val availableQualities: StateFlow<List<VideoQuality>> = _availableQualities.asStateFlow()

    private val _currentQuality = MutableStateFlow<VideoQuality?>(null)
    val currentQuality: StateFlow<VideoQuality?> = _currentQuality.asStateFlow()

    private val _availableSubtitles = MutableStateFlow<List<Subtitle>>(emptyList())
    val availableSubtitles: StateFlow<List<Subtitle>> = _availableSubtitles.asStateFlow()

    private val _currentSubtitle = MutableStateFlow<Subtitle?>(null)
    val currentSubtitle: StateFlow<Subtitle?> = _currentSubtitle.asStateFlow()

    private val _availableAudioTracks = MutableStateFlow<List<AudioTrackReceived>>(emptyList())
    val availableAudioTracks: StateFlow<List<AudioTrackReceived>> =
        _availableAudioTracks.asStateFlow()

    private val _currentAudioTrack = MutableStateFlow<AudioTrackReceived?>(null)
    val currentAudioTrack: StateFlow<AudioTrackReceived?> = _currentAudioTrack.asStateFlow()

    private val _currentResizeModeIndex = MutableStateFlow(0)
    val currentResizeModeIndex: StateFlow<Int> = _currentResizeModeIndex.asStateFlow()

    private val _currentResizeModeName = MutableStateFlow("Подогнать")
    val currentResizeModeName: StateFlow<String> = _currentResizeModeName.asStateFlow()

    private var baseUrl: String = ""

    data class MovieWatchState(
        var hlsUri: String = "",
        var title: String = "",
        var position: Int = 0,
        var movieAlphaId: String = "",
    )

    data class VideoQuality(
        val resolution: String,
        val bandwidth: Int,
        val uri: String
    )

    data class Subtitle(val id: String, val name: String, val language: String)

    data class AudioTrackReceived(val id: String, val name: String, val language: String)

    private val playerListener =
        object : Player.Listener {
            override fun onTracksChanged(tracks: Tracks) {
                updateAvailableQualities()
                updateAvailableSubtitles()
                val hasActiveTextTrack = tracks.groups.any { group ->
                    group.type == C.TRACK_TYPE_TEXT && group.isSelected
                }

                if (!hasActiveTextTrack) {
                    _currentSubtitle.value =
                        _availableSubtitles.value.firstOrNull { it.id == "off" }
                }

                updateAvailableAudioTracks()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_ENDED -> {
                        stopPlayback()
                    }
                    Player.STATE_READY -> {
                        _duration.value = player.duration
                        startPositionUpdates()
                    }
                    else -> {
                        stopPositionUpdates()
                    }
                }
                _isPlaying.value = playbackState == Player.STATE_READY && player.playWhenReady
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }
        }

    private fun stopPlayback() {
        player.pause()
        player.seekTo(0)
        _isPlaying.value = false
        stopPositionUpdates()
    }

    init {
        player.addListener(playerListener)
        startPositionUpdates()
    }

    fun toggleResizeMode() {
        _currentResizeModeIndex.value = (_currentResizeModeIndex.value + 1) % resizeModes.size
        updateResizeModeName()
    }

    private fun updateResizeModeName() {
        _currentResizeModeName.value = when (resizeModes[_currentResizeModeIndex.value]) {
            AspectRatioFrameLayout.RESIZE_MODE_FIT -> "Оптимальный"
            AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH -> "По ширине"
            AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT -> "По высоте"
            AspectRatioFrameLayout.RESIZE_MODE_FILL -> "Заполнение"
            AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> "Увеличение"
            else -> "Неизвестно"
        }
    }

    private fun updateAvailableAudioTracks() {
        val audioTracks = mutableListOf<AudioTrackReceived>()

        player.currentTracks.groups.forEachIndexed { index, trackGroup ->
            if (trackGroup.type == C.TRACK_TYPE_AUDIO) {
                for (i in 0 until trackGroup.length) {
                    val format = trackGroup.getTrackFormat(i)
                    val language = format.language ?: "unknown"
                    val name = format.label ?: language
                    val id = "$language-$i"
                    audioTracks.add(AudioTrackReceived(id, name, language))
                }
            }
        }

        _availableAudioTracks.value = audioTracks

        val currentAudioTrack = player.currentTracks.groups
            .filter { it.type == C.TRACK_TYPE_AUDIO }
            .flatMap { group ->
                (0 until group.length)
                    .filter { group.isTrackSelected(it) }
                    .map { group.getTrackFormat(it) }
            }
            .firstOrNull()

        _currentAudioTrack.value = currentAudioTrack?.let { track ->
            audioTracks.find {
                it.language == track.language && it.name == (track.label ?: track.language)
            }
        } ?: audioTracks.firstOrNull()
    }

    fun setAudioTrack(audioTrack: AudioTrackReceived) {
        viewModelScope.launch {
            _currentAudioTrack.value = audioTrack

            player.trackSelectionParameters = player.trackSelectionParameters.buildUpon()
                .setPreferredAudioLanguage(audioTrack.language)
                .build()

            player.currentTracks.groups
                .filter { it.type == C.TRACK_TYPE_AUDIO }
                .forEach { group ->
                    for (i in 0 until group.length) {
                        val format = group.getTrackFormat(i)
                        if (format.language == audioTrack.language && format.label == audioTrack.name) {
                            (player as ExoPlayer).trackSelectionParameters =
                                player.trackSelectionParameters.buildUpon()
                                    .addOverride(TrackSelectionOverride(group.mediaTrackGroup, i))
                                    .build()
                            break
                        }
                    }
                }

            (player as ExoPlayer).trackSelectionParameters = player.trackSelectionParameters
        }
    }

    private fun updateAvailableSubtitles() {
        val subtitles = mutableListOf<Subtitle>()
        subtitles.add(Subtitle("off", "Отключить субтитры", ""))

        player.currentTracks.groups.forEachIndexed { index, trackGroup ->
            if (trackGroup.type == C.TRACK_TYPE_TEXT) {
                for (i in 0 until trackGroup.length) {
                    val format = trackGroup.getTrackFormat(i)
                    val language = format.language ?: "unknown"
                    val name = format.label ?: language
                    val id = "$language-$i"
                    subtitles.add(Subtitle(id, name, language))
                }
            }
        }

        _availableSubtitles.value = subtitles

        val currentSubtitleTrack = player.currentTracks.groups
            .filter { it.type == C.TRACK_TYPE_TEXT }
            .flatMap { group ->
                (0 until group.length)
                    .filter { group.isTrackSelected(it) }
                    .map { group.getTrackFormat(it) }
            }
            .firstOrNull()

        _currentSubtitle.value = if (currentSubtitleTrack != null) {
            subtitles.find {
                it.language == currentSubtitleTrack.language && it.name == (currentSubtitleTrack.label
                    ?: currentSubtitleTrack.language)
            }
        } else {
            subtitles.first()
        }
    }

    fun setSubtitle(subtitle: Subtitle) {
        viewModelScope.launch {
            _currentSubtitle.value = subtitle

            if (subtitle.id == "off") {
                // Отключаем все текстовые треки
                player.trackSelectionParameters = player.trackSelectionParameters.buildUpon()
                    .setIgnoredTextSelectionFlags(C.SELECTION_FLAG_FORCED or C.SELECTION_FLAG_DEFAULT)
                    .setPreferredTextLanguage(null)
                    .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)  // Отключаем текстовые треки
                    .clearOverridesOfType(C.TRACK_TYPE_TEXT)  // Очищаем все переопределения для текстовых треков
                    .build()
            } else {
                // Включаем конкретный трек субтитров
                player.trackSelectionParameters = player.trackSelectionParameters.buildUpon()
                    .setIgnoredTextSelectionFlags(0)
                    .setPreferredTextLanguage(subtitle.language)
                    .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)  // Включаем текстовые треки
                    .clearOverridesOfType(C.TRACK_TYPE_TEXT)  // Сначала очищаем все переопределения
                    .build()

                // Выбираем конкретный трек субтитров
                player.currentTracks.groups
                    .filter { it.type == C.TRACK_TYPE_TEXT }
                    .forEach { group ->
                        for (i in 0 until group.length) {
                            val format = group.getTrackFormat(i)
                            if (format.language == subtitle.language && format.label == subtitle.name) {
                                (player as ExoPlayer).trackSelectionParameters =
                                    player.trackSelectionParameters.buildUpon()
                                        .addOverride(
                                            TrackSelectionOverride(
                                                group.mediaTrackGroup,
                                                i
                                            )
                                        )
                                        .build()
                                break
                            }
                        }
                    }
            }
            (player as ExoPlayer).trackSelectionParameters = player.trackSelectionParameters
        }
    }

    fun setHlsManifest(manifestUrl: String) {
        baseUrl = manifestUrl.substringBeforeLast("/")
        _movieWatchState.value = _movieWatchState.value.copy(hlsUri = manifestUrl)
    }

    @OptIn(UnstableApi::class)
    private fun updateAvailableQualities() {
        val qualities = mutableListOf<VideoQuality>()
        qualities.add(VideoQuality("Авто", 0, ""))

        player.currentTracks.groups.forEach { trackGroup ->
            if (trackGroup.type == C.TRACK_TYPE_VIDEO) {
                for (i in 0 until trackGroup.length) {
                    val format = trackGroup.getTrackFormat(i)
                    val width = format.width
                    val height = format.height
                    if (width > 0 && height > 0) {
                        qualities.add(VideoQuality("${width}x${height}", format.bitrate, ""))
                    }
                }
            }
        }

        _availableQualities.value = qualities.sortedByDescending { it.bandwidth }
        if (_currentQuality.value == null || _availableQualities.value.none { it.resolution == _currentQuality.value?.resolution }) {
            _currentQuality.value = qualities.first()
        }
    }

    @OptIn(UnstableApi::class)
    fun setQuality(quality: VideoQuality) {
        viewModelScope.launch {
            _currentQuality.value = quality
            val currentPosition = player.currentPosition

            val trackSelector = (player as ExoPlayer).trackSelector as DefaultTrackSelector
            if (quality.resolution == "Авто") {
                trackSelector.setParameters(
                    trackSelector.buildUponParameters().clearOverridesOfType(C.TRACK_TYPE_VIDEO)
                )
            } else {
                val qualityWidth = quality.resolution.split("x")[0].toInt()
                val qualityHeight = quality.resolution.split("x")[1].toInt()
                trackSelector.setParameters(
                    trackSelector.buildUponParameters()
                        .setMaxVideoSize(qualityWidth, qualityHeight)
                        .setMinVideoSize(qualityWidth, qualityHeight)
                )
            }

            player.seekTo(currentPosition)
            player.play()
        }
    }

    fun startPlayer() {
        viewModelScope.launch {
            val uri = movieWatchState.value.hlsUri
            player.setMediaItem(MediaItem.fromUri(uri))
            player.prepare()
            player.seekTo(movieWatchState.value.position.toLong())
            player.play()
            startPositionUpdates()
        }
    }

    fun setMovieData(hlsUri: String, title: String, position: Int, movieAlphaId: String) {
        viewModelScope.launch {
            _movieWatchState.value = MovieWatchState(
                hlsUri = hlsUri,
                title = title,
                position = position,
                movieAlphaId = movieAlphaId
            )
        }
    }

    private fun startPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = viewModelScope.launch {
            while (isActive) {
                _currentPosition.value = player.currentPosition
                delay(1000)
            }
        }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
    }

    fun togglePlayPause() {
        if (player.playbackState == Player.STATE_ENDED) {
            player.seekTo(0)
            player.play()
            startPositionUpdates()
        } else if (player.isPlaying) {
            player.pause()
            stopPositionUpdates()
        } else {
            player.play()
            startPositionUpdates()
        }
        _isPlaying.value = player.isPlaying
    }

    fun seekTo(position: Long) {
        player.seekTo(position)
    }

    fun savePosition() {
        viewModelScope.launch {
            val movieAlphaId = _movieWatchState.value.movieAlphaId
            val currentPosition = (player.currentPosition / 1000).toInt()
            sakhCastRepository.setMoviePosition(movieAlphaId, currentPosition)
            _movieWatchState.value = _movieWatchState.value.copy(position = currentPosition)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopPositionUpdates()
        savePosition()
        player.removeListener(playerListener)
        player.release()
    }
}