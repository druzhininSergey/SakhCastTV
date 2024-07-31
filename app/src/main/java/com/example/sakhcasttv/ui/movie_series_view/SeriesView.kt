package com.example.sakhcasttv.ui.movie_series_view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.sakhcasttv.R
import com.example.sakhcasttv.model.UserContinueWatchSeries
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SeriesView(
    seriesId: Int?,
    navigateToSeriesCategoryScreen: (String) -> Unit,
    navigateToSeriesCategoryByCompany: (String, String) -> Unit,
    navigateToSeriesPlayer: (String, String, String, String) -> Unit,
    seriesViewModel: SeriesViewModel = hiltViewModel(),
) {
    val seriesState =
        seriesViewModel.seriesState.observeAsState(SeriesViewModel.SeriesState())

    LaunchedEffect(seriesId) {
        if (seriesId != null && seriesState.value.series == null) {
            seriesViewModel.getFullSeries(seriesId)
        }
    }
    val series = seriesState.value.series
    val isFavorite = remember { mutableStateOf(seriesState.value.isFavorite ?: false) }
    LaunchedEffect(seriesState.value.isFavorite) {
        isFavorite.value = seriesState.value.isFavorite ?: false
    }
    var seasonId by remember { mutableIntStateOf(series?.userLastSeasonId ?: 0) }
    LaunchedEffect(series) {
        seasonId = series?.userLastSeasonId ?: 0
    }

    val seriesEpisodes = seriesState.value.series?.seasons

    val imageUrl = series?.backdropAlt + ".webp"
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(432.dp)
                    .bringIntoViewRequester(bringIntoViewRequester)
            ) {
                SeriesImageWithGradients(
                    imageUrl = imageUrl,
                    modifier = Modifier.fillMaxSize()
                )
                if (series != null) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(0.55f)
                                .padding(start = 60.dp, top = 60.dp)
                        ) {
                            SeriesLargeTitle(name = series.name, eName = series.eName)

                            Column(
                                modifier = Modifier.alpha(0.75f)
                            ) {
                                val genresString = series.genres.joinToString(", ") { it.name }
                                val countriesString = series.network
                                val year = if (series.yearEnd == 0) "${series.year} - ..."
                                else "${series.year} - ${series.yearEnd}"

                                SeriesDescription(description = series.about)
                                Spacer(modifier = Modifier.height(10.dp))
                                FullInfoSeries(
                                    genresString = genresString,
                                    countriesString = countriesString,
                                    year = year,
                                    ageLimits = "${series.ageLimits}+"
                                )
                            }
                        }
                        SeriesViewMainButtons(
                            getLastMediaData = seriesViewModel::getLastMediaData,
                            navigateToSeriesPlayer = navigateToSeriesPlayer,
                            seriesName = series.name,
                            isFavorite = isFavorite,
                            onFavoriteButtonPushed = seriesViewModel::onFavoriteButtonPushed,
                            modifier = Modifier
                                .onFocusChanged {
                                    if (it.isFocused) {
                                        coroutineScope.launch { bringIntoViewRequester.bringIntoView() }
                                    }
                                },
                            userFavType = series.userFavoriteInSeries?.kind ?: ""
                        )
                    }
                }
            }
        }

        item {
            SeriesEpisodeView(
                episodes = seriesEpisodes,
                navigateToSeriesPlayer = navigateToSeriesPlayer,
                seasonId = series?.userLastSeasonId ?: 0,
                seriesName = series?.name ?: ""
            )
        }
    }
}

@Composable
fun SeriesViewMainButtons(
    getLastMediaData: () -> UserContinueWatchSeries?,
    navigateToSeriesPlayer: (String, String, String, String) -> Unit,
    seriesName: String,
    isFavorite: MutableState<Boolean>,
    onFavoriteButtonPushed: (String) -> Unit,
    modifier: Modifier,
    userFavType: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 60.dp, end = 60.dp, top = 60.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = modifier,
                shape = ButtonDefaults.shape(RoundedCornerShape(4.dp)),
                colors = ButtonDefaults.colors(
                    containerColor = Color.Gray.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.onSecondary,
                    focusedContentColor = Color.Black,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ),
                onClick = {
                    val lastMediaData = getLastMediaData()
                    if (lastMediaData != null) {
                        val lastIndex = lastMediaData.lastMediaIndex.toString()
                        val lastRg = lastMediaData.lastRgWatched
                        val lastSeasonId = lastMediaData.lastSeasonId
                        navigateToSeriesPlayer(
                            lastSeasonId.toString(),
                            seriesName,
                            lastIndex,
                            lastRg
                        )
                    }
                }) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Продолжить просмотр",
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(R.font.jura_bold))
                    )
                }
            }
            AddInFavoritesButtonSeries(
                isFavorite = isFavorite.value,
                onFavoriteButtonPushed = onFavoriteButtonPushed,
                modifier = modifier,
                userFavType = userFavType
            )
        }
    }
}

