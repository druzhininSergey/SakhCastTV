package com.example.sakhcasttv.ui.movie_series_view

import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
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
import coil.compose.SubcomposeAsyncImage
import com.example.sakhcasttv.R
import com.example.sakhcasttv.model.Movie

@Composable
fun MovieView(
    alphaId: String?,
    navigateToMoviePlayer: (String, String, Int, String) -> Unit,
    navigateToMovieByAlphaId: (String) -> Unit,
    navigateToMovieCategoriesByGenresId: (String, String) -> Unit,
    movieViewModel: MovieViewModel = hiltViewModel()
) {
    val movieState = movieViewModel.movieState.observeAsState(MovieViewModel.MovieState())
    val movie = movieState.value.movie

    var alphaIdToSend by remember { mutableStateOf("") }
    var hlsToSend by remember { mutableStateOf("") }
    var titleToSend by remember { mutableStateOf("") }
    var positionToSend by remember { mutableIntStateOf(0) }
    val positionUpdated by remember {
        mutableIntStateOf(movieViewModel.position.value)
    }

    LaunchedEffect(alphaId) {
        if (alphaId != null && movie == null) movieViewModel.getFullMovieWithRecommendations(alphaId)
        if (alphaId != null && movie != null) movieViewModel.getMoviePosition(alphaId)
    }
    val isFavorite = remember {
        mutableStateOf(movieState.value.isFavorite ?: false)
    }
    LaunchedEffect(movieState.value.isFavorite) {
        isFavorite.value = movieState.value.isFavorite ?: false
        alphaIdToSend = movie?.idAlpha ?: ""
        hlsToSend = Uri.encode(movie?.sources?.defaultSource ?: "")
        titleToSend = movie?.ruTitle ?: ""
        positionToSend = movie?.userFavourite?.position ?: 0
    }
    val imageUrl = movie?.backdropAlt + ".webp"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SubcomposeAsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop,
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                )
            }
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
    )
    if (movie != null) {
        Box(
            modifier = Modifier
                .padding(top = 60.dp, start = 60.dp)
                .fillMaxWidth(0.5f)
        ) {
            FullInfo(movie)
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 60.dp, end = 60.dp, bottom = 60.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    shape = ButtonDefaults.shape(RoundedCornerShape(4.dp)),
                    colors = ButtonDefaults.colors(
                        containerColor = Color.Gray.copy(alpha = 0.5f),
                        focusedContainerColor = MaterialTheme.colorScheme.onSecondary,
                        focusedContentColor = Color.Black,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    onClick = {
                        if (positionUpdated == 0)
                            navigateToMoviePlayer(
                                hlsToSend,
                                titleToSend,
                                positionToSend,
                                alphaIdToSend
                            )
                        else
                            navigateToMoviePlayer(
                                hlsToSend,
                                titleToSend,
                                positionUpdated,
                                alphaIdToSend
                            )
                    }) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Смотреть",
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.jura_bold))
                        )
                    }
                }
                AddInFavoritesButton(
                    isFavorite = isFavorite.value,
                ) { kind: String ->
                    movieViewModel.onFavoriteButtonPushed(kind)
                }
            }
        }

    }
}

@Composable
fun AddInFavoritesButton(
    isFavorite: Boolean,
    onFavoriteButtonPushed: (String) -> Unit
) {
    var isExpandedFavorite by remember { mutableStateOf(false) }
    var selectedFavoriteType by remember { mutableStateOf<String?>(null) }
    val listFavoriteType = if (isFavorite) {
        mapOf(
            "Убрать из избранного" to "delete",
            "Буду смотреть" to "will",
            "Просмотренно" to "watched"
        )
    } else {
        mapOf(
            "Буду смотреть" to "will",
            "Просмотренно" to "watched"
        )
    }
    Box(Modifier.padding(end = 60.dp)) {
        Button(
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
                    Text(text = "Добавить в избранное")
                }
            } else {
                Text(text = "Раздел: ${selectedFavoriteType ?: ""}")
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
fun FullInfo(movie: Movie) {
    val genresString = movie.genres.joinToString(", ") { it.name }
    val countriesString = movie.productionCountries.joinToString(", ") { it.name }
    Column {
        Text(
            text = movie.ruTitle,
            fontFamily = FontFamily(Font(R.font.jura_bold)),
            fontSize = 30.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = movie.originTitle,
            fontFamily = FontFamily(Font(R.font.jura_bold)),
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSecondary
        )
        Spacer(modifier = Modifier.height(20.dp))
        if (movie.overview != null) Text(
            text = movie.overview,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSecondary,
            fontFamily = FontFamily(Font(R.font.jura_bold))
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "${movie.releaseDate.take(4)}г. • $genresString • ${movie.ageLimits}+",
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