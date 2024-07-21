package com.example.sakhcasttv.ui.main_screens.favorites_screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.TabRow
import com.example.sakhcasttv.ui.main_screens.main_screen_tabrow.MainScreensViewModel
import com.example.sakhcasttv.ui.main_screens.main_screen_tabrow.MenuItem
import com.example.sakhcasttv.ui.main_screens.main_screen_tabrow.NavigationTabItem
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavoritesScreen(
    navigateToMovieByAlphaId: (String) -> Unit,
    navigateToSeriesById: (String) -> Unit,
    navigateToSeriesCategoryByType: (String, String) -> Unit,
    navigateToMovieCategoriesByGenresId: (String, String) -> Unit,
    favoritesScreenState: StateFlow<MainScreensViewModel.FavoritesScreenState>,
//    loadDataToHomeScreen: (FavoritesScreenViewModel.FavoritesScreenState) -> Unit,
) {
    val favoritesScreenStateCollected by favoritesScreenState.collectAsState()

    val seriesCardWatching = favoritesScreenStateCollected.seriesCardWatching
    val seriesCardWillWatch = favoritesScreenStateCollected.seriesCardWillWatch
    val seriesCardFinishedWatching =
        favoritesScreenStateCollected.seriesCardFinishedWatching
    val seriesCardWatched = favoritesScreenStateCollected.seriesCardWatched
    val movieCardsWillWatch = favoritesScreenStateCollected.movieCardsWillWatch
    val movieCardsWatched = favoritesScreenStateCollected.movieCardsWatched

    val pages = listOf("Сериалы", "Фильмы")
    var tabIndex by remember {
        mutableIntStateOf(0)
    }
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState {
        pages.size
    }
    var selectedTabIndex by remember { mutableIntStateOf(pagerState.currentPage) }
    LaunchedEffect(tabIndex) {
        pagerState.animateScrollToPage(tabIndex)
    }
    LaunchedEffect(pagerState.currentPage) {
        tabIndex = pagerState.currentPage
    }

    Box {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, bottom = 10.dp),
                contentAlignment = Alignment.Center
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
                    pages.forEachIndexed { index, title ->
                        NavigationTabItem(
                            item = MenuItem(id = title, text = title),
                            isSelected = selectedTabIndex == index,
                            onMenuSelected = {
                                selectedTabIndex = index
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false
            ) { index ->
                if (index == 0)
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