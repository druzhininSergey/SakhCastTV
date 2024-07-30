package com.example.sakhcasttv.ui.main_screens.home_screen.movie

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sakhcasttv.Dimens
import com.example.sakhcasttv.model.MovieList
import com.example.sakhcasttv.ui.general.CustomListItem


@Composable
fun MovieCategoryView(
    movieList: MovieList,
    navigateToMovieByAlphaId: (String) -> Unit,
    navigateToCatalogAllMovies: () -> Unit
) {
    CustomListItem(text = "Все фильмы", isSeries = false, onClick = navigateToCatalogAllMovies)
    LazyRow(
        modifier = Modifier.padding(vertical = Dimens.mainPadding),
        contentPadding = PaddingValues(start = Dimens.mainPadding, end = Dimens.mainPadding),
        horizontalArrangement = Arrangement.spacedBy(Dimens.mainPadding)
    ) {
        items(
            items = movieList.items,
            key = { it.id }
        ) { item ->
            MovieItemView(
                movieCard = item,
                navigateToMovieByAlphaId = navigateToMovieByAlphaId,
                modifier = Modifier.width(150.dp)
            )
        }
    }
}
