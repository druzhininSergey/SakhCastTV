package com.example.sakhcasttv.ui.main_screens.favorites_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.TabRow
import com.example.sakhcasttv.Colors
import com.example.sakhcasttv.ui.general.MenuItem
import com.example.sakhcasttv.ui.general.NavigationTabItem
import kotlinx.coroutines.flow.StateFlow

@Composable
fun FavoritesScreen(
    navigateToMovieByAlphaId: (String) -> Unit,
    navigateToSeriesById: (String) -> Unit,
    navigateToSeriesCategoryByType: (String, String) -> Unit,
    navigateToMovieCategoriesByGenresId: (String, String) -> Unit,
    allScreensFavoriteState: StateFlow<FavoritesScreenViewModel.FavoritesScreenState>,
    loadDataToHomeScreen: (FavoritesScreenViewModel.FavoritesScreenState) -> Unit,
    favoritesScreenViewModel: FavoritesScreenViewModel = hiltViewModel()
) {
    val favoritesScreenState by favoritesScreenViewModel.favoritesScreenState.collectAsState()
    val allScreensStateCollected = allScreensFavoriteState.collectAsState()

    LaunchedEffect(Unit) {
        if (allScreensStateCollected.value.movieCardsWatched == null) {
            favoritesScreenViewModel.getAllContent()
        } else favoritesScreenViewModel.updateAllContent()
    }

    LaunchedEffect(favoritesScreenState) {
        if (!favoritesScreenState.isLoading) {
            loadDataToHomeScreen(favoritesScreenState)
        }
    }

    val seriesCardWatching = allScreensStateCollected.value.seriesCardWatching
    val seriesCardWillWatch = allScreensStateCollected.value.seriesCardWillWatch
    val seriesCardFinishedWatching =
        allScreensStateCollected.value.seriesCardFinishedWatching
    val seriesCardWatched = allScreensStateCollected.value.seriesCardWatched
    val movieCardsWillWatch = allScreensStateCollected.value.movieCardsWillWatch
    val movieCardsWatched = allScreensStateCollected.value.movieCardsWatched

    val pages = listOf("Сериалы", "Фильмы")
    var tabIndex by remember {
        mutableIntStateOf(0)
    }
    val pagerState = rememberPagerState { pages.size }
    var selectedTabIndex by remember { mutableIntStateOf(pagerState.currentPage) }
    LaunchedEffect(tabIndex) {
        pagerState.animateScrollToPage(tabIndex)
    }
    LaunchedEffect(pagerState.currentPage) {
        tabIndex = pagerState.currentPage
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        if (favoritesScreenState.isLoading) {
            CircularProgressIndicator(
                color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                trackColor = Colors.blueColor
            )
        } else {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp, bottom = 10.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        indicator = { _, _ ->
                            Box(
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.small)
                                    .background(Color.Transparent)
                            )
                        },
                    ) {
                        pages.forEachIndexed { index, page ->
                            NavigationTabItem(
                                item = MenuItem(id = page, text = page),
                                isSelected = selectedTabIndex == index,
                                onMenuSelected = {
                                    selectedTabIndex = index
                                },
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))
                if (selectedTabIndex == 0)
                    SeriesPage(
                        seriesCardWatching,
                        seriesCardWillWatch,
                        seriesCardFinishedWatching,
                        seriesCardWatched,
                        navigateToSeriesById,
                        navigateToSeriesCategoryByType
                    )
                else MoviesPage(
                    movieCardsWillWatch,
                    movieCardsWatched,
                    navigateToMovieByAlphaId,
                    navigateToMovieCategoriesByGenresId
                )
            }
        }
    }
}