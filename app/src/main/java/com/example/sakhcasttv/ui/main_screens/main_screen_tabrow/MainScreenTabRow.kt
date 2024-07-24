package com.example.sakhcasttv.ui.main_screens.main_screen_tabrow

//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.pager.HorizontalPager
//import androidx.compose.foundation.pager.rememberPagerState
//import androidx.compose.runtime.mutableIntStateOf
//import androidx.compose.ui.Alignment
//import androidx.hilt.navigation.compose.hiltViewModel

//import com.example.sakhcasttv.ui.main_screens.catalog_screen.CatalogScreen
//import com.example.sakhcasttv.ui.main_screens.favorites_screen.FavoritesScreen
//import com.example.sakhcasttv.ui.main_screens.home_screen.HomeScreen
//import com.example.sakhcasttv.ui.main_screens.notifications_screen.NotificationScreen
//import com.example.sakhcasttv.ui.main_screens.search_screen.SearchScreen
//import com.example.sakhcasttv.ui.profile_screen.ProfileScreen
//
//@Composable
//fun MainScreenTabRow(
//    navigateToMovieByAlphaId: (String) -> Unit,
//    navigateToSeriesById: (String) -> Unit,
//    navigateToCatalogAllSeries: () -> Unit,
//    navigateToCatalogAllMovies: () -> Unit,
//    navigateToSeriesCategoryScreen: (String) -> Unit,
//    navigateToMoviesCategoryScreen: (String) -> Unit,
//    navigateToSeriesCategoryByType: (String, String) -> Unit,
//    navigateToMovieCategoriesByGenresId: (String, String) -> Unit,
//    mainScreensViewModel: MainScreensViewModel = hiltViewModel(),
//) {
//    val homeScreenState = mainScreensViewModel.homeScreenState
//    val isLoading = mainScreensViewModel.isLoading
//    val favoritesScreenState = mainScreensViewModel.favoritesScreenState
//    val notificationScreenState = mainScreensViewModel.notificationScreenState
//    val listItems = listOf(
//        TabRowNavigationItem.HomeScreen,
//        TabRowNavigationItem.CatalogScreen,
//        TabRowNavigationItem.FavoritesScreen,
//        TabRowNavigationItem.NotificationsScreen,
//        TabRowNavigationItem.SearchScreen
//    )
//    val pagerState = rememberPagerState { 6 }
//    val selectedTabIndex by remember { mutableIntStateOf(pagerState.currentPage) }
//
//    Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 5.dp),
//            contentAlignment = Alignment.Center
//        ) {
////            TabRow(
////                selectedTabIndex = selectedTabIndex,
////                indicator = { _, _ ->
////                    Box(
////                        modifier = Modifier
////                            .clip(MaterialTheme.shapes.small)
////                            .background(Color.Transparent)
////                    )
////                },
////            ) {
//            LazyRow(
//                modifier = Modifier.padding(),
//                contentPadding = PaddingValues(horizontal = 16.dp),
//                horizontalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                items(listItems) {
//                    TopAppTab(navItem = it)
//                }
//            }
////                listItems.forEach { navItem ->
////                    TopAppTab(navItem = navItem)
////                }
//                listItems.forEachIndexed { index, navItem ->
//                    NavigationTabItem(
//                        item = navItem,
//                        isSelected = selectedTabIndex == index,
//                        onMenuSelected = {
//                            selectedTabIndex = index
//                            scope.launch {
//                                pagerState.scrollToPage(index)
//                            }
//                        },
//                    )
//                }
////            }
//        }
//
//        HorizontalPager(
//            state = pagerState,
//            userScrollEnabled = false,
//        ) { index ->
//            when (index) {
//                0 -> HomeScreen(
//                    navigateToMovieByAlphaId = navigateToMovieByAlphaId,
//                    navigateToSeriesById = navigateToSeriesById,
//                    navigateToCatalogAllSeries = navigateToCatalogAllSeries,
//                    navigateToCatalogAllMovies = navigateToCatalogAllMovies,
//                    homeScreenState = homeScreenState,
//                    isLoading = isLoading
//                )
//
//                1 -> CatalogScreen(
//                    navigateToSeriesCategoryScreen = navigateToSeriesCategoryScreen,
//                    navigateToMoviesCategoryScreen = navigateToMoviesCategoryScreen,
//                )
//
//                2 -> FavoritesScreen(
//                    navigateToMovieByAlphaId = navigateToMovieByAlphaId,
//                    navigateToSeriesById = navigateToSeriesById,
//                    navigateToSeriesCategoryByType = navigateToSeriesCategoryByType,
//                    navigateToMovieCategoriesByGenresId = navigateToMovieCategoriesByGenresId,
//                    favoritesScreenState = favoritesScreenState
//                )
//
//                3 -> NotificationScreen(
//                    notificationScreenState = notificationScreenState,
//                    navigateToSeriesById = navigateToSeriesById,
//                )
//
//                4 -> SearchScreen(
//                    navigateToMovieByAlphaId = navigateToMovieByAlphaId,
//                    navigateToSeriesById = navigateToSeriesById
//                )
//
//                5 -> ProfileScreen()
//            }
//        }
//    }
//}
//



