package com.example.sakhcasttv.ui.main_screens.notifications_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.sakhcasttv.Dimens
import com.example.sakhcasttv.ui.main_screens.main_screen_tabrow.MainScreensViewModel
import kotlinx.coroutines.flow.StateFlow


@Preview
@Composable
fun PreviewNotificationScreen() {
//    NotificationScreen(
//        paddingValues = PaddingValues(20.dp),
//        notificationScreenState = notificationScreenState
//    )
}

@Composable
fun NotificationScreen(
    notificationScreenState: StateFlow<MainScreensViewModel.NotificationScreenState>,
    navigateToSeriesById: (String) -> Unit
) {
    val notificationScreenStateCollected by notificationScreenState.collectAsState()
        val notificationList = notificationScreenStateCollected.notificationsList?.items

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (notificationList != null)
                itemsIndexed(notificationList) { index, notification ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navigateToSeriesById(notification.summary.notificationData.serialId.toString()) },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(start = Dimens.mainPadding, top = 3.dp, bottom = 3.dp)
                                .widthIn(max = 350.dp),
                            text = notification.text.substringBefore("<br>"),
                            fontSize = 12.sp,
                        )
                    }
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