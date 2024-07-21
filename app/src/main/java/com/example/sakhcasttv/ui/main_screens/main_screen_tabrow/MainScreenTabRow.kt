package com.example.sakhcasttv.ui.main_screens.main_screen_tabrow

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Tab
import androidx.tv.material3.TabDefaults
import androidx.tv.material3.TabRow
import androidx.tv.material3.TabRowScope
import androidx.tv.material3.Text
import com.example.sakhcasttv.ui.main_screens.catalog_screen.CatalogScreen
import com.example.sakhcasttv.ui.main_screens.favorites_screen.FavoritesScreen
import com.example.sakhcasttv.ui.main_screens.home_screen.HomeScreen
import com.example.sakhcasttv.ui.main_screens.notifications_screen.NotificationScreen
import com.example.sakhcasttv.ui.main_screens.search_screen.SearchScreen
import com.example.sakhcasttv.ui.profile_screen.ProfileScreen
import kotlinx.coroutines.launch

@Composable
fun MainScreenTabRow(
    navigateToMovieByAlphaId: (String) -> Unit,
    navigateToSeriesById: (String) -> Unit,
    navigateToCatalogAllSeries: () -> Unit,
    navigateToCatalogAllMovies: () -> Unit,
    navigateToSeriesCategoryScreen: (String) -> Unit,
    navigateToMoviesCategoryScreen: (String) -> Unit,
    navigateToSeriesCategoryByType: (String, String) -> Unit,
    navigateToMovieCategoriesByGenresId: (String, String) -> Unit,
    mainScreensViewModel: MainScreensViewModel = hiltViewModel(),
) {
    val homeScreenState = mainScreensViewModel.homeScreenState
    val isLoading = mainScreensViewModel.isLoading
    val favoritesScreenState = mainScreensViewModel.favoritesScreenState
    val notificationScreenState = mainScreensViewModel.notificationScreenState
    val pages = listOf("Главная", "Каталог", "Избранное", "Уведомления", "Поиск", "Профиль")
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState { 6 }
    var selectedTabIndex by remember { mutableIntStateOf(pagerState.currentPage) }

    Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp),
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


        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
        ) { index ->
            when (index) {
                0 -> HomeScreen(
                    navigateToMovieByAlphaId = navigateToMovieByAlphaId,
                    navigateToSeriesById = navigateToSeriesById,
                    navigateToCatalogAllSeries = navigateToCatalogAllSeries,
                    navigateToCatalogAllMovies = navigateToCatalogAllMovies,
                    homeScreenState = homeScreenState,
                    isLoading = isLoading
                )

                1 -> CatalogScreen(
                    navigateToSeriesCategoryScreen = navigateToSeriesCategoryScreen,
                    navigateToMoviesCategoryScreen = navigateToMoviesCategoryScreen,
                )

                2 -> FavoritesScreen(
                    navigateToMovieByAlphaId = navigateToMovieByAlphaId,
                    navigateToSeriesById = navigateToSeriesById,
                    navigateToSeriesCategoryByType = navigateToSeriesCategoryByType,
                    navigateToMovieCategoriesByGenresId = navigateToMovieCategoriesByGenresId,
                    favoritesScreenState = favoritesScreenState
                )

                3 -> NotificationScreen(
                    notificationScreenState = notificationScreenState,
                    navigateToSeriesById = navigateToSeriesById,
                )

                4 -> SearchScreen(
                    navigateToMovieByAlphaId = navigateToMovieByAlphaId,
                    navigateToSeriesById = navigateToSeriesById
                )

                5 -> ProfileScreen()
            }
        }
    }
}

@Composable
fun TabRowScope.NavigationTabItem(
    item: MenuItem,
    isSelected: Boolean,
    enabled: Boolean = true,
    onMenuSelected: ((menuItem: MenuItem) -> Unit)?,
) {
    val mutableInteractionSource = remember { MutableInteractionSource() }
    val isFocused by mutableInteractionSource.collectIsFocusedAsState()

    Tab(
        selected = isSelected,
        enabled = enabled,
        onClick = {
            onMenuSelected?.invoke(item)
        },
        onFocus = {},
        interactionSource = mutableInteractionSource,
        colors = TabDefaults.pillIndicatorTabColors(
            selectedContentColor = MaterialTheme.colorScheme.onSurface,
            focusedContentColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .background(
                color = when {
                    isFocused -> MaterialTheme.colorScheme.inverseSurface
                    isSelected -> Color.DarkGray
                    else -> Color.Transparent
                },
                shape = MaterialTheme.shapes.small,
            )
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.onBackground,
                shape = MaterialTheme.shapes.small,
            )
    ) {
        Text(
            text = item.text,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            color = when {
                isFocused -> Color.Black
                isSelected -> MaterialTheme.colorScheme.onBackground
                else -> MaterialTheme.colorScheme.onBackground
            },
            fontSize = 12.sp,
        )
    }
}

data class MenuItem(
    val id: String,
    val text: String,
    val icon: ImageVector? = null
)

