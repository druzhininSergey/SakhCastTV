package com.example.sakhcasttv.ui.category_screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.sakhcasttv.Dimens
import com.example.sakhcasttv.ui.main_screens.home_screen.movie.MovieItemView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieCategoryScreen(
    paddingValues: PaddingValues,
    searchCategoryName: String?,
    searchGenreId: String?,
    navigateToMovieByAlphaId: (String) -> Unit,
    movieCategoryScreenViewModel: MovieCategoryScreenViewModel = hiltViewModel()
) {
    val isLoading by movieCategoryScreenViewModel.isLoading.collectAsStateWithLifecycle()
    val moviePagingDataFlow by movieCategoryScreenViewModel.moviePagingData.collectAsStateWithLifecycle()

    LaunchedEffect(searchCategoryName, searchGenreId) {
        if (searchGenreId == null || searchGenreId == "{}")
            movieCategoryScreenViewModel.initCategory(searchCategoryName)
        else movieCategoryScreenViewModel.initCategory(searchGenreId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CenterAlignedTopAppBar(
            title = {
                if (searchCategoryName != null) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = searchCategoryName.replaceFirstChar { it.uppercaseChar() },
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.background),
        )

        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            moviePagingDataFlow != null -> {
                val moviePagingItems = moviePagingDataFlow!!.collectAsLazyPagingItems()

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 150.dp),
                    modifier = Modifier
                        .padding(bottom = paddingValues.calculateBottomPadding())
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.mainPadding),
                    verticalArrangement = Arrangement.spacedBy(Dimens.mainPadding),
                    contentPadding = PaddingValues(Dimens.mainPadding)
                ) {
                    items(count = moviePagingItems.itemCount) { index ->
                        moviePagingItems[index]?.let {
                            MovieItemView(
                                movieCard = it,
                                navigateToMovieByAlphaId = navigateToMovieByAlphaId
                            )
                        }
                    }
                }
            }

            else -> {
                Text(
                    "Нет доступных данных",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}