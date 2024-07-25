package com.example.sakhcasttv.ui.main_screens.catalog_screen

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.TabRow
import com.example.sakhcasttv.Genres
import com.example.sakhcasttv.ui.general.MenuItem
import com.example.sakhcasttv.ui.general.NavigationTabItem
import kotlinx.coroutines.launch

@Composable
fun CatalogScreen(
    navigateToSeriesCategoryScreen: (String) -> Unit,
    navigateToMoviesCategoryScreen: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val pages = listOf("Сериалы", "Фильмы")
    var tabIndex by remember {
        mutableIntStateOf(0)
    }
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

    Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
//                .background(MaterialTheme.colorScheme.background)
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
                pages.forEachIndexed { index, page ->
                    NavigationTabItem(
                        item = MenuItem(id = page, text = page),
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
            userScrollEnabled = false,
        ) { index ->
            val categories =
                if (index == 0) Genres.catalogScreenSeriesGenres
                else Genres.catalogScreenMoviesGenres
            CatalogList(
                categories,
                tabIndex,
                navigateToMoviesCategoryScreen,
                navigateToSeriesCategoryScreen
            )
        }
    }
}