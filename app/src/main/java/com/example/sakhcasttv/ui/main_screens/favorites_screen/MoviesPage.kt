package com.example.sakhcasttv.ui.main_screens.favorites_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sakhcasttv.Dimens
import com.example.sakhcasttv.model.MovieList
import com.example.sakhcasttv.ui.general.CustomListItem
import com.example.sakhcasttv.ui.main_screens.home_screen.movie.MovieItemView

@Composable
fun MoviesPage(
    movieCardsWillWatch: MovieList?,
    movieCardsWatched: MovieList?,
    navigateToMovieByAlphaId: (String) -> Unit,
    navigateToMovieCategoriesByGenresId: (String, String) -> Unit,
) {
    val categoryNames =
        mapOf("Буду смотреть" to "movie.favorite.will", "Просмотренные" to "movie.favorite.watched")

    LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
        item {
            MovieSection(
                categoryNames = categoryNames.entries.first(),
                movieList = movieCardsWillWatch,
                navigateToMovieByAlphaId = navigateToMovieByAlphaId,
                navigateToMovieCategoriesByGenresId = navigateToMovieCategoriesByGenresId
            )
        }
        item {
            MovieSection(
                categoryNames = categoryNames.entries.last(),
                movieList = movieCardsWatched,
                navigateToMovieByAlphaId = navigateToMovieByAlphaId,
                navigateToMovieCategoriesByGenresId = navigateToMovieCategoriesByGenresId
            )
        }
    }
}

@Composable
fun MovieSection(
    categoryNames: Map.Entry<String, String>,
    movieList: MovieList?,
    navigateToMovieByAlphaId: (String) -> Unit,
    navigateToMovieCategoriesByGenresId: (String, String) -> Unit
) {
    CustomListItem(
        category = categoryNames,
        isSeries = false,
        onClickWithParam = navigateToMovieCategoriesByGenresId
    )
    LazyRow(
        contentPadding = PaddingValues(Dimens.mainPadding),
        horizontalArrangement = Arrangement.spacedBy(Dimens.mainPadding),
    ) {
        if (movieList != null) {
            items(items = movieList.items, key = { it.id }) { movie ->
                MovieItemView(
                    movieCard = movie,
                    navigateToMovieByAlphaId = navigateToMovieByAlphaId,
                    modifier = Modifier.width(150.dp)
                )
            }
        }
    }
}