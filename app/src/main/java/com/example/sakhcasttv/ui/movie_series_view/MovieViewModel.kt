package com.example.sakhcasttv.ui.movie_series_view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakhcasttv.data.repository.SakhCastRepository
import com.example.sakhcasttv.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val sakhCastRepository: SakhCastRepository,
) : ViewModel() {

    private var _movieState = MutableLiveData(MovieState())
    val movieState: LiveData<MovieState> = _movieState

    private val _position = MutableStateFlow(0)
    val position: StateFlow<Int> = _position

    data class MovieState(
        var movie: Movie? = null,
        var isFavorite: Boolean? = null,
    )

    fun getMoviePosition(alphaId: String) {
        if (_position.value == 0) {
            viewModelScope.launch {
                val position = sakhCastRepository.getMoviePosition(alphaId) ?: 0
                _position.value = position
            }
        }
    }

    fun getFullMovieWithRecommendations(alphaId: String) {
        viewModelScope.launch {
            val movie = sakhCastRepository.getMovieByAlphaId(alphaId)
            val isFavorite = movie?.userFavourite?.isFav
            _movieState.value = movieState.value?.copy(
                movie = movie,
                isFavorite = isFavorite
            )
        }
    }

    fun onFavoriteButtonPushed(kind: String) {
        viewModelScope.launch {
            val currentFavType = _movieState.value?.movie?.userFavourite?.favKind
            val movieAlphaId = movieState.value?.movie?.idAlpha ?: "0"
            if (kind == "delete") {
                val response = sakhCastRepository.deleteMovieFromFavorites(movieAlphaId)
                if (response?.result == true) _movieState.value =
                    movieState.value?.copy(isFavorite = false)
            } else if (_movieState.value?.isFavorite == true && kind != currentFavType) {
                val response =
                    sakhCastRepository.changeMovieFavoritesType(
                        movieAlphaId = movieAlphaId,
                        kind = kind
                    )
                if (response?.result == true) _movieState.value =
                    movieState.value?.copy(isFavorite = true)
            } else {
                val response =
                    sakhCastRepository.putMovieInFavorites(movieAlphaId = movieAlphaId, kind = kind)
                if (response?.result == true) _movieState.value =
                    movieState.value?.copy(isFavorite = true)
            }
        }
    }
}