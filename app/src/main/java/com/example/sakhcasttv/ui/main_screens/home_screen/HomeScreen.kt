package com.example.sakhcasttv.ui.main_screens.home_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import com.example.sakhcasttv.Colors
import com.example.sakhcasttv.data.formatMinSec
import com.example.sakhcasttv.ui.main_screens.home_screen.movie.MovieCategoryView
import com.example.sakhcasttv.ui.main_screens.home_screen.recently_watched.ContinueWatchView
import com.example.sakhcasttv.ui.main_screens.home_screen.series.SeriesCategoryView
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navigateToMovieByAlphaId: (String) -> Unit,
    navigateToSeriesById: (String) -> Unit,
    navigateToCatalogAllSeries: () -> Unit,
    navigateToCatalogAllMovies: () -> Unit,
    allScreensHomeState: StateFlow<HomeScreenViewModel.HomeScreenState>,
    loadDataToHomeScreen: (HomeScreenViewModel.HomeScreenState) -> Unit,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel()
) {
    val homeScreenState by homeScreenViewModel.homeScreenState.collectAsStateWithLifecycle()
    val isLoading by homeScreenViewModel.isLoading.collectAsStateWithLifecycle()
    val allScreensStateCollected by allScreensHomeState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (allScreensHomeState.value.lastWatched == null) {
            homeScreenViewModel.getAllScreenData()
        } else homeScreenViewModel.refreshData()
    }
    LaunchedEffect(homeScreenState) {
        if (!isLoading && homeScreenState.lastWatched != null) {
            loadDataToHomeScreen(homeScreenState)
        }
    }

    val seriesList = allScreensStateCollected.seriesList
    val movieList = allScreensStateCollected.moviesList
    val lastWatchedMovieTime =
        allScreensStateCollected.lastWatched?.movie?.data?.userFavourite?.position?.formatMinSec()
            ?: "0"
    val movie = allScreensStateCollected.lastWatched?.movie
    val series = allScreensStateCollected.lastWatched?.serial
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LazyColumn(
        state = scrollState,
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .onKeyEvent { event ->
                if (event.key == Key.Back) {
                    coroutineScope.launch {
                        scrollState.animateScrollToItem(0)
                        focusRequester.requestFocus()
                    }
                    true
                } else {
                    false
                }
            }
    ) {
        if (!isLoading) {
            if (movie != null && series != null) {
                item {
                    ContinueWatchView(
                        movie,
                        series,
                        lastWatchedMovieTime,
                        navigateToMovieByAlphaId,
                        navigateToSeriesById,
                        modifier = Modifier.focusRequester(focusRequester)
                    )
                }
            }
            if (seriesList != null) {
                item {
                    SeriesCategoryView(
                        seriesList,
                        navigateToSeriesById,
                        navigateToCatalogAllSeries
                    )
                }
            }
            if (movieList != null) {
                item {
                    MovieCategoryView(
                        movieList,
                        navigateToMovieByAlphaId,
                        navigateToCatalogAllMovies
                    )
                }
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                trackColor = Colors.blueColor
            )
        }
    }
}
