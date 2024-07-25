package com.example.sakhcasttv.ui.main_screens.catalog_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.sakhcasttv.CustomListItemColors
import com.example.sakhcasttv.Dimens
import com.example.sakhcasttv.Genres

@Composable
fun CatalogList(
    categories: List<String>,
    tabIndex: Int,
    navigateToMoviesCategoryScreen: (String) -> Unit,
    navigateToSeriesCategoryScreen: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    LazyColumn {
        itemsIndexed(categories) { _, item ->
            ListItem(
                colors = CustomListItemColors.listItemColors(),
                shape = ListItemDefaults.shape(RoundedCornerShape(10.dp)),
                selected = false,
                onClick = {
                    if (item == categories.first()) {
                        expanded = true
                    } else {
                        val navigateFunction =
                            if (tabIndex == 0) navigateToSeriesCategoryScreen else navigateToMoviesCategoryScreen
                        navigateFunction(item)
                    }
                },
                headlineContent = {
                    Text(
                        text = item,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(start = 20.dp)
                    )
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(end = 20.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .clip(RoundedCornerShape(10.dp))
            )
            if (item != categories.last()) {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(start = Dimens.mainPaddingHalf, end = Dimens.mainPaddingHalf),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }

    DropdownMenu(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        val genresList = if (tabIndex == 0) Genres.seriesGenres else Genres.movieGenresMap.keys
        genresList.forEach { category ->
            ListItem(
                colors = CustomListItemColors.listItemColors(),
                shape = ListItemDefaults.shape(RoundedCornerShape(10.dp)),
                selected = false,
                onClick = {
                    expanded = false
                    selectedCategory = category
                    val navigateFunction =
                        if (tabIndex == 0) navigateToSeriesCategoryScreen else navigateToMoviesCategoryScreen
                    navigateFunction(category)
                },
                headlineContent = {
                    Text(
                        text = category,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(start = 20.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .clip(RoundedCornerShape(10.dp))
            )
        }
    }
}