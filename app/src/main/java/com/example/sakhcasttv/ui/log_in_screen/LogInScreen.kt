package com.example.sakhcasttv.ui.log_in_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Lock
import androidx.compose.material.icons.twotone.Person
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.sakhcasttv.HOME_SCREEN
import com.example.sakhcasttv.R

@Composable
fun LogInScreen(
    navController: NavHostController,
    logInScreenViewModel: LogInScreenViewModel = hiltViewModel(),
) {
    val logInScreenState =
        logInScreenViewModel.userDataState.observeAsState(LogInScreenViewModel.UserDataState())
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordHidden by rememberSaveable { mutableStateOf(true) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .padding()
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_sakh_tv_logo),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .padding(top = 80.dp)
                .width(191.dp)
                .height(68.dp)
        )

        OutlinedTextField(
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.DarkGray,
                focusedIndicatorColor = Color.DarkGray,
                cursorColor = Color.DarkGray,
                focusedPlaceholderColor = Color.DarkGray,
                unfocusedLabelColor = Color.DarkGray,
                focusedLabelColor = Color.DarkGray,
                focusedContainerColor = Color.DarkGray,
                unfocusedContainerColor = Color.DarkGray,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            modifier = Modifier.padding(top = 20.dp),
            shape = RoundedCornerShape(10.dp),
            value = login,
            onValueChange = { login = it },
            label = {
                Text(
                    "Логин",
                    color = Color.White,
                )
            },
            leadingIcon = { Icon(imageVector = Icons.TwoTone.Person, contentDescription = null) },
            singleLine = true,
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.DarkGray,
                focusedIndicatorColor = Color.DarkGray,
                cursorColor = Color.DarkGray,
                focusedPlaceholderColor = Color.DarkGray,
                unfocusedLabelColor = Color.DarkGray,
                focusedLabelColor = Color.DarkGray,
                focusedContainerColor = Color.DarkGray,
                unfocusedContainerColor = Color.DarkGray,
            ),
            shape = RoundedCornerShape(10.dp),
            value = password,
            leadingIcon = { Icon(imageVector = Icons.TwoTone.Lock, contentDescription = null) },
            onValueChange = { password = it },
            singleLine = true,
            label = { Text("Пароль", color = Color.White) },
            visualTransformation =
            if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    logInScreenViewModel.checkUserData(login, password)
                    if (logInScreenState.value.isLogged == true) navController.navigate(HOME_SCREEN)
                }
            ),
            trailingIcon = {
                IconButton(onClick = { passwordHidden = !passwordHidden }) {
                    val visibilityIcon =
                        if (passwordHidden) painterResource(id = R.drawable.ic_visibility)
                        else painterResource(id = R.drawable.ic_visibility_off)
                    val description = if (passwordHidden) "Show password" else "Hide password"
                    Icon(painter = visibilityIcon, contentDescription = description)
                }
            }
        )
        if (!logInScreenState.value.isPasswordCorrect) {
            Text(
                text = "Неверный логин или пароль",
                modifier = Modifier
                    .padding(top = 20.dp)
                    .background(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    )
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        } else if (!logInScreenState.value.isUserPro) {
            Text(
                text = "У вас не активна PRO подписка",
                modifier = Modifier
                    .padding(top = 20.dp)
                    .background(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    )
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
        Button(
            onClick = {
                logInScreenViewModel.checkUserData(login, password)
                if (logInScreenState.value.isLogged == true) navController.navigate(HOME_SCREEN)
            },
            shape = ButtonDefaults.shape(MaterialTheme.shapes.medium),
            modifier = Modifier.padding(top = 20.dp),
            colors = ButtonDefaults.colors(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Text(text = "Войти")
        }
    }
}