package com.example.sakhcasttv.ui.series_player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import com.example.sakhcasttv.data.repository.SakhCastRepository
import com.example.sakhcasttv.model.AudioTrackReceived
import com.example.sakhcasttv.model.Episode
import com.example.sakhcasttv.model.SeriesPlaylist
import com.example.sakhcasttv.model.Subtitle
import com.example.sakhcasttv.model.VideoQuality
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@UnstableApi
@HiltViewModel
class SeriesPlayerViewModel @Inject constructor(
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

    private var _isDataLoaded = MutableStateFlow(false)
    val isDataLoaded: StateFlow<Boolean> = _isDataLoaded

    private val _seriesWatchState = MutableStateFlow(SeriesWatchState())
    val seriesWatchState = _seriesWatchState.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()

    private val _availableQualities = MutableStateFlow<List<VideoQuality>>(emptyList())
    val availableQualities = _availableQualities.asStateFlow()

    private val _currentQuality = MutableStateFlow<VideoQuality?>(null)
    val currentQuality = _currentQuality.asStateFlow()

    private val _availableSubtitles = MutableStateFlow<List<Subtitle>>(emptyList())
    val availableSubtitles = _availableSubtitles.asStateFlow()

    private val _currentSubtitle = MutableStateFlow<Subtitle?>(null)
    val currentSubtitle = _currentSubtitle.asStateFlow()

    private val _availableAudioTracks = MutableStateFlow<List<AudioTrackReceived>>(emptyList())
    val availableAudioTracks = _availableAudioTracks.asStateFlow()

    private val _currentAudioTrack = MutableStateFlow<AudioTrackReceived?>(null)
    val currentAudioTrack = _currentAudioTrack.asStateFlow()

    private val _currentResizeModeIndex = MutableStateFlow(0)
    val currentResizeModeIndex = _currentResizeModeIndex.asStateFlow()

    private val _currentResizeModeName = MutableStateFlow("Подогнать")
    val currentResizeModeName = _currentResizeModeName.asStateFlow()

    private val _showContinueDialog = MutableStateFlow(false)
    val showContinueDialog = _showContinueDialog.asStateFlow()

    data class SeriesWatchState(
        var episodeList: List<Episode>? = emptyList(),
        var playlist: List<SeriesPlaylist>? = emptyList(),
        var playlistUriList: List<String>? = emptyList(),
        var seasonId: String = "",
        var seriesTitle: String = "",
        var currentEpisodeIndex: Int = 0,
        var rgChosen: String = "",
        var lastWatchedTime: Int = 0,
        var currentEpisodeId: Int = 0,
    )

    private val playerListener = object : Player.Listener {
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

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateCurrentEpisode()
        }
    }

    init {
        player.addListener(playerListener)
    }

    fun setSeriesData(seasonId: String, seriesTitle: String, episodeIndex: Int, rgChosen: String) {
        viewModelScope.launch {
            val episodeList = sakhCastRepository.getSeriesEpisodesBySeasonId(seasonId.toInt())
            val playlist =
                sakhCastRepository.getSeriesPlaylistBySeasonIdAndRgName(seasonId, rgChosen)

            _seriesWatchState.value = SeriesWatchState(
                episodeList = episodeList,
                playlist = playlist,
                seasonId = seasonId,
                seriesTitle = seriesTitle,
                currentEpisodeIndex = episodeIndex,
                rgChosen = rgChosen
            )

            setCurrentEpisodeData(episodeIndex)
            _showContinueDialog.value = _seriesWatchState.value.lastWatchedTime > 0
            _isDataLoaded.update { true }
        }
    }

    private fun setCurrentEpisodeData(episodeIndex: Int) {
        val episode = _seriesWatchState.value.episodeList?.getOrNull(episodeIndex)
        val playlistItem =
            _seriesWatchState.value.playlist?.find { it.episodeIndex.toInt() == episodeIndex + 1 }

        _seriesWatchState.value = _seriesWatchState.value.copy(
            lastWatchedTime = episode?.isViewed ?: 0,
            currentEpisodeId = playlistItem?.mediaId ?: 0,
            currentEpisodeIndex = episodeIndex
        )
    }

    fun startPlayer() {
        viewModelScope.launch {
            val playlistUriList = _seriesWatchState.value.playlist?.map { it.episodePlaylist }
            val mediaItems = playlistUriList?.map { MediaItem.fromUri(it) }
            if (mediaItems != null) {
                player.setMediaItems(mediaItems)
            }
            player.seekTo(_seriesWatchState.value.currentEpisodeIndex, 0)
            player.prepare()
            startPositionUpdates()
        }
    }

    private fun updateCurrentEpisode() {
        val currentIndex = player.currentMediaItemIndex
        setCurrentEpisodeData(currentIndex)
    }

    private fun startPositionUpdates() {
        viewModelScope.launch {
            while (true) {
                _currentPosition.value = player.currentPosition
                delay(1000)
            }
        }
    }

    private fun stopPositionUpdates() {
        viewModelScope.launch {
            _currentPosition.value = 0
        }
    }

    private fun stopPlayback() {
        player.pause()
        player.seekTo(0)
        _isPlaying.value = false
        stopPositionUpdates()
    }

    fun togglePlayPause() {
        if (player.playbackState == Player.STATE_ENDED) {
            player.seekTo(0)
            player.play()
            startPositionUpdates()
        } else if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
        _isPlaying.value = player.isPlaying
    }

    fun seekTo(position: Long) {
        player.seekTo(position)
    }

    fun toggleResizeMode() {
        _currentResizeModeIndex.value = (_currentResizeModeIndex.value + 1) % resizeModes.size
        updateResizeModeName()
    }

    private fun updateResizeModeName() {
        _currentResizeModeName.value = when (resizeModes[_currentResizeModeIndex.value]) {
            AspectRatioFrameLayout.RESIZE_MODE_FIT -> "Подогнать"
            AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH -> "По ширине"
            AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT -> "По высоте"
            AspectRatioFrameLayout.RESIZE_MODE_FILL -> "Заполнение"
            AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> "Увеличение"
            else -> "Неизвестно"
        }
    }

    fun setQuality(quality: VideoQuality) {
        // Реализация выбора качества видео
    }

    fun setSubtitle(subtitle: Subtitle) {
        // Реализация выбора субтитров
    }

    fun setAudioTrack(audioTrack: AudioTrackReceived) {
        // Реализация выбора аудиодорожки
    }

    fun continuePlaying() {
        player.seekTo(_seriesWatchState.value.lastWatchedTime * 1000L)
        player.play()
        _showContinueDialog.value = false
    }

    fun playFromBeginning() {
        player.seekTo(0)
        player.play()
        _showContinueDialog.value = false
    }

    override fun onCleared() {
        super.onCleared()
        player.removeListener(playerListener)
        player.release()
    }
}