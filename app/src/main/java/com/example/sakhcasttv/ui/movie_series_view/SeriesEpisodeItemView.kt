package com.example.sakhcasttv.ui.movie_series_view

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.TabRow
import androidx.tv.material3.TabRowDefaults
import androidx.tv.material3.Text
import coil.compose.SubcomposeAsyncImage
import com.example.sakhcasttv.Colors
import com.example.sakhcasttv.Dimens
import com.example.sakhcasttv.R
import com.example.sakhcasttv.model.Season
import com.example.sakhcasttv.model.SeriesEpisode

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SeriesEpisodeView(
    episodes: List<Season>?,
    navigateToSeriesPlayer: (String, String, String, String) -> Unit,
    seriesId: Int
) {
    var focusedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    var activeTabIndex by rememberSaveable { mutableIntStateOf(focusedTabIndex) }

    val firstSeasonId = episodes?.firstOrNull()?.id ?: 0
    var currentSeasonIdChosen by rememberSaveable(firstSeasonId) { mutableIntStateOf(firstSeasonId) }

    Column {
        if (!episodes.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Сезоны:")
                    TabRow(
                        selectedTabIndex = focusedTabIndex,
                        indicator = { tabPositions, doesTabRowHaveFocus ->
                            //Focused
                            TabRowDefaults.PillIndicator(
                                currentTabPosition = tabPositions[focusedTabIndex],
                                activeColor = Color.White,
                                inactiveColor = Color.Transparent,
                                doesTabRowHaveFocus = doesTabRowHaveFocus,
                            )
                            //Selected
                            TabRowDefaults.PillIndicator(
                                currentTabPosition = tabPositions[activeTabIndex],
                                doesTabRowHaveFocus = doesTabRowHaveFocus,
                                activeColor = Color.Gray,
                                inactiveColor = Color.Gray,
                            )
                        },
                        modifier = Modifier
                            .focusRestorer()
                    ) {
                        repeat(episodes.size) { index ->
                            key(index) {
                                val mutableInteractionSource =
                                    remember { MutableInteractionSource() }
                                val isFocused by mutableInteractionSource.collectIsFocusedAsState()
                                val isSelected = activeTabIndex == index

                                Card(
                                    scale = CardDefaults.scale(),
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.surface)
                                        .padding(8.dp),
                                    onClick = {
                                        focusedTabIndex = index
                                        activeTabIndex = index
                                        currentSeasonIdChosen = episodes[index].id
                                    },
                                    interactionSource = mutableInteractionSource,
                                    colors = CardDefaults.colors(
                                        containerColor = when {
                                            isSelected -> Color.DarkGray
                                            isFocused -> Color.Gray
                                            else -> MaterialTheme.colorScheme.surface
                                        },
                                        focusedContainerColor = Color.Gray,
                                    )
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 6.dp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(Dimens.mainPadding),
            modifier = Modifier.height(500.dp)
        ) {
            if (episodes != null) {
                itemsIndexed(episodes[activeTabIndex].episodes) { _, episode ->
                    SeriesEpisodeItemView(
                        seriesEpisode = episode,
                        navigateToSeriesPlayer = navigateToSeriesPlayer,
                        seasonId = currentSeasonIdChosen,
                        episodeChosenIndex = episode.index,
                        seriesId = seriesId
                    )
                }
            }
        }
    }
}

@Composable
fun SeriesEpisodeItemView(
    seriesEpisode: SeriesEpisode,
    navigateToSeriesPlayer: (String, String, String, String) -> Unit,
    seasonId: Int,
    episodeChosenIndex: String,
    seriesId: Int,
) {

    val imageUrl = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        seriesEpisode.previewAlt + ".avif"
    } else {
        seriesEpisode.previewAlt + ".webp"
    }
    var isExpanded by remember {
        mutableStateOf(false)
    }

    val backdropColor1 = listOf(
        Color(0xFF616161),
        Color(0xFF78909C),
        Color(0xFF5D4037),
        Color(0xFF546E7A),
        Color(0xFF0D47A1),
        Color(0xFF1B5E20),
        Color(0xFF212121)
    ).random()
    val backdropColor2 = listOf(
        Color(0xFF757575),
        Color(0xFF90A4AE),
        Color(0xFF4E342E),
        Color(0xFF455A64),
        Color(0xFF1565C0),
        Color(0xFF2E7D32),
        Color(0xFF424242)
    ).random()
    val brush = Brush.verticalGradient(listOf(backdropColor1, backdropColor2))

    Card(
        colors = CardDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .width(190.dp)
            .background(MaterialTheme.colorScheme.surface),
        onClick = {
            if (seriesEpisode.medias.size == 1) {
                navigateToSeriesPlayer(
                    seasonId.toString(),
                    seriesId.toString(),
                    episodeChosenIndex,
                    seriesEpisode.medias[0].name
                )
            } else
                isExpanded = true
        },
    ) {
        Box {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .height(123.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(brush = brush)
                    )
                }
            )
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(30.dp)
                    .background(
                        color = Color.Gray.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    fontSize = 12.sp,
                    text = seriesEpisode.index,
                    color = Color.White,
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = seriesEpisode.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 12.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = seriesEpisode.date, fontSize = 10.sp)
                if (seriesEpisode.isViewed == 0)
                    Icon(
                        painterResource(id = R.drawable.ic_circle),
                        contentDescription = null,
                        tint = Colors.blueColor
                    )
            }
        }
        DropdownMenu(
            modifier = Modifier.background(
                color = Color.Gray.copy(alpha = 0.5f)
            ),
            offset = DpOffset(0.dp, 8.dp),
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
        ) {
            seriesEpisode.medias.forEach { rgs ->
                DropdownMenuItem(
                    text = { Text(text = rgs.ruName) },
                    onClick = {
                        navigateToSeriesPlayer(
                            seasonId.toString(),
                            seriesId.toString(),
                            episodeChosenIndex,
                            rgs.name
                        )
                        isExpanded = false
                    },
                )
            }
        }
    }
}