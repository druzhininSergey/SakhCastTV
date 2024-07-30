package com.example.sakhcasttv.ui.main_screens.search_screen

import android.view.KeyEvent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import com.example.sakhcasttv.Dimens
import com.example.sakhcasttv.model.MovieCard
import com.example.sakhcasttv.model.SeriesCard
import com.example.sakhcasttv.ui.general.MenuItem
import com.example.sakhcasttv.ui.general.NavigationTabItem
import com.example.sakhcasttv.ui.main_screens.home_screen.movie.MovieItemView
import com.example.sakhcasttv.ui.main_screens.home_screen.series.SeriesItemView
import com.example.sakhcasttv.ui.theme.JetStreamCardShape
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    navigateToMovieByAlphaId: (String) -> Unit,
    navigateToSeriesById: (String) -> Unit,
    searchScreenViewModel: SearchScreenViewModel = hiltViewModel()
) {
    val seriesPagingItems =
        searchScreenViewModel.seriesList.observeAsState().value?.collectAsLazyPagingItems()
    val moviePagingItems =
        searchScreenViewModel.movieList.observeAsState().value?.collectAsLazyPagingItems()
    var textInput by rememberSaveable { mutableStateOf("") }
    val pages = listOf("Сериалы", "Фильмы")
    var tabIndex by remember { mutableIntStateOf(0) }
    var searchJob: Job? = remember { null }
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(tabIndex) {
        if (textInput.isNotEmpty()) {
            if (tabIndex == 0) {
                searchScreenViewModel.searchSeries(textInput)
            } else {
                searchScreenViewModel.searchMovies(textInput)
            }
        }
    }
    LaunchedEffect(key1 = textInput) {
        searchJob?.cancel()
        searchJob = launch {
            delay(1000)
            if (textInput.isNotEmpty() && textInput.length > 2) {
                if (tabIndex == 0) {
                    searchScreenViewModel.searchSeries(textInput)
                } else {
                    searchScreenViewModel.searchMovies(textInput)
                }
            }
        }
    }
    val tfFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val tfInteractionSource = remember { MutableInteractionSource() }
    val isTfFocused by tfInteractionSource.collectIsFocusedAsState()

    LazyColumn(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            SearchBar(
                textInput = textInput,
                onTextInputChange = { textInput = it },
                tfFocusRequester = tfFocusRequester,
                tfInteractionSource = tfInteractionSource,
                isTfFocused = isTfFocused,
                focusManager = focusManager,
                onSearch = {
                    if (tabIndex == 0) searchScreenViewModel.searchSeries(textInput)
                    else searchScreenViewModel.searchMovies(textInput)
                    keyboardController?.hide()
                }
            )
        }

        item {
            TabRowContent(
                pages = pages,
                tabIndex = tabIndex,
                onTabSelected = { tabIndex = it }
            )
        }

        item {
            SearchContentGrid(
                tabIndex = tabIndex,
                seriesPagingItems = seriesPagingItems,
                moviePagingItems = moviePagingItems,
                navigateToSeriesById = navigateToSeriesById,
                navigateToMovieByAlphaId = navigateToMovieByAlphaId
            )
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(
    textInput: String,
    onTextInputChange: (String) -> Unit,
    tfFocusRequester: FocusRequester,
    tfInteractionSource: MutableInteractionSource,
    isTfFocused: Boolean,
    focusManager: FocusManager,
    onSearch: () -> Unit
) {
    Surface(
        shape = ClickableSurfaceDefaults.shape(shape = JetStreamCardShape),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.inverseOnSurface,
            focusedContainerColor = MaterialTheme.colorScheme.inverseOnSurface,
            pressedContainerColor = MaterialTheme.colorScheme.inverseOnSurface,
            focusedContentColor = MaterialTheme.colorScheme.onSurface,
            pressedContentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(
                    width = if (isTfFocused) 2.dp else 1.dp,
                    color = animateColorAsState(
                        targetValue = if (isTfFocused) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.border,
                        label = ""
                    ).value
                ),
                shape = JetStreamCardShape
            )
        ),
        tonalElevation = 2.dp,
        modifier = Modifier
            .padding(horizontal = 30.dp)
            .padding(top = 8.dp),
        onClick = { tfFocusRequester.requestFocus() }
    ) {
        BasicTextField(
            value = textInput,
            onValueChange = onTextInputChange,
            decorationBox = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .padding(start = 20.dp),
                ) {
                    it()
                    if (textInput.isEmpty()) {
                        Text(
                            modifier = Modifier.graphicsLayer { alpha = 0.6f },
                            text = "Введите название...",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 4.dp,
                    horizontal = 8.dp
                )
                .focusRequester(tfFocusRequester)
                .onKeyEvent {
                    if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                        when (it.nativeKeyEvent.keyCode) {
                            KeyEvent.KEYCODE_DPAD_DOWN -> {
                                focusManager.moveFocus(FocusDirection.Down)
                            }

                            KeyEvent.KEYCODE_DPAD_UP -> {
                                focusManager.moveFocus(FocusDirection.Up)
                            }

                            KeyEvent.KEYCODE_BACK -> {
                                focusManager.moveFocus(FocusDirection.Exit)
                            }
                        }
                    }
                    true
                },
            cursorBrush = Brush.verticalGradient(
                colors = listOf(
                    LocalContentColor.current,
                    LocalContentColor.current,
                )
            ),
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            maxLines = 1,
            interactionSource = tfInteractionSource,
            textStyle = MaterialTheme.typography.titleSmall.copy(
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Composable
fun TabRowContent(
    pages: List<String>,
    tabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, bottom = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        TabRow(
            selectedTabIndex = tabIndex,
            indicator = { _, _ ->
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(Color.Transparent)
                )
            },
        ) {
            pages.forEachIndexed { index, page ->
                NavigationTabItem(
                    item = MenuItem(id = page, text = page),
                    isSelected = tabIndex == index,
                    onMenuSelected = { onTabSelected(index) },
                )
            }
        }
    }
}

@Composable
fun SearchContentGrid(
    tabIndex: Int,
    seriesPagingItems: LazyPagingItems<SeriesCard>?,
    moviePagingItems: LazyPagingItems<MovieCard>?,
    navigateToSeriesById: (String) -> Unit,
    navigateToMovieByAlphaId: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        modifier = Modifier
            .padding()
            .background(MaterialTheme.colorScheme.background)
            .height(450.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.mainPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.mainPadding),
        contentPadding = PaddingValues(Dimens.mainPadding)
    ) {
        if (tabIndex == 0) {
            seriesPagingItems?.itemCount?.let {
                items(it) { index2 ->
                    seriesPagingItems[index2]?.let { seriesCard ->
                        SeriesItemView(seriesCard, navigateToSeriesById)
                    }
                }
            }
        } else {
            moviePagingItems?.itemCount?.let {
                items(it) { index3 ->
                    moviePagingItems[index3]?.let { movieCard ->
                        MovieItemView(movieCard, navigateToMovieByAlphaId)
                    }
                }
            }
        }
    }
}