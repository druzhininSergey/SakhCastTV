package com.example.sakhcasttv.ui.movie_player

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.example.sakhcasttv.data.repository.SakhCastRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val sakhCastRepository: SakhCastRepository,
    val player: Player,
) : ViewModel() {

    private var _movieWatchState = MutableStateFlow(MovieWatchState())
    val movieWatchState = _movieWatchState.asStateFlow()

//    private var _isPlayerPlaying = MutableStateFlow(player.isPlaying)
//    val isPlayerPlaying = _isPlayerPlaying.asStateFlow()
//
//    private var _contentCurrentPosition = MutableStateFlow(player.currentPosition)
//    val contentCurrentPosition = _contentCurrentPosition.asStateFlow()

    private var _isPositionSending = MutableStateFlow(false)
    private val isPositionSending = _isPositionSending.asStateFlow()

    data class MovieWatchState(
        var hlsUri: String = "",
        var title: String = "",
        var position: Int = 0,
        var movieAlphaId: String = "",
    )

//    private val playerListener = object : Player.Listener {
//        override fun onPlaybackStateChanged(playbackState: Int) {
//            _isPlayerPlaying.value = player.isPlaying
//            _contentCurrentPosition.value = player.currentPosition
//        }
//
//        override fun onIsPlayingChanged(isPlaying: Boolean) {
//            _isPlayerPlaying.value = isPlaying
//        }
//    }

    fun preparePlayer(hlsUrl: String) {
        val mediaItem = MediaItem.fromUri(hlsUrl)
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    fun startPlayer() {
        if (!isPositionSending.value) {
            _isPositionSending.value = true
            viewModelScope.launch {
                val uri = movieWatchState.value.hlsUri
                player.setMediaItem(
                    MediaItem.Builder()
                        .setUri(uri)
                        .build()
                )
//                player.addListener(playerListener)
                player.prepare()
                setMoviePosition()
            }
        }
    }

    fun onPlayPauseToggle() {
        if (!player.isPlaying) player.play()
        else player.pause()
    }

    fun onSeek(seekProgress: Float) {
        player.seekTo(player.duration.times(seekProgress).toLong())
    }

    fun seekForward() {
        player.seekForward()
    }

    fun seekBack() {
        player.seekBack()
    }

//    fun releasePlayer() {
//        player.release()
//    }

    fun setMovieData(hlsUri: String, title: String, position: Int, movieAlphaId: String) {
        viewModelScope.launch {
            _movieWatchState.value = _movieWatchState.value.copy(
                hlsUri = hlsUri,
                title = title,
                position = position,
                movieAlphaId = movieAlphaId
            )
        }
    }

    private fun setMoviePosition() {
        viewModelScope.launch {
            while (true) {
                if (player.isPlaying) {
                    delay(5000)
                    val movieAlphaId = _movieWatchState.value.movieAlphaId
                    val currentPosition = (player.currentPosition / 1000).toInt()
                    sakhCastRepository.setMoviePosition(movieAlphaId, currentPosition)
                    _movieWatchState.value = _movieWatchState.value.copy(position = currentPosition)
                } else {
                    delay(5000)
                    continue
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
//        player.removeListener(playerListener)
        _isPositionSending.value = false
        player.stop()
        player.clearMediaItems()

        player.release()
        Log.i("!!!", "player.release() ViewModel")
    }

}