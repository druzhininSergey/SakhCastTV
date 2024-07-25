package com.example.sakhcasttv.ui.main_screens.top_app_bar

import com.example.sakhcasttv.CATALOG_SCREEN
import com.example.sakhcasttv.FAVORITES_SCREEN
import com.example.sakhcasttv.HOME_SCREEN
import com.example.sakhcasttv.NOTIFICATION_SCREEN
import com.example.sakhcasttv.SEARCH_SCREEN

sealed class TopBarItem(
    val title: String,
    val route: String,
) {
    data object HomeScreenData : TopBarItem("Главная", HOME_SCREEN)
    data object CatalogScreenData : TopBarItem("Каталог", CATALOG_SCREEN)
    data object FavoritesScreenData : TopBarItem("Избранное", FAVORITES_SCREEN)
    data object NotificationsScreenData : TopBarItem("Уведомления", NOTIFICATION_SCREEN)
    data object SearchScreenData : TopBarItem("Поиск", SEARCH_SCREEN)
}

