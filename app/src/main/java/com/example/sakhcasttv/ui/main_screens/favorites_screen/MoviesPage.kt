package com.example.sakhcasttv.ui.main_screens.favorites_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sakhcasttv.Dimens
import com.example.sakhcasttv.model.MovieList
import com.example.sakhcasttv.ui.main_screens.home_screen.movie.MovieItemView

@Composable
fun MoviesPage(
    movieCardsWillWatch: MovieList?,
    movieCardsWatched: MovieList?,
    navigateToMovieByAlphaId: (String) -> Unit,
    navigateToMovieCategoriesByGenresId: (String, String) -> Unit,
) {
    Column {
        MovieSection(
            title = "Буду смотреть",
            genreId = "movie.favorite.will",
            movieList = movieCardsWillWatch,
            navigateToMovieByAlphaId = navigateToMovieByAlphaId,
            navigateToMovieCategoriesByGenresId = navigateToMovieCategoriesByGenresId
        )

        MovieSection(
            title = "Просмотренные",
            genreId = "movie.favorite.watched",
            movieList = movieCardsWatched,
            navigateToMovieByAlphaId = navigateToMovieByAlphaId,
            navigateToMovieCategoriesByGenresId = navigateToMovieCategoriesByGenresId
        )
    }
}

@Composable
fun MovieSection(
    title: String,
    genreId: String,
    movieList: MovieList?,
    navigateToMovieByAlphaId: (String) -> Unit,
    navigateToMovieCategoriesByGenresId: (String, String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier
                .padding(
                    horizontal = Dimens.mainPadding,
                    vertical = Dimens.mainPadding
                )
                .clickable { navigateToMovieCategoriesByGenresId(title, genreId) },
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Icon(
            modifier = Modifier.padding(top = 4.dp),
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null
        )
    }
    LazyRow(
        contentPadding = PaddingValues(horizontal = Dimens.mainPadding),
        horizontalArrangement = Arrangement.spacedBy(Dimens.mainPadding)
    ) {
        if (movieList != null) {
            items(items = movieList.items, key = { it.id }) { movie ->
                MovieItemView(
                    movieCard = movie,
                    navigateToMovieByAlphaId = navigateToMovieByAlphaId
                )
            }
        }
    }
}