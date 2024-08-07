package com.example.sakhcasttv.ui.general

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.sakhcasttv.R

@Composable
fun CustomDialog(
    openDialogCustom: MutableState<Boolean>,
    text: String,
    onExitClick: (() -> Unit)? = null,
    navigateUp: (() -> Boolean)? = null,
) {
    Dialog(onDismissRequest = { openDialogCustom.value = false }) {
        CustomDialogUI(
            openDialogCustom = openDialogCustom,
            text = text,
            onExitClick = onExitClick,
            navigateUp = navigateUp
        )
    }
}

// Layout
@Composable
fun CustomDialogUI(
    modifier: Modifier = Modifier,
    openDialogCustom: MutableState<Boolean>,
    text: String,
    onExitClick: (() -> Unit)? = null,
    navigateUp: (() -> Boolean)? = null,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(openDialogCustom.value) {
        if (openDialogCustom.value) {
            focusRequester.requestFocus()
        }
    }

    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(10.dp, 5.dp, 10.dp, 10.dp),
    ) {
        Column(modifier.background(MaterialTheme.colorScheme.surface)) {
            // .......................................................................
            Image(
                painter = painterResource(id = R.drawable.ic_info),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                modifier = Modifier
                    .padding(top = 35.dp)
                    .height(70.dp)
                    .fillMaxWidth(),

                )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Подтверждение",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = text,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .background(MaterialTheme.colorScheme.background.copy(0.2F)),
            ) {
                TextButton(
                    onClick = {
                        openDialogCustom.value = false
                    }, modifier = Modifier
                        .weight(1F)
                        .focusRequester(focusRequester),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text(
                        "Нет",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
                    )
                }

                TextButton(
                    onClick = {
                        openDialogCustom.value = false
                        if (onExitClick != null) onExitClick()
                        else if (navigateUp != null) navigateUp()
                    }, modifier = Modifier
                        .weight(1F),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text(
                        "Да",
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
                    )
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(name = "Custom Dialog")
@Composable
fun MyDialogUIPreview() {
    CustomDialogUI(
        openDialogCustom = mutableStateOf(false),
        text = "Желаете выйти из приложения?"

    )
}