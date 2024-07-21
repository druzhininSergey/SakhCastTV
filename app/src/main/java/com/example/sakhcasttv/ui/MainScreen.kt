package com.example.sakhcasttv.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.sakhcasttv.model.CurrentUser
import com.example.sakhcasttv.ui.log_in_screen.LogInScreen
import com.example.sakhcasttv.ui.log_in_screen.LogInScreenViewModel

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
        AuthenticatedMainScreen(navController, user)
    }
}

@Composable
fun AuthenticatedMainScreen(
    navController: NavHostController,
    user: CurrentUser?,
) {
    AuthNavGraph(
        navHostController = navController,
        user,
    )
}
