package com.example.sakhcasttv.ui.profile_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.sakhcasttv.CURRENT_VERSION
import com.example.sakhcasttv.Colors
import com.example.sakhcasttv.model.CurrentUser

@Composable
fun ProfileScreen(user: CurrentUser?, onLogoutButtonPushed: () -> Unit) {
    val avatarPainter: Painter =
        rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current).data(data = user?.avatar)
                .apply(block = fun ImageRequest.Builder.() {
                    crossfade(true)
                }).build()
        )
//    val profileScreenState = logInScreenViewModel.userDataState.observeAsState(
//        LogInScreenViewModel.UserDataState()
//    )
//    val currentUser = profileScreenState.value.currentUser
//
//    LaunchedEffect(Unit) {
//        logInScreenViewModel.checkLoggedUser()
//    }

    Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(text = "Учетная запись")
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

        TextButton(
            onClick = {
                onLogoutButtonPushed()
            }, modifier = Modifier
                .padding(16.dp)
                .clip(MaterialTheme.shapes.small)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            Text(text = "Выйти из аккаунта", color = Colors.blueColor)
        }
        Spacer(modifier = Modifier.height(200.dp))
    }

}
