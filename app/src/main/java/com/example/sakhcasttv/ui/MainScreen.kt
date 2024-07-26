package com.example.sakhcasttv.ui

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sakhcasttv.MOVIE_VIEW
import com.example.sakhcasttv.PLAYER
import com.example.sakhcasttv.SERIES_PLAYER
import com.example.sakhcasttv.SERIES_VIEW
import com.example.sakhcasttv.model.CurrentUser
import com.example.sakhcasttv.ui.log_in_screen.LogInScreen
import com.example.sakhcasttv.ui.log_in_screen.LogInScreenViewModel
import com.example.sakhcasttv.ui.main_screens.top_app_bar.TvTopBar

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    val logInScreenViewModel: LogInScreenViewModel = hiltViewModel()
    val loginScreenState = logInScreenViewModel.userDataState.observeAsState(
        LogInScreenViewModel.UserDataState()
    )
    LaunchedEffect(logInScreenViewModel) {
        logInScreenViewModel.checkLoggedUser()
        logInScreenViewModel.checkTokenExist()
    }
    val isLogged = loginScreenState.value.isLogged
    val user = loginScreenState.value.currentUser

    if (isLogged == false || isLogged == null) {
        LogInScreen(navController)
    } else {
        AuthenticatedMainScreen(navController, user, logInScreenViewModel::onLogoutButtonPushed)
    }
}

@Composable
fun AuthenticatedMainScreen(
    navController: NavHostController,
    user: CurrentUser?,
    onLogoutButtonPushed: () -> Unit,
) {
    val backStackState = navController.currentBackStackEntryAsState().value
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination?.route

//    val isTopBarVisible = remember(key1 = backStackState) {
//        currentDestination != "$SERIES_CATEGORY_SCREEN/{category}/{name}" &&
//                currentDestination != "$MOVIE_CATEGORY_SCREEN/{category}/{genresId}" &&
//                currentDestination != "$MOVIE_VIEW/{movieId}" &&
//                currentDestination != "$SERIES_VIEW/{seriesId}" &&
//                currentDestination != SEARCH_SCREEN &&
//                currentDestination != "$PLAYER/{hls}/{title}/{position}/{movieAlphaId}" &&
//                currentDestination != "$SERIES_PLAYER/{seasonId}/{seriesTitle}/{episodeChosenIndex}/{rgChosen}"
//    }
    val isTopBarVisible = remember(key1 = backStackState) {
        currentDestination != "$MOVIE_VIEW/{movieId}" &&
                currentDestination != "$SERIES_VIEW/{seriesId}" &&
                currentDestination != "$PLAYER/{hls}/{title}/{position}/{movieAlphaId}" &&
                currentDestination != "$SERIES_PLAYER/{seasonId}/{seriesTitle}/{episodeChosenIndex}/{rgChosen}"
    }

    Scaffold(
        topBar = { if (isTopBarVisible) TvTopBar(navController = navController, user) }
    ) { paddingValues ->


        AuthNavGraph(
            navHostController = navController,
            user = user,
            paddingValues = paddingValues,
            onLogoutButtonPushed = onLogoutButtonPushed
        )
    }

}