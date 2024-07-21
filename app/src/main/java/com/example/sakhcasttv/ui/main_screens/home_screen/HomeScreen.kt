package com.example.sakhcasttv.ui.main_screens.home_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.sakhcasttv.Colors
import com.example.sakhcasttv.data.formatMinSec
import com.example.sakhcasttv.ui.main_screens.home_screen.movie.MovieCategoryView
import com.example.sakhcasttv.ui.main_screens.home_screen.recently_watched.ContinueWatchView
import com.example.sakhcasttv.ui.main_screens.home_screen.series.SeriesCategoryView
import com.example.sakhcasttv.ui.main_screens.home_screen.series.SeriesItemView
import com.example.sakhcasttv.ui.main_screens.main_screen_tabrow.MainScreensViewModel
import kotlinx.coroutines.flow.StateFlow


@Composable
fun HomeScreen(
    navigateToMovieByAlphaId: (String) -> Unit,
    navigateToSeriesById: (String) -> Unit,
    navigateToCatalogAllSeries: () -> Unit,
    navigateToCatalogAllMovies: () -> Unit,
    homeScreenState: StateFlow<MainScreensViewModel.HomeScreenState>,
    isLoading: StateFlow<Boolean>
) {
    val homeScreenStateCollected by homeScreenState.collectAsState()
    val isLoadingCollected by isLoading.collectAsState()
    val seriesList = homeScreenStateCollected.seriesList
    val movieList = homeScreenStateCollected.moviesList
    val lastWatchedMovieTime =
        homeScreenStateCollected.lastWatched?.movie?.data?.userFavourite?.position?.formatMinSec()
            ?: "0"
    val movie = homeScreenStateCollected.lastWatched?.movie
    val series = homeScreenStateCollected.lastWatched?.serial


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        if (!isLoadingCollected) {
            if (movie != null && series != null) {
                item {
                    ContinueWatchView(
                        movie,
                        series,
                        lastWatchedMovieTime,
                        navigateToMovieByAlphaId,
                        navigateToSeriesById
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

    if (isLoadingCollected) {
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
