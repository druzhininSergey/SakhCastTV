package com.example.sakhcasttv.ui

import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sakhcasttv.CATALOG_SCREEN
import com.example.sakhcasttv.FAVORITES_SCREEN
import com.example.sakhcasttv.HOME_SCREEN
import com.example.sakhcasttv.MOVIE_CATEGORY_SCREEN
import com.example.sakhcasttv.MOVIE_VIEW
import com.example.sakhcasttv.NOTIFICATION_SCREEN
import com.example.sakhcasttv.PLAYER
import com.example.sakhcasttv.PROFILE_SCREEN
import com.example.sakhcasttv.SEARCH_SCREEN
import com.example.sakhcasttv.SERIES_CATEGORY_SCREEN
import com.example.sakhcasttv.SERIES_PLAYER
import com.example.sakhcasttv.SERIES_VIEW
import com.example.sakhcasttv.model.CurrentUser
import com.example.sakhcasttv.ui.main_screens.MainScreensViewModel
import com.example.sakhcasttv.ui.main_screens.catalog_screen.CatalogScreen
import com.example.sakhcasttv.ui.main_screens.favorites_screen.FavoritesScreen
import com.example.sakhcasttv.ui.main_screens.home_screen.HomeScreen
import com.example.sakhcasttv.ui.main_screens.notifications_screen.NotificationScreen
import com.example.sakhcasttv.ui.main_screens.notifications_screen.NotificationScreenViewModel
import com.example.sakhcasttv.ui.main_screens.search_screen.SearchScreen
import com.example.sakhcasttv.ui.movie_series_view.MovieView
import com.example.sakhcasttv.ui.profile_screen.ProfileScreen

