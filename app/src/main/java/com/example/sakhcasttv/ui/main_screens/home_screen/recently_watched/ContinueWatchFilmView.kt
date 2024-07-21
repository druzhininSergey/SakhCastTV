package com.example.sakhcasttv.ui.main_screens.home_screen.recently_watched

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.SubcomposeAsyncImage
import com.example.sakhcasttv.model.MovieRecent

@Preview(showBackground = true)
@Composable
fun Preview2() {
//    ContinueWatchMovieView(movieCard = Samples.getOneMovie())
}

@Composable
fun ContinueWatchMovieView(
    movieCard: MovieRecent,
    lastWatchedMovieTime: String,
    navigateToMovieByAlphaId: (String) -> Unit
) {
    val imageUrl = movieCard.data.backdropAlt + ".webp"

    val backdropColor1 =
        Color(
            android.graphics.Color.parseColor(
                movieCard.data.coverColors?.background1 ?: "#000000"
            )
        )
    val backdropColor2 =
        Color(
            android.graphics.Color.parseColor(
                movieCard.data.coverColors?.background2 ?: "#000000"
            )
        )
    val brush = Brush.verticalGradient(listOf(backdropColor1, backdropColor2))

    Card(
        modifier = Modifier
            .height(234.dp)
            .aspectRatio(16f / 9f),
        onClick = { navigateToMovieByAlphaId(movieCard.data.idAlpha) }
    ) {
        Box {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds,
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(brush = brush)
                    )
                }
            )
            Column(
                Modifier
                    .padding(start = 6.dp, bottom = 6.dp)
                    .background(
                        color = Color.Gray.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
                    .align(Alignment.BottomStart)
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.Start),
                    text = movieCard.data.ruTitle,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.Start),
                    text = lastWatchedMovieTime,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 12.sp,
                )
            }
        }
    }
}