package com.example.sakhcasttv.ui.main_screens.home_screen.series

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.SubcomposeAsyncImage
import com.example.sakhcasttv.R
import com.example.sakhcasttv.model.SeriesCard
import java.util.Locale


@Preview(showBackground = true)
@Composable
fun PreviewSeriesItemView() {
    SeriesItemView(
        seriesCard = com.example.sakhcasttv.data.samples.SeriesCardSample.seriesCard,
        navigateToSeriesById = {},
    )
}

@Composable
fun SeriesItemView(
    seriesCard: SeriesCard,
    navigateToSeriesById: (String) -> Unit,
    modifier: Modifier = Modifier,
    isFavoriteScreen: Boolean = false,
) {
    val imageUrl = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) seriesCard.coverAlt + ".avif"
    else seriesCard.coverAlt + ".webp"

    val backdropColor1 =
        Color(android.graphics.Color.parseColor(seriesCard.coverColors?.background1 ?: "#000000"))
    val backdropColor2 =
        Color(android.graphics.Color.parseColor(seriesCard.coverColors?.background2 ?: "#000000"))
    val brush = Brush.verticalGradient(listOf(backdropColor1, backdropColor2))

    Card(
        modifier = modifier
            .aspectRatio(250f / 366f),
        onClick = { navigateToSeriesById(seriesCard.id.toString()) },
        shape = CardDefaults.shape(RectangleShape),
        border = CardDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(
                    width = 0.dp,
                    color = MaterialTheme.colorScheme.background
                )
            ),
        )
    ) {
        Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.FillBounds,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(brush = brush)
                        )
                    }
                )
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,

                    ) {
                    if (seriesCard.imdb) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color.Gray.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.padding(5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_imdb),
                                    contentDescription = null
                                )
                                val formattedRatingImdb =
                                    String.format(Locale.US, "%.1f", seriesCard.imdbRating)
                                Text(
                                    modifier = Modifier.padding(start = 3.dp),
                                    text = formattedRatingImdb,
                                    color = Color.White,
                                    fontSize = 8.sp
                                )
                            }
                        }
                    }
                    if (seriesCard.kp) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color.Gray.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.padding(5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_kinopoisk),
                                    contentDescription = null,
                                )
                                val formattedRatingKp =
                                    String.format(Locale.US, "%.1f", seriesCard.kpRating)
                                Text(
                                    modifier = Modifier.padding(start = 3.dp),
                                    text = formattedRatingKp,
                                    color = Color.White,
                                    fontSize = 8.sp
                                )
                            }
                        }
                    }
                }
            }
            Text(
                text = seriesCard.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = seriesCard.year.toString(), fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = seriesCard.seasons, fontSize = 8.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }
}


