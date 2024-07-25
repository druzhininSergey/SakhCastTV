package com.example.sakhcasttv.ui.main_screens.home_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakhcasttv.data.repository.SakhCastRepository
import com.example.sakhcasttv.model.LastWatched
import com.example.sakhcasttv.model.MovieList
import com.example.sakhcasttv.model.SeriesList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val sakhCastRepository: SakhCastRepository) :
    ViewModel() {

    private val _homeScreenState = MutableStateFlow(HomeScreenState())
    val homeScreenState = _homeScreenState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    data class HomeScreenState(
        var seriesList: SeriesList? = null,
        var moviesList: MovieList? = null,
        var lastWatched: LastWatched? = null,
    )

    fun refreshData() {
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

    fun getAllScreenData() {
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

}