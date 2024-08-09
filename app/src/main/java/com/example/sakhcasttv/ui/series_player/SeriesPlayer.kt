package com.example.sakhcasttv.ui.series_player

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi

@OptIn(UnstableApi::class)
@Composable
fun SeriesPlayer(
    seasonId: String,
    seriesId: Int,
    episodeIndex: Int,
    rgChosen: String,
    navigateUp: () -> Boolean,
    seriesPlayerViewModel: SeriesPlayerViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {
        seriesPlayerViewModel.getSeriesCombined(
            seasonId = seasonId,
            seriesId = seriesId,
            episodeIndex = episodeIndex,
            rgChosen = rgChosen
        )
    }

}
