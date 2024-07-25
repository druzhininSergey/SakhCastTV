package com.example.sakhcasttv.ui.main_screens.top_app_bar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.sakhcasttv.PROFILE_SCREEN
import com.example.sakhcasttv.model.CurrentUser

@Composable
fun TvTopBar(navController: NavHostController, user: CurrentUser?) {
    val listItems = listOf(
        TopBarItem.HomeScreenData,
        TopBarItem.CatalogScreenData,
        TopBarItem.FavoritesScreenData,
        TopBarItem.NotificationsScreenData,
        TopBarItem.SearchScreenData
    )
    val avatarPainter: Painter =
        rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current).data(data = user?.avatar)
                .apply(block = fun ImageRequest.Builder.() {
                    crossfade(true)
                }).build()
        )

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 20.dp, top = 20.dp, end = 20.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            listItems.forEach { item ->
                TopAppTab(
                    navItem = item,
                    isSelected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
        Card(
            onClick = { navController.navigate(PROFILE_SCREEN) },
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            border = CardDefaults.border(
                focusedBorder = Border(
                    border = BorderStroke(
                        width = 0.dp,
                        color = MaterialTheme.colorScheme.background
                    )
                ),
            ),
            scale = CardDefaults.scale(focusedScale = 1.3f)
        ) {
            Image(
                painter = avatarPainter,
                contentDescription = null,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .size(40.dp)
                    .clip(CircleShape)
            )
        }
    }
}

@Composable
fun TopAppTab(
    navItem: TopBarItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Card(
        onClick = onClick,
        interactionSource = interactionSource,
        border = CardDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(
                    width = 0.dp,
                    color = MaterialTheme.colorScheme.background
                )
            ),
        ),
        colors = CardDefaults.colors(
            containerColor = when {
                isSelected -> Color.DarkGray
                isFocused -> Color.Gray
                else -> MaterialTheme.colorScheme.background
            },
            focusedContainerColor = Color.Gray,
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = navItem.title,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}