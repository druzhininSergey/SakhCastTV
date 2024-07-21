package com.example.sakhcasttv.ui.main_screens.favorites_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.sakhcasttv.Colors
import com.example.sakhcasttv.Dimens
import com.example.sakhcasttv.model.SeriesList
import com.example.sakhcasttv.ui.general.CustomListItem
import com.example.sakhcasttv.ui.main_screens.home_screen.series.SeriesItemView

@Composable
fun SeriesPage(
    seriesCardWatching: SeriesList?,
    seriesCardWillWatch: SeriesList?,
    seriesCardFinishedWatching: SeriesList?,
    seriesCardWatched: SeriesList?,
    navigateToSeriesById: (String) -> Unit,
    navigateToSeriesCategoryByType: (String, String) -> Unit,
) {
    val isFirstItemVisible = remember { mutableStateOf(false) }
    val categoryNames = mapOf(
        "Cмотрю" to "watching",
        "Буду смотреть" to "will",
        "Перестал" to "stopped",
        "Досмотрел" to "watched"
    )
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
    ) {
        categoryNames.entries.forEachIndexed { indexCategories, category ->
            CustomListItem(category = category, onClickWithParam = navigateToSeriesCategoryByType)
            if (
                seriesCardWatching != null &&
                seriesCardWillWatch != null &&
                seriesCardFinishedWatching != null &&
                seriesCardWatched != null
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = Dimens.mainPadding),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.mainPadding)
                ) {

                    itemsIndexed(
                        items = when (indexCategories) {
                            0 -> seriesCardWatching.items
                            1 -> seriesCardWillWatch.items
                            2 -> seriesCardFinishedWatching.items
                            3 -> seriesCardWatched.items
                            else -> emptyList()
                        },
                        key = { _, series -> series.id }
                    ) { _, series ->
                        SeriesItemView(
                            seriesCard = series,
                            navigateToSeriesById = navigateToSeriesById,
                        )
                    }

                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        trackColor = Colors.blueColor
                    )
                }
            }
        }
    }
}