@Composable
fun SeriesDescription(description: String) {
    Text(
        text = description,
        style = MaterialTheme.typography.titleSmall.copy(
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily(Font(R.font.jura_bold))
        ),
        modifier = Modifier.padding(top = 8.dp),
        maxLines = 6,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun SeriesLargeTitle(name: String, eName: String) {
    Column {
        Text(
            text = name,
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.jura_bold))
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = eName,
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.jura_bold)),
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = 20.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun AddInFavoritesButtonSeries(
    isFavorite: Boolean,
    onFavoriteButtonPushed: (String) -> Unit,
    modifier: Modifier,
    userFavType: String,
) {
    var isExpandedFavorite by remember { mutableStateOf(false) }
    var selectedFavoriteType by remember { mutableStateOf<String?>(null) }
    val listFavoriteType = if (!isFavorite) {
        mapOf(
            "Смотрю" to "watching",
            "Буду смотреть" to "will",
            "Перестал" to "stopped",
            "Досмотрел" to "watched"
        )
    } else {
        mapOf(
            "Убрать из избранного" to "delete",
            "Смотрю" to "watching",
            "Буду смотреть" to "will",
            "Перестал" to "stopped",
            "Досмотрел" to "watched"
        )
    }
    val currentFavoriteTypeKey =
        listFavoriteType.entries.find { it.value == userFavType }?.key ?: "Неизвестно"

    Box {
        Button(
            modifier = modifier,
            shape = ButtonDefaults.shape(RoundedCornerShape(4.dp)),
            colors = ButtonDefaults.colors(
                containerColor = Color.Gray.copy(alpha = 0.5f),
                focusedContainerColor = MaterialTheme.colorScheme.onSecondary,
                focusedContentColor = Color.Black,
                contentColor = MaterialTheme.colorScheme.onBackground
            ),
            onClick = { isExpandedFavorite = true }
        ) {
            if (!isFavorite && selectedFavoriteType == null) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Добавить в избранное",
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(R.font.jura_bold))
                    )
                }
            } else {
                Text(text = "Раздел: ${selectedFavoriteType ?: currentFavoriteTypeKey}")
            }

        }
        DropdownMenu(
            modifier = Modifier.background(Color.Gray.copy(alpha = 0.5f)),
            expanded = isExpandedFavorite,
            onDismissRequest = { isExpandedFavorite = false }
        ) {
            listFavoriteType.forEach { (favType, value) ->
                ListItem(
                    shape = ListItemDefaults.shape(RoundedCornerShape(10.dp)),
                    selected = false,
                    onClick = {
                        onFavoriteButtonPushed(value)
                        selectedFavoriteType = if (value != "delete") favType else null
                        isExpandedFavorite = false
                    },
                    headlineContent = {
                        Text(
                            text = favType,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 20.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color.Gray.copy(alpha = 0.5f))
                        .clip(RoundedCornerShape(10.dp))
                )
            }
        }
    }
}

@Composable
fun FullInfoSeries(
    genresString: String,
    countriesString: String,
    year: String,
    ageLimits: String,
) {

    Column {
        Text(
            text = "${year}г. • $genresString • $ageLimits",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSecondary,
            fontFamily = FontFamily(Font(R.font.jura_bold))
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = countriesString,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSecondary,
            fontFamily = FontFamily(Font(R.font.jura_bold))
        )
    }
}

@Composable
private fun SeriesImageWithGradients(
    imageUrl: String,
    modifier: Modifier = Modifier,
    gradientColor: Color = MaterialTheme.colorScheme.surface,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(imageUrl)
            .crossfade(true).build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier.drawWithContent {
            drawContent()
            drawRect(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, gradientColor),
                    startY = 600f
                )
            )
            drawRect(
                Brush.horizontalGradient(
                    colors = listOf(gradientColor, Color.Transparent),
                    endX = 1000f,
                    startX = 300f
                )
            )
            drawRect(
                Brush.linearGradient(
                    colors = listOf(gradientColor, Color.Transparent),
                    start = Offset(x = 500f, y = 500f),
                    end = Offset(x = 1000f, y = 0f)
                )
            )
        }
    )
}