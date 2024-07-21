package com.example.sakhcasttv.ui.main_screens.home_screen.series

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sakhcasttv.model.SeriesList
import com.example.sakhcasttv.ui.general.CustomListItem

@Composable
fun SeriesCategoryView(
    seriesList: SeriesList,
    navigateToSeriesById: (String) -> Unit,
    navigateToCatalogAllSeries: () -> Unit
) {
    CustomListItem(text = "Все сериалы", onClick = navigateToCatalogAllSeries)
    LazyRow(
        modifier = Modifier.padding(vertical = 16.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(items = seriesList.items, key = { _, series -> series.id }) { _, item ->
            SeriesItemView(seriesCard = item, navigateToSeriesById)
        }
    }
}