@Composable
fun AuthNavGraph(
    navHostController: NavHostController,
    user: CurrentUser?,
    paddingValues: PaddingValues,
    onLogoutButtonPushed: () -> Unit,
    mainScreensViewModel: MainScreensViewModel = hiltViewModel()
) {
    val navigateUp = { navHostController.navigateUp() }
    val navigate = { route: String -> navHostController.navigate(route) }

    val navigateToMovieByAlphaId = { movieAlphaId: String -> navigate("$MOVIE_VIEW/$movieAlphaId") }
    val navigateToSeriesById = { seriesId: String -> navigate("$SERIES_VIEW/$seriesId") }
    val navigateToCatalogAllSeries = { navigate("$SERIES_CATEGORY_SCREEN/Все/{}") }
    val navigateToCatalogAllMovies = { navigate("$MOVIE_CATEGORY_SCREEN/Все/{}") }
    val navigateToSeriesCategoryScreen =
        { categoryName: String -> navigate("$SERIES_CATEGORY_SCREEN/$categoryName/{}") }
    val navigateToMoviesCategoryScreen =
        { categoryName: String -> navigate("$MOVIE_CATEGORY_SCREEN/$categoryName/{}") }
    val navigateToMoviePlayer =
        { hlsToSend: String, titleToSend: String, positionToSend: Int, alphaIdToSend: String ->
            navigate("$PLAYER/$hlsToSend/$titleToSend/$positionToSend/$alphaIdToSend")
        }
    val navigateToMovieCategoriesByGenresId = { genresName: String, genresId: String ->
        navigate("$MOVIE_CATEGORY_SCREEN/$genresName/$genresId")
    }
    val navigateToSeriesCategoryByType =
        { type: String, name: String -> navigate("$SERIES_CATEGORY_SCREEN/$type/$name") }
    val navigateToSeriesPlayer = {
            seasonId: String,
            seriesTitle: String,
            episodeChosenIndex: String,
            rgChosen: String,
        ->
        navigate("$SERIES_PLAYER/$seasonId/$seriesTitle/$episodeChosenIndex/$rgChosen")
    }

    NavHost(
        navController = navHostController,
        startDestination = HOME_SCREEN,
        enterTransition = { slideInVertically(initialOffsetY = { -it }) },
        exitTransition = { slideOutVertically(targetOffsetY = { it }) },
    ) {
        composable(HOME_SCREEN) {
            HomeScreen(
                navigateToMovieByAlphaId = navigateToMovieByAlphaId,
                navigateToSeriesById = navigateToSeriesById,
                navigateToCatalogAllSeries = navigateToCatalogAllSeries,
                navigateToCatalogAllMovies = navigateToCatalogAllMovies,
                allScreensHomeState = mainScreensViewModel.homeScreenState,
                loadDataToHomeScreen = mainScreensViewModel::loadDataToHomeScreen,
            )
        }
        composable(CATALOG_SCREEN) {
            CatalogScreen(
                navigateToSeriesCategoryScreen = navigateToSeriesCategoryScreen,
                navigateToMoviesCategoryScreen = navigateToMoviesCategoryScreen,
            )
        }
        composable(FAVORITES_SCREEN) {
            FavoritesScreen(
                navigateToMovieByAlphaId = navigateToMovieByAlphaId,
                navigateToSeriesById = navigateToSeriesById,
                navigateToSeriesCategoryByType = navigateToSeriesCategoryByType,
                navigateToMovieCategoriesByGenresId = navigateToMovieCategoriesByGenresId,
                allScreensFavoriteState = mainScreensViewModel.favoritesScreenState,
                loadDataToHomeScreen = mainScreensViewModel::loadDataToFavoritesScreen,
            )
        }
        composable(NOTIFICATION_SCREEN) {
            val notificationScreenViewModel: NotificationScreenViewModel = hiltViewModel()
            val notificationScreenState by notificationScreenViewModel.notificationScreenState.collectAsState()
            val allScreensNotificationsState by mainScreensViewModel.notificationScreenState.collectAsState()
            LaunchedEffect(Unit) {
                if (allScreensNotificationsState.notificationsList == null) {
                    notificationScreenViewModel.getNotifications()
                }
            }
            LaunchedEffect(notificationScreenState) {
                if (!notificationScreenState.isLoading) {
                    mainScreensViewModel.loadDataToNotificationsScreen(notificationScreenState)
                }
            }

            NotificationScreen(
                allScreensNotificationsState,
                notificationScreenViewModel::makeAllNotificationsRead,
                notificationScreenViewModel::getNotifications,
                navigateToSeriesById
            )
        }
        composable(SEARCH_SCREEN) {
            SearchScreen(
                navigateToMovieByAlphaId = navigateToMovieByAlphaId,
                navigateToSeriesById = navigateToSeriesById
            )
        }
        composable(PROFILE_SCREEN) {
            ProfileScreen(user = user, onLogoutButtonPushed = onLogoutButtonPushed)
        }
        composable("$MOVIE_VIEW/{movieId}") { backStackEntry ->
            val alphaId = backStackEntry.arguments?.getString("movieId")

            MovieView(
                alphaId = alphaId,
                navigateToMoviePlayer = navigateToMoviePlayer,
                navigateToMovieByAlphaId = navigateToMovieByAlphaId,
                navigateToMovieCategoriesByGenresId = navigateToMovieCategoriesByGenresId,
            )
        }
//        composable("$SERIES_VIEW/{seriesId}") { backStackEntry ->
//            val seriesId = backStackEntry.arguments?.getString("seriesId")?.toIntOrNull()
//            SeriesView(
//                paddingValues = paddingValues,
//                navigateUp = navigateUp,
//                seriesId = seriesId,
//                navigateToSeriesCategoryByCompany = navigateToSeriesCategoryByType,
//                navigateToSeriesCategoryScreen = navigateToSeriesCategoryScreen,
//                navigateToSeriesPlayer = navigateToSeriesPlayer
//            )
//        }
//        composable("$MOVIE_CATEGORY_SCREEN/{category}/{genresId}") { backStackEntry ->
//            val searchCategoryNameUri = backStackEntry.arguments?.getString("category") ?: "Все"
//            val searchCategoryName = Uri.decode(searchCategoryNameUri)
//            val searchGenreId = backStackEntry.arguments?.getString("genresId")
//            MovieCategoryScreen(
//                paddingValues = paddingValues,
//                searchCategoryName = searchCategoryName,
//                searchGenreId = searchGenreId,
//                navigateUp = navigateUp,
//                navigateToMovieByAlphaId = navigateToMovieByAlphaId
//            )
//        }
//        composable("$SERIES_CATEGORY_SCREEN/{category}/{name}") { backStackEntry ->
//            val categoryName = backStackEntry.arguments?.getString("category") ?: "Все"
//            val name = backStackEntry.arguments?.getString("name") ?: ""
//            SeriesCategoryScreen(
//                paddingValues = paddingValues,
//                categoryName = categoryName,
//                name = name,
//                navigateUp = navigateUp,
//                navigateToSeriesById = navigateToSeriesById,
//            )
//        }
//        composable("$PLAYER/{hls}/{title}/{position}/{movieAlphaId}") { backStackEntry ->
//            val hls = backStackEntry.arguments?.getString("hls") ?: ""
//            val title = backStackEntry.arguments?.getString("title") ?: ""
//            val positionStr = backStackEntry.arguments?.getString("position") ?: "0"
//            val position = positionStr.toInt()
//            val movieAlphaId = backStackEntry.arguments?.getString("movieAlphaId") ?: ""
//            Player2(
//                hls = hls,
//                title = title,
//                position = position,
//                movieAlphaId = movieAlphaId,
//                navigateUp = navigateUp
//            )
//        }
//        composable("$SERIES_PLAYER/{seasonId}/{seriesTitle}/{episodeChosenIndex}/{rgChosen}") { backStackEntry ->
//            val seasonId = backStackEntry.arguments?.getString("seasonId") ?: ""
//            val seriesTitle = backStackEntry.arguments?.getString("seriesTitle") ?: ""
//            val episodeChosenIndex =
//                backStackEntry.arguments?.getString("episodeChosenIndex") ?: "1"
//            val episodeChosenIndexInt = episodeChosenIndex.toInt()
//            val rgChosen = backStackEntry.arguments?.getString("rgChosen") ?: ""
//
//            val initialized = rememberSaveable { mutableStateOf(false) }
//            val seriesPlayerViewModel: SeriesPlayerViewModel = hiltViewModel()
//            LaunchedEffect(seasonId) {
//                if (!initialized.value) {
//                    seriesPlayerViewModel.setSeriesData(
//                        seasonId,
//                        seriesTitle,
//                        episodeChosenIndexInt,
//                        rgChosen
//                    )
//                    seriesPlayerViewModel.getPlaylist(seasonId, rgChosen)
//                    initialized.value = true
//                }
//            }
//            val isPlayListLoaded by seriesPlayerViewModel.isPlaylistLoaded.collectAsState()
//            val isDataLoaded by seriesPlayerViewModel.isDataLoaded.collectAsState()
//            val seriesState by seriesPlayerViewModel.seriesWatchState.collectAsState()
//
//            LaunchedEffect(isDataLoaded) {
//                if (isDataLoaded) seriesPlayerViewModel.startPlayer()
//            }
//
//            SeriesPlayer(
//                navigateUp = navigateUp,
//                isPlayListLoaded,
//                isDataLoaded,
//                seriesState,
//                seriesPlayerViewModel.player,
//                seriesPlayerViewModel::onEpisodeChanged,
//            )
//        }
    }
}