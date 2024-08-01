package com.example.sakhcasttv.ui.test_player.exoplayer

import android.view.View
import com.example.sakhcasttv.ui.test_player.state.PlayerStateListener

interface TLPlayer {
    fun play()
    fun pause()
    fun stop()

    fun seekTo(positionMs: Long)
    fun seekForward()
    fun seekBack()

    fun prepare(uri: String, playWhenReady: Boolean)
    fun release()
    fun getView(): View

    val currentPosition: Long
    val duration: Long
    val isPlaying: Boolean

    fun setPlaybackEvent(callback: PlayerStateListener)
    fun removePlaybackEvent(callback: PlayerStateListener)
}