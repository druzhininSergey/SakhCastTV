package com.example.sakhcasttv.ui.main_screens.main_screen_tabrow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakhcasttv.data.repository.SakhCastRepository
import com.example.sakhcasttv.model.LastWatched
import com.example.sakhcasttv.model.MovieList
import com.example.sakhcasttv.model.NotificationList
import com.example.sakhcasttv.model.SeriesList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreensViewModel @Inject constructor(private val sakhCastRepository: SakhCastRepository)
    : ViewModel() {

    private var _favoritesScreenState = MutableStateFlow(FavoritesScreenState())
    val favoritesScreenState = _favoritesScreenState.asStateFlow()

    private var _notificationScreenState = MutableStateFlow(NotificationScreenState())
    val notificationScreenState = _notificationScreenState.asStateFlow()

    private val _homeScreenState = MutableStateFlow(HomeScreenState())
    val homeScreenState = _homeScreenState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        getHomeScreenData()
        getFavorites()
        getNotifications()
    }

    data class HomeScreenState(
        var seriesList: SeriesList? = null,
        var moviesList: MovieList? = null,
        var lastWatched: LastWatched? = null,
    )

    data class NotificationScreenState(
        var notificationsList: NotificationList? = null,
        var isLoading: Boolean = true,
    )

    data class FavoritesScreenState(
        var seriesCardWatching: SeriesList? = null,
        var seriesCardWillWatch: SeriesList? = null,
        var seriesCardFinishedWatching: SeriesList? = null,
        var seriesCardWatched: SeriesList? = null,
        var movieCardsWillWatch: MovieList? = null,
        var movieCardsWatched: MovieList? = null,
        var isLoading: Boolean = true,
    )

    fun refreshHomeScreenData() {
        viewModelScope.launch {
            try {
                val lastWatched = sakhCastRepository.getContinueWatchMovieAndSeries()
                val seriesList =
                    sakhCastRepository.getSeriesListByCategoryName(categoryName = "all", page = 0)
                val moviesList =
                    sakhCastRepository.getMoviesListByCategoryName(categoryName = "all", page = 0)

                _homeScreenState.update { currentState ->
                    currentState.copy(
                        lastWatched = lastWatched,
                        seriesList = seriesList,
                        moviesList = moviesList,
                    )
                }
            } catch (e: Exception) {
                _isLoading.update { false }
            }
        }
    }

    private fun getHomeScreenData() {
        viewModelScope.launch {
            _isLoading.update { true }
            try {
                val lastWatched = sakhCastRepository.getContinueWatchMovieAndSeries()
                val seriesList =
                    sakhCastRepository.getSeriesListByCategoryName(categoryName = "all", page = 0)
                val moviesList =
                    sakhCastRepository.getMoviesListByCategoryName(categoryName = "all", page = 0)

                _homeScreenState.update { currentState ->
                    currentState.copy(
                        lastWatched = lastWatched,
                        seriesList = seriesList,
                        moviesList = moviesList,
                    )
                }
                _isLoading.update { false }
            } catch (e: Exception) {
                _isLoading.update { false }
            }
        }
    }

    private fun getFavorites() {
        _favoritesScreenState.update { currentState ->
            currentState.copy(isLoading = true)
        }
        try {
            viewModelScope.launch {
                val seriesCardWatching = sakhCastRepository.getSeriesFavorites("watching")
                val seriesCardWillWatch = sakhCastRepository.getSeriesFavorites("will")
                val seriesCardFinishedWatching = sakhCastRepository.getSeriesFavorites("stopped")
                val seriesCardWatched = sakhCastRepository.getSeriesFavorites("watched")
                val movieCardsWillWatch = sakhCastRepository.getMovieFavorites(kind = "will")
                val movieCardsWatched = sakhCastRepository.getMovieFavorites(kind = "watched")

                _favoritesScreenState.update { currentState ->
                    currentState.copy(
                        seriesCardWatching = seriesCardWatching,
                        seriesCardWillWatch = seriesCardWillWatch,
                        seriesCardFinishedWatching = seriesCardFinishedWatching,
                        seriesCardWatched = seriesCardWatched,
                        movieCardsWillWatch = movieCardsWillWatch,
                        movieCardsWatched = movieCardsWatched,
                        isLoading = false
                    )
                }
            }
        } catch (e: Exception) {
            _favoritesScreenState.update { it.copy(isLoading = false) }
        }
    }

    private fun getNotifications() {
        viewModelScope.launch {
            _notificationScreenState.update { currentState ->
                currentState.copy(isLoading = true)
            }
            try {
                val notificationList = sakhCastRepository.getNotificationsList()
                _notificationScreenState.update {
                    it.copy(notificationsList = notificationList, isLoading = false)
                }
            } catch (e: Exception) {
                _notificationScreenState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun makeAllNotificationsRead() {
        viewModelScope.launch {
            sakhCastRepository.makeAllNotificationsRead()
            val currentNotifications =
                notificationScreenState.value.notificationsList?.items ?: emptyList()
            val updatedNotifications = currentNotifications.map { it.copy(acknowledge = true) }
            _notificationScreenState.update { currentState ->
                currentState.copy(
                    notificationsList = NotificationList(
                        amount = updatedNotifications.size,
                        items = updatedNotifications
                    )
                )
            }

        }
    }

}