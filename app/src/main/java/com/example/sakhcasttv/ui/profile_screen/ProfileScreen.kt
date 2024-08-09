package com.example.sakhcasttv.ui.profile_screen

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.sakhcasttv.CURRENT_VERSION
import com.example.sakhcasttv.Colors
import com.example.sakhcasttv.model.CurrentUser
import java.util.Locale

@Composable
fun ProfileScreen(
    user: CurrentUser?,
    onLogoutButtonPushed: () -> Unit,
    navigateUp: () -> Boolean,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val downloadSpeed by viewModel.downloadSpeed.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val avatarPainter: Painter =
        rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current).data(data = user?.avatar)
                .apply(block = fun ImageRequest.Builder.() {
                    crossfade(true)
                }).build()
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 40.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(text = "Учетная запись")
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                IconButton(
                    onClick = { navigateUp() },
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                    colors = ButtonDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.onTertiaryContainer)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
        Box(
            modifier = Modifier
                .padding(16.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.inverseOnSurface)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(100.dp)
                        .clip(CircleShape),
                    painter = avatarPainter,
                    contentDescription = null
                )
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (user != null) {
                        Text(text = user.login)
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(
                            text = user.proDays.toString() + "дн.",
                            color = Color.Black,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.small)
                                .background(Colors.proDaysCountGreenColor)
                                .padding(4.dp)
                        )
                    }
                }
            }
        }

        Text(
            text = "Текущая версия: $CURRENT_VERSION",
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                colors = ButtonDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.onTertiaryContainer),
                onClick = { viewModel.measureDownloadSpeed() },
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
            ) {
                Text(text = "Измерить скорость загрузки", color = Colors.blueColor)
            }

            if (isLoading) {
                CircularProgressIndicator(color = Colors.blueColor)
            } else {
                downloadSpeed?.let { speed ->
                    Text(
                        text = when {
                            speed < 0 -> "Ошибка измерения"
                            speed < 1 -> String.format(Locale.US,"%.2f Kbps", speed * 1000)
                            else -> String.format(Locale.US,"%.2f Mbps", speed)
                        }
                    )
                }
            }
        }

        Button(
            colors = ButtonDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.onTertiaryContainer),
            onClick = { onLogoutButtonPushed() },
            modifier = Modifier
                .padding(16.dp)
                .clip(MaterialTheme.shapes.small)
                .fillMaxWidth(),
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(text = "Выйти из аккаунта", color = Colors.blueColor)
            }
        }
    }
}
