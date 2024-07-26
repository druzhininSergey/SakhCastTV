package com.example.sakhcasttv.ui.movie_series_view
//
//import android.os.Build
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.core.animateFloatAsState
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.ScrollState
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.ExperimentalMaterialApi
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
//import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
//import androidx.compose.material.icons.twotone.PlayArrow
//import androidx.compose.material.pullrefresh.PullRefreshIndicator
//import androidx.compose.material.pullrefresh.pullRefresh
//import androidx.compose.material.pullrefresh.rememberPullRefreshState
//import androidx.compose.material3.DropdownMenu
//import androidx.compose.material3.DropdownMenuItem
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.runtime.mutableIntStateOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.rotate
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.layout.onGloballyPositioned
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.DpOffset
//import androidx.compose.ui.unit.IntSize
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.zIndex
//import androidx.hilt.navigation.compose.hiltViewModel
//import coil.compose.SubcomposeAsyncImage
//import com.example.sakhcasttv.R
//import com.example.sakhcasttv.data.browserIntent
//import com.example.sakhcasttv.model.Cast
//import com.example.sakhcasttv.model.Episode
//import com.example.sakhcasttv.model.Genre
//import com.example.sakhcasttv.model.Network
//import com.example.sakhcasttv.model.Person
//import com.example.sakhcasttv.model.Season
//import com.example.sakhcasttv.model.Series
//import com.example.sakhcasttv.model.UserContinueWatchSeries
//import com.example.sakhcasttv.ui.movie_series_view.SeriesViewModel
//import kotlinx.coroutines.delay
//import java.util.Locale
//import kotlin.math.min
//
//@Preview
//@Composable
//fun PreviewSeriesView() {
////    SeriesView(PaddingValues(top = 40.dp, bottom = 40.dp), navHostController)
//}
//
//@Preview
//@Composable
//fun PreviewSeriesInfo() {
////    SeriesInfo(SeriesSample.getFullSeries(), SeriesEpisodesSample.getSeriesEpisodesList())
//}
//
//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//fun SeriesView(
//    paddingValues: PaddingValues,
//    seriesId: Int?,
//    navigateUp: () -> Boolean,
//    navigateToSeriesCategoryScreen: (String) -> Unit,
//    navigateToSeriesCategoryByCompany: (String, String) -> Unit,
//    navigateToSeriesPlayer: (String, String, String, String) -> Unit,
//    seriesViewModel: SeriesViewModel = hiltViewModel(),
//) {
//    val seriesState =
//        seriesViewModel.seriesState.observeAsState(SeriesViewModel.SeriesState())
//
//    LaunchedEffect(seriesId) {
//        if (seriesId != null && seriesState.value.series == null) {
//            seriesViewModel.getFullSeries(seriesId)
//        }
//    }
//    val series = seriesState.value.series
//    val isFavorite = remember { mutableStateOf(seriesState.value.isFavorite ?: false) }
//    LaunchedEffect(seriesState.value.isFavorite) {
//        isFavorite.value = seriesState.value.isFavorite ?: false
//    }
//    var seasonId by remember { mutableIntStateOf(series?.userLastSeasonId ?: 0) }
//    LaunchedEffect(series) {
//        seasonId = series?.userLastSeasonId ?: 0
//    }
//
//    LaunchedEffect(seasonId) {
//        if (seasonId != 0) {
//            seriesViewModel.getSeriesEpisodesBySeasonId(seasonId)
//        }
//    }
//    val seriesEpisodes = seriesState.value.episodeList
//    val scrollState = rememberScrollState()
//    var sizeImage by remember { mutableStateOf(IntSize.Zero) }
//
//    val imageUrl = series?.posterAlt + ".webp"
//
//    val backdropColor1 =
//        if (series != null) Color(android.graphics.Color.parseColor(series.posterColors.background1))
//        else Color.Gray
//    val backdropColor2 =
//        if (series != null) Color(android.graphics.Color.parseColor(series.posterColors.background2))
//        else Color.Blue
//    val brush = Brush.verticalGradient(listOf(backdropColor1, backdropColor2))
//
//    var refreshing by remember { mutableStateOf(false) }
//    val pullRefreshState = rememberPullRefreshState(refreshing, { refreshing = true })
//    LaunchedEffect(refreshing) {
//        if (refreshing) {
//            seriesId?.let { seriesViewModel.getFullSeries(it) }
//            delay(2000)
//            refreshing = false
//        }
//    }
//
//    Box {
//        Column(
//            modifier = Modifier
//                .padding(bottom = paddingValues.calculateBottomPadding())
//                .fillMaxSize()
//                .pullRefresh(pullRefreshState)
//                .verticalScroll(scrollState)
//        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(500.dp)
//                    .background(MaterialTheme.colorScheme.primary),
//                contentAlignment = Alignment.Center
//            ) {
//                SubcomposeAsyncImage(
//                    model = imageUrl,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .graphicsLayer {
//                            alpha =
//                                1f - ((scrollState.value.toFloat() / scrollState.maxValue) * 1.5f)
//                            translationY = 0.5f * scrollState.value
//                        },
//                    contentScale = ContentScale.Crop,
//                    loading = {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .background(brush = brush)
//                        )
//                    }
//                )
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(500.dp)
//                        .onGloballyPositioned {
//                            sizeImage = it.size
//                        }
//                        .background(
//                            Brush.verticalGradient(
//                                colors = listOf(
//                                    Color.Transparent,
//                                    MaterialTheme.colorScheme.primary
//                                ),
//                                startY = sizeImage.height.toFloat() / 2,
//                                endY = sizeImage.height.toFloat()
//                            )
//                        ),
//                    contentAlignment = Alignment.BottomStart
//                ) {
//                    Box(
//                        modifier = Modifier
//                            .background(Color.Transparent)
//                            .padding(16.dp)
//                    ) {
//                        Column {
//                            series?.name?.let {
//                                Text(
//                                    text = it,
//                                    fontSize = 25.sp,
//                                    color = MaterialTheme.colorScheme.onPrimary
//                                )
//                            }
//                            series?.eName?.let {
//                                Text(
//                                    text = it,
//                                    fontSize = 18.sp,
//                                    color = MaterialTheme.colorScheme.onPrimary
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//            Box(modifier = Modifier.background(MaterialTheme.colorScheme.primary)) {
//                series?.let {
//                    SeriesInfo(
//                        it,
//                        seriesEpisodes,
//                        navigateToSeriesCategoryScreen,
//                        navigateToSeriesCategoryByCompany,
//                        navigateToSeriesPlayer,
//                        seasonId,
//                        series.name,
//                        seriesViewModel::getLastMediaData
//                    ) { newSeasonId ->
//                        seasonId = newSeasonId
//                    }
//                }
//            }
//        }
//        series?.name?.let {
//            TopSeriesBar(
//                scrollState = scrollState,
//                ruTitle = it,
//                paddingValues = paddingValues,
//                isFavorite = isFavorite.value,
//                navigateUp = navigateUp,
//                onFavoriteButtonPushed = { kind ->
//                    seriesViewModel.onFavoriteButtonPushed(kind)
//                },
//            )
//        }
//        PullRefreshIndicator(
//            refreshing = refreshing,
//            state = pullRefreshState,
//            modifier = Modifier
//                .padding(paddingValues)
//                .align(Alignment.TopCenter)
//                .zIndex(1f)
//        )
//    }
//}
//
//
//@Composable
//fun SeriesInfo(
//    series: Series,
//    seriesEpisodes: List<Episode>,
//    navigateToSeriesCategoryScreen: (String) -> Unit,
//    navigateToSeriesCategoryByCompany: (String, String) -> Unit,
//    navigateToSeriesPlayer: (String, String, String, String) -> Unit,
//    seasonId: Int,
//    seriesName: String,
//    getLastMediaData: () -> UserContinueWatchSeries?,
//    onSeasonChanged: (Int) -> Unit
//) {
//    val isRatingExists = series.imdbRating != null || series.kpRating != null
//    val year = if (series.yearEnd == 0) "${series.year} - ..."
//    else "${series.year} - ${series.yearEnd}"
//
//    Column(
//        modifier = Modifier.background(MaterialTheme.colorScheme.primary)
//    ) {
//
//        SeriesGenres(series.genres, navigateToSeriesCategoryScreen)
//        if (isRatingExists) SeriesRating(
//            series.imdbRating,
//            series.kpRating,
//            series.imdbUrl,
//            series.kpId
//        )
//        SeriesCountryYearStatus(series.country, year, series.status)
//        SeriesDownloads(
//            series.seasons,
//            seriesEpisodes,
//            onSeasonChanged,
//            series.userLastSeason,
//            navigateToSeriesPlayer,
//            seasonId,
//            seriesName,
//            getLastMediaData
//        )
//        SeriesOverview(series.about)
//        series.cast?.let { SeriesExpandableCastTab(cast = it, navigateToSeriesCategoryByCompany) }
//        SeriesProductionCompanies(series.networks, navigateToSeriesCategoryByCompany)
//        SeriesViewsCountInfo(series.views, series.favAmount)
//    }
//}
//
//@Composable
//fun SeriesExpandableCastTab(
//    cast: Cast,
//    navigateToSeriesCategoriesByGenresId: (String, String) -> Unit
//) {
//    var isExpanded by remember { mutableStateOf(false) }
//    val rotationAngle by animateFloatAsState(
//        targetValue = if (isExpanded) 90f else 0f,
//        animationSpec = tween(durationMillis = 300), label = ""
//    )
//
//    Row(
//        modifier = Modifier
//            .padding(bottom = 40.dp)
//            .fillMaxWidth()
//            .clickable { isExpanded = !isExpanded },
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Text(
//            modifier = Modifier.padding(start = 16.dp),
//            text = "Состав",
//            fontSize = 25.sp,
//            color = MaterialTheme.colorScheme.onPrimary
//        )
//        Icon(
//            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
//            contentDescription = null,
//            tint = MaterialTheme.colorScheme.onPrimary,
//            modifier = Modifier
//                .padding(top = 4.dp)
//                .rotate(rotationAngle)
//        )
//    }
//
//    AnimatedVisibility(visible = isExpanded) {
//        Column {
//
//            val castMembers = listOfNotNull(
//                cast.voiceActor?.let { "Актеры озвучки" to it },
//                cast.designer?.let { "Художники" to it },
//                cast.actor?.let { "Актеры" to it },
//                cast.composer?.let { "Композиторы" to it },
//                cast.director?.let { "Режиссеры" to it },
//                cast.producer?.let { "Продюсеры" to it },
//                cast.writer?.let { "Сценаристы" to it },
//                cast.editor?.let { "Монтажеры" to it },
//                cast.operator?.let { "Операторы" to it }
//            )
//
//            castMembers.forEach { (title, members) ->
//                Text(
//                    modifier = Modifier.padding(start = 16.dp),
//                    text = title,
//                    fontSize = 20.sp,
//                    color = MaterialTheme.colorScheme.onPrimary
//                )
//                LazyRow(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp),
//                    contentPadding = PaddingValues(16.dp)
//                ) {
//                    items(items = members, key = { it.id }) { person ->
//                        SeriesPersonItem(person, navigateToSeriesCategoriesByGenresId)
//                    }
//                }
//                DividerBase()
//            }
//        }
//    }
//}
//
//@Composable
//fun SeriesPersonItem(
//    person: Person,
//    navigateToSeriesCategoriesByGenresId: (String, String) -> Unit
//) {
//    val imageUrl = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//        person.photoAlt + ".avif"
//    } else {
//        person.photoAlt + ".webp"
//    }
//    val backdropColor1 = Color.Gray
//    val backdropColor2 = Color.Black
//    val brush = Brush.verticalGradient(listOf(backdropColor1, backdropColor2))
//    Column(
//        modifier = Modifier
//            .width(70.dp)
//            .padding(vertical = 4.dp)
//            .clickable {
////              TODO  navigateToSeriesCategoriesByGenresId(
////                    person.ruName,
////                    "${person.id}.person"
////                )
//            },
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        SubcomposeAsyncImage(
//            model = imageUrl,
//            contentDescription = null,
//            modifier = Modifier
//                .clip(CircleShape)
//                .size(60.dp),
//            contentScale = ContentScale.FillBounds,
//            loading = {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(brush = brush)
//                )
//            }
//        )
//        Text(
//            text = person.ruName,
//            fontSize = 12.sp,
//            color = MaterialTheme.colorScheme.onPrimary,
//            textAlign = TextAlign.Center,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 8.dp)
//        )
//    }
//}
//
//@Composable
//fun SeriesViewsCountInfo(views: Int, favorites: Int) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(bottom = 16.dp),
//        horizontalArrangement = Arrangement.SpaceEvenly,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text(
//                text = "Просмотры",
//                color = Color.Gray,
//                fontSize = 14.sp,
//                modifier = Modifier,
//            )
//            Spacer(modifier = Modifier.width(16.dp))
//            Text(
//                text = views.toString(),
//                fontSize = 25.sp,
//                color = MaterialTheme.colorScheme.onPrimary
//            )
//        }
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text(
//                text = "Подписки",
//                color = Color.Gray,
//                fontSize = 14.sp,
//            )
//            Spacer(modifier = Modifier.width(16.dp))
//            Text(
//                text = favorites.toString(),
//                fontSize = 25.sp,
//                color = MaterialTheme.colorScheme.onPrimary
//            )
//        }
//    }
//}
//
//@Composable
//fun SeriesProductionCompanies(
//    productionCompanies: List<Network>,
//    navigateToSeriesCategoryByCompany: (String, String) -> Unit,
//) {
//    Text(
//        modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
//        text = "Кинокомпании",
//        fontSize = 25.sp,
//        color = MaterialTheme.colorScheme.onPrimary
//    )
//    LazyRow(
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.spacedBy(16.dp),
//        contentPadding = PaddingValues(start = 16.dp, end = 16.dp)
//    ) {
//        items(items = productionCompanies, key = { it.id }) { company ->
//            Text(
//                text = company.name,
//                color = Color.Gray,
//                fontSize = 14.sp,
//                modifier = Modifier
//                    .border(1.dp, Color.Gray, MaterialTheme.shapes.small)
//                    .clickable {
//                        navigateToSeriesCategoryByCompany(
//                            "${company.id}.company",
//                            company.name
//                        )
//                    }
//                    .padding(horizontal = 8.dp, vertical = 4.dp)
//            )
//        }
//    }
//    DividerBase()
//}
//
//@Composable
//fun TopSeriesBar(
//    scrollState: ScrollState,
//    ruTitle: String,
//    paddingValues: PaddingValues,
//    isFavorite: Boolean,
//    navigateUp: () -> Boolean,
//    onFavoriteButtonPushed: (String) -> Unit,
//) {
//    var isExpandedFavorite by remember { mutableStateOf(false) }
//    val alpha = if (scrollState.maxValue > 0) {
//        min(1f, (scrollState.value.toFloat() / scrollState.maxValue) * 1.5f)
//    } else 0f
//
//    val primaryColor = MaterialTheme.colorScheme.primary
//    val favIcon = if (isFavorite) painterResource(R.drawable.ic_star_full2)
//    else painterResource(R.drawable.ic_star_empty2)
//    val listFavoriteType = if (!isFavorite) {
//        mapOf(
//            "Смотрю" to "watching",
//            "Буду смотреть" to "will",
//            "Перестал" to "stopped",
//            "Досмотрел" to "watched"
//        )
//    } else {
//        mapOf(
//            "Убрать из избранного" to "delete",
//            "Смотрю" to "watching",
//            "Буду смотреть" to "will",
//            "Перестал" to "stopped",
//            "Досмотрел" to "watched"
//        )
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(60.dp + paddingValues.calculateTopPadding())
//            .background(color = primaryColor.copy(alpha = alpha))
//    ) {
//        Spacer(modifier = Modifier.height(paddingValues.calculateTopPadding()))
//        Row(
//            modifier = Modifier
//                .fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            IconButton(onClick = { navigateUp() }) {
//                Icon(
//                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .padding(8.dp)
//                        .size(30.dp),
//                    tint = Color.White,
//                )
//            }
//            Text(
//                text = ruTitle,
//                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = alpha),
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(horizontal = 8.dp),
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis
//            )
//            Box(modifier = Modifier) {
//                Icon(
//                    painter = favIcon,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .padding(8.dp)
//                        .size(40.dp)
//                        .clickable { isExpandedFavorite = true },
//                    tint = if (isFavorite) Color(0xFFFFD700) else Color.White
//                )
//                DropdownMenu(
//                    modifier = Modifier.background(
//                        color = Color.Gray.copy(alpha = 0.5f)
//                    ),
//                    offset = DpOffset(0.dp, 8.dp),
//                    expanded = isExpandedFavorite,
//                    onDismissRequest = { isExpandedFavorite = false },
//                ) {
//                    listFavoriteType.keys.forEach { favType ->
//                        DropdownMenuItem(text = { Text(text = favType) }, onClick = {
//                            listFavoriteType[favType]?.let { onFavoriteButtonPushed(it) }
//                            isExpandedFavorite = false
//                        })
//                    }
//                }
//            }
//        }
//    }
//
//}
//
//@Composable
//fun SeriesDownloads(
//    seasons: List<Season>,
//    seriesEpisodes: List<Episode>,
//    onSeasonChanged: (Int) -> Unit,
//    userLastSeason: String,
//    navigateToSeriesPlayer: (String, String, String, String) -> Unit,
//    seasonId: Int,
//    seriesName: String,
//    getLastMediaData: () -> UserContinueWatchSeries?,
//) {
//    var isExpanded by remember { mutableStateOf(false) }
//    var seasonSelected by remember { mutableStateOf("Сезон $userLastSeason") }
//    val scrollState = rememberScrollState()
//    Row(
//        modifier = Modifier
//            .padding(start = 16.dp, end = 16.dp)
//            .fillMaxWidth(),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        if (seasons.size > 1) {
//            Box(
//                modifier = Modifier
//                    .background(color = Color.Transparent)
//            ) {
//                Text(
//                    text = seasonSelected,
//                    color = MaterialTheme.colorScheme.onSecondary,
//                    modifier = Modifier
//                        .background(
//                            color = Color.Gray.copy(alpha = 0.5f),
//                            shape = MaterialTheme.shapes.small
//                        )
//                        .padding(6.dp)
//                        .clickable { isExpanded = true }
//                )
//                DropdownMenu(
//                    modifier = Modifier.background(
//                        color = Color.Gray.copy(alpha = 0.5f)
//                    ),
//                    offset = DpOffset(0.dp, 8.dp),
//                    expanded = isExpanded,
//                    onDismissRequest = { isExpanded = false },
//                ) {
//                    seasons.forEach { season ->
//                        DropdownMenuItem(
//                            modifier = Modifier.align(Alignment.CenterHorizontally),
//                            text = { Text("Сезон ${season.index}") },
//                            onClick = {
//                                seasonSelected = "Сезон ${season.index}"
//                                isExpanded = false
//                                onSeasonChanged(season.id)
//                            },
//                        )
//                    }
//                }
//                LaunchedEffect(isExpanded) {
//                    if (isExpanded) {
//                        scrollState.scrollTo(scrollState.maxValue)
//                    }
//                }
//            }
//        }
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier
//                .background(
//                    shape = MaterialTheme.shapes.small,
//                    color = MaterialTheme.colorScheme.primary
//                )
//                .border(width = 1.dp, color = Color.Gray, shape = MaterialTheme.shapes.small)
//                .clickable {
//                    val lastMediaData = getLastMediaData()
//                    if (lastMediaData != null) {
//                        val lastIndex = lastMediaData.lastMediaIndex.toString()
//                        val lastRg = lastMediaData.lastRgWatched
//                        val lastSeasonId = lastMediaData.lastSeasonId
//                        navigateToSeriesPlayer(
//                            lastSeasonId.toString(),
//                            seriesName,
//                            lastIndex,
//                            lastRg
//                        )
//                    }
//                }
//                .padding(5.dp)
//        ) {
//            Icon(imageVector = Icons.TwoTone.PlayArrow, contentDescription = null)
//            Text(text = "Продолжить просмотр", color = MaterialTheme.colorScheme.onPrimary)
//        }
//    }
//    SeriesEpisodeView(seriesEpisodes, navigateToSeriesPlayer, seasonId, seriesName)
//    DividerBase()
//}
//
//@Composable
//fun SeriesCountryYearStatus(
//    country: String,
//    releaseDate: String,
//    movieStatus: String
//) {
//    val infoColumns = listOf("Страна", "Год", "Статус")
//    Column(
//        modifier = Modifier.fillMaxWidth(),
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            infoColumns.forEach { columnName ->
//                Text(
//                    text = columnName,
//                    color = Color.Gray,
//                    fontSize = 14.sp,
//                    modifier = Modifier.weight(1f),
//                    textAlign = TextAlign.Center
//                )
//            }
//        }
//        Spacer(modifier = Modifier.height(8.dp))
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceEvenly,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            listOf(country, releaseDate.take(4), movieStatus).forEach { info ->
//                Text(
//                    text = info,
//                    color = MaterialTheme.colorScheme.onPrimary,
//                    fontSize = 18.sp,
//                    maxLines = 2,
//                    overflow = TextOverflow.Ellipsis,
//                    modifier = Modifier.weight(1f),
//                    textAlign = TextAlign.Center
//                )
//            }
//        }
//    }
//    DividerBase()
//}
//
//@Composable
//fun SeriesRating(_imdbRating: Double?, _kinopoiskRating: Double?, imdbUrl: String?, kpId: Int?) {
//    val imdbRating = String.format(Locale.US, "%.1f", _imdbRating)
//    val kinopoiskRating = String.format(Locale.US, "%.1f", _kinopoiskRating)
//    val context = LocalContext.current
//
//    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
//        if (_imdbRating != null && _imdbRating != 0.0) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier
//                    .border(
//                        1.dp,
//                        Color.Gray,
//                        MaterialTheme.shapes.small
//                    )
//                    .padding(horizontal = 8.dp, vertical = 4.dp)
//                    .clickable { imdbUrl?.let { context.browserIntent(it) } }
//            ) {
//                Text(text = "IMDb:", color = Color.Gray, fontSize = 16.sp)
//                Spacer(modifier = Modifier.width(10.dp))
//                Text(
//                    text = imdbRating,
//                    color = MaterialTheme.colorScheme.scrim,
//                    fontSize = 18.sp
//                )
//            }
//        }
//        if (_kinopoiskRating != null && _kinopoiskRating != 0.0) {
//            Row(verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier
//                    .border(
//                        1.dp,
//                        Color.Gray,
//                        MaterialTheme.shapes.small
//                    )
//                    .padding(horizontal = 8.dp, vertical = 4.dp)
//                    .clickable { context.browserIntent("$KINOPOISK_SERIES_SEARCH_URL$kpId/") }) {
//                Text(text = "Кинопоиск:", color = Color.Gray, fontSize = 16.sp)
//                Spacer(modifier = Modifier.width(10.dp))
//                Text(
//                    text = kinopoiskRating,
//                    color = MaterialTheme.colorScheme.scrim,
//                    fontSize = 18.sp
//                )
//            }
//        }
//    }
//    DividerBase()
//}
//
//@Composable
//fun SeriesGenres(genres: List<Genre>, navigateToSeriesCategoryScreen: (String) -> Unit) {
//    LazyRow(Modifier.fillMaxWidth()) {
//        items(items = genres, key = { it.id }) { genres ->
//            TextButton(onClick = {}) {
//                Text(
//                    text = genres.name.uppercase(),
//                    color = Color.Gray,
//                    fontSize = 10.sp,
//                    modifier = Modifier
//                        .border(
//                            1.dp,
//                            Color.Gray,
//                            MaterialTheme.shapes.small
//                        )
//                        .clickable { navigateToSeriesCategoryScreen(genres.name) }
//                        .padding(horizontal = 8.dp, vertical = 4.dp)
//                )
//            }
//        }
//    }
//    DividerBase()
//}
//
//@Composable
//fun SeriesOverview(overview: String) {
//    Text(
//        modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
//        text = "Описание",
//        fontSize = 25.sp,
//        color = MaterialTheme.colorScheme.onPrimary
//    )
//    Text(
//        modifier = Modifier.padding(horizontal = 16.dp),
//        text = overview,
//        fontSize = 14.sp,
//        color = Color.Gray
//    )
//    DividerBase()
//}