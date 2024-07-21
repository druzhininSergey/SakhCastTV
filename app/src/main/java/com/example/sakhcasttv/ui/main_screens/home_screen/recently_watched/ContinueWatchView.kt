package com.example.sakhcasttv.ui.main_screens.home_screen.recently_watched

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sakhcasttv.model.MovieRecent
import com.example.sakhcasttv.model.SeriesRecent

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContinueWatchView(
    movie: MovieRecent,
    series: SeriesRecent,
    lastWatchedMovieTime: String,
    navigateToMovieByAlphaId: (String) -> Unit,
    navigateToSeriesById: (String) -> Unit
) {
    val pagerState = rememberPagerState { 2 }
    var tabIndex by remember {
        mutableIntStateOf(0)
    }
    LaunchedEffect(tabIndex) {
        pagerState.animateScrollToPage(tabIndex)
    }
    LaunchedEffect(pagerState.currentPage) {
        tabIndex = pagerState.currentPage
    }
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row {
            ContinueWatchSeriesView(series, navigateToSeriesById)
            Spacer(modifier = Modifier.width(30.dp))
            ContinueWatchMovieView(movie, lastWatchedMovieTime, navigateToMovieByAlphaId)
        }
    }
}