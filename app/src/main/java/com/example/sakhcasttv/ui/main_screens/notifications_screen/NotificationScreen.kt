package com.example.sakhcasttv.ui.main_screens.notifications_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
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

@Composable
fun NotificationScreen(
    notificationScreenState: NotificationScreenViewModel.NotificationScreenState,
    makeAllNotificationsRead: () -> Unit,
    getNotifications: () -> Unit,
    navigateToSeriesById: (String) -> Unit
) {
    val notificationList = notificationScreenState.notificationsList?.items

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 10.dp),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (notificationList != null)
                itemsIndexed(notificationList) { index, notification ->
                    ListItem(
                        colors = CustomListItemColors.listItemColors(),
                        shape = ListItemDefaults.shape(RoundedCornerShape(10.dp)),
                        selected = false,
                        onClick = { navigateToSeriesById(notification.summary.notificationData.serialId.toString()) },
                        headlineContent = {
                            Text(
                                text = notification.text.substringBefore("<br>"),
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
                    if (index in 0 until notificationList.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier
                                .padding(start = Dimens.mainPadding, end = Dimens.mainPadding),
                            thickness = 1.dp
                        )
                    }
                }
        }
    }
}