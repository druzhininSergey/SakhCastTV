package com.example.sakhcasttv.ui.main_screens.favorites_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
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
    val categoryNames = mapOf(
        "Cмотрю" to "watching",
        "Буду смотреть" to "will",
        "Перестал" to "stopped",
        "Досмотрел" to "watched"
    )

    LazyColumn {
        categoryNames.entries.forEachIndexed { indexCategories, category ->
            val seriesList = when (indexCategories) {
                0 -> seriesCardWatching
                1 -> seriesCardWillWatch
                2 -> seriesCardFinishedWatching
                3 -> seriesCardWatched
                else -> null
            }

            if (seriesList != null && seriesList.items.isNotEmpty()) {
                item {
                    CustomListItem(
                        category = category,
                        isSeries = true,
                        onClickWithParam = navigateToSeriesCategoryByType
                    )
                }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(Dimens.mainPadding),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.mainPadding),
                    ) {
                        itemsIndexed(
                            items = seriesList.items,
                        ) { _, series ->
                            SeriesItemView(
                                seriesCard = series,
                                navigateToSeriesById = navigateToSeriesById,
                            )
                        }
                    }
                }
            }
        }
    }
}