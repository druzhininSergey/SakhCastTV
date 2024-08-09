package com.example.sakhcasttv.ui.series_player

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import com.example.sakhcasttv.data.repository.SakhCastRepository
import com.example.sakhcasttv.model.Episode
import com.example.sakhcasttv.model.Season
import com.example.sakhcasttv.model.SeriesPlaylist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _seriesWatchState = MutableStateFlow(SeriesWatchState())
    val seriesWatchState = _seriesWatchState.asStateFlow()

    data class SeriesWatchState(
        var episodeList: List<Episode>? = null,
        var playlist: List<SeriesPlaylist>? = null,
        var playlistUriList: List<String>? = emptyList(),
        var seasonId: String = "",
        var seriesId: Int = 0,
        var episodeChosenIndex: Int = 0,
        var rgChosen: String = "",
        var lastWatchedTime: Int = 0,
        var currentEpisodeId: Int = 0,
    )

    fun getSeriesCombined(seasonId: String, seriesId: Int, episodeIndex: Int, rgChosen: String) {
        viewModelScope.launch {
            val series = sakhCastRepository.getSeriesById(seriesId = seriesId)
            val playlist = series?.seasons ?: emptyList()
            setPlaylist(seasonId, rgChosen, playlist)
        }
    }

    private fun setPlaylist(
        seasonId: String,
        rgChosen: String,
        playlist: List<Season>
    ) {
        var seasonIndex = ""
        val mediaList = playlist
            .filter { season -> season.id == seasonId.toInt() }
            .flatMap { season ->
                seasonIndex = season.index
                season.episodes.flatMap { episode ->
                    episode.medias.filter { media ->
                        media.name == rgChosen
                    }
                }
            }
    }

}