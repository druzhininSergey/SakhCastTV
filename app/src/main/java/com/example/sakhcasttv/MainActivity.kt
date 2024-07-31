package com.example.sakhcasttv


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.tv.material3.Surface
import com.example.sakhcasttv.data.registerOnBackPress
import com.example.sakhcasttv.ui.MainScreen
import com.example.sakhcasttv.ui.general.CustomDialog
import com.example.sakhcasttv.ui.theme.SakhCastTVTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            val displayDialog = remember {
                mutableStateOf(false)
            }

            registerOnBackPress {
                displayDialog.value = true
            }
            if (displayDialog.value) {
                CustomDialog(openDialogCustom = displayDialog) {
                    finish()
                }
            }


            SakhCastTVTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape
                ) {
                    MainScreen()
                }
            }
        }
    }
}