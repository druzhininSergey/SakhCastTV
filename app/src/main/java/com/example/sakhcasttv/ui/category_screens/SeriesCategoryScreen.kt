package com.example.sakhcasttv.ui.category_screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
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
import com.example.sakhcasttv.ui.main_screens.home_screen.series.SeriesItemView
import kotlinx.coroutines.flow.filterNotNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesCategoryScreen(
    paddingValues: PaddingValues,
    categoryName: String,
    name: String,
    navigateToSeriesById: (String) -> Unit,
    seriesCategoryScreenViewModel: SeriesCategoryScreenViewModel = hiltViewModel()
) {

    val isLoading by seriesCategoryScreenViewModel.isLoading.collectAsStateWithLifecycle()
    val seriesPagingDataFlow by seriesCategoryScreenViewModel.seriesPagingData.collectAsStateWithLifecycle()

    LaunchedEffect(categoryName) {
        seriesCategoryScreenViewModel.initCategory(categoryName)
    }

    val categoryNameTitle = if (categoryName.endsWith(".company")) name
    else if (categoryName.endsWith(".favorite")) name
    else categoryName.replaceFirstChar { it.uppercase() }

    val lazyGridState = rememberLazyGridState()
    val isDataLoaded = remember { mutableStateOf(false) }
    val seriesPagingData = seriesPagingDataFlow?.collectAsLazyPagingItems()

    LaunchedEffect(seriesPagingData) {
        snapshotFlow { seriesPagingData?.itemCount }
            .filterNotNull()
            .collect { itemCount ->
                isDataLoaded.value = itemCount > 0
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    textAlign = TextAlign.Center,
                    text = categoryNameTitle,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.background),
        )
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            seriesPagingDataFlow != null -> {
                val seriesPagingItems = seriesPagingDataFlow!!.collectAsLazyPagingItems()

                if (isDataLoaded.value) {
                    LazyVerticalGrid(
                        state = lazyGridState,
                        columns = GridCells.Adaptive(minSize = 150.dp),
                        modifier = Modifier
                            .padding(bottom = paddingValues.calculateBottomPadding())
                            .background(MaterialTheme.colorScheme.background)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.mainPadding),
                        verticalArrangement = Arrangement.spacedBy(Dimens.mainPadding),
                        contentPadding = PaddingValues(Dimens.mainPadding)
                    ) {
                        items(count = seriesPagingItems.itemCount) { index ->
                            seriesPagingItems[index]?.let {
                                SeriesItemView(
                                    seriesCard = it,
                                    isFavoriteScreen = categoryName.endsWith(".favorite"),
                                    navigateToSeriesById = navigateToSeriesById
                                )
                            }
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