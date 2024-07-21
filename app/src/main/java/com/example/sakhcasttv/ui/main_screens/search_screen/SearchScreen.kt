package com.example.sakhcasttv.ui.main_screens.search_screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Search
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import com.example.sakhcasttv.Dimens
import com.example.sakhcasttv.ui.main_screens.main_screen_tabrow.MenuItem
import com.example.sakhcasttv.ui.main_screens.main_screen_tabrow.NavigationTabItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
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
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState { pages.size }
    val lazyGridState = rememberLazyGridState()
    var searchJob: Job? = remember { null }
    val keyboardController = LocalSoftwareKeyboardController.current
    var selectedTabIndex by remember { mutableIntStateOf(pagerState.currentPage) }
    LaunchedEffect(tabIndex) {
        pagerState.animateScrollToPage(tabIndex)
        if (textInput.isNotEmpty()) {
            if (tabIndex == 0) {
                searchScreenViewModel.searchSeries(textInput)
            } else {
                searchScreenViewModel.searchMovies(textInput)
            }
        }
    }
    LaunchedEffect(pagerState.currentPage) {
        tabIndex = pagerState.currentPage
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

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (tabIndex == 0) searchScreenViewModel.searchSeries(textInput)
                    else searchScreenViewModel.searchMovies(textInput)
                    keyboardController?.hide()
                }
            ),
            shape = RoundedCornerShape(10.dp),
            value = textInput,
            onValueChange = { textInput = it },
            label = {
                Text(
                    text = "Введите название...",
                    color = MaterialTheme.colorScheme.onBackground,
                )
            },
            leadingIcon = { Icon(imageVector = Icons.TwoTone.Search, contentDescription = null) },
            singleLine = true,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp, bottom = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                indicator = { _, _ ->
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .background(Color.Transparent)
                    )
                },
            ) {
                pages.forEachIndexed { index, title ->
                    NavigationTabItem(
                        item = MenuItem(id = title, text = title),
                        isSelected = selectedTabIndex == index,
                        onMenuSelected = {
                            selectedTabIndex = index
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(5.dp).background(MaterialTheme.colorScheme.background))
        HorizontalPager(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            state = pagerState,
            userScrollEnabled = false
        ) { index ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding()
                    .background(MaterialTheme.colorScheme.primary)
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.mainPadding),
                verticalArrangement = Arrangement.spacedBy(Dimens.mainPadding),
                contentPadding = PaddingValues(Dimens.mainPadding),
                state = lazyGridState
            ) {
//                if (index == 0) {
//                    seriesPagingItems?.itemCount?.let {
//                        items(it) { index2 ->
//                            seriesPagingItems[index2]?.let { seriesCard ->
//                                SeriesCategoryCardItem(seriesCard, navigateToSeriesById)
//                            }
//                        }
//                    }
//                } else {
//                    moviePagingItems?.itemCount?.let {
//                        items(it) { index3 ->
//                            moviePagingItems[index3]?.let { movieCard ->
//                                MovieCategoryCardItem(movieCard, navigateToMovieByAlphaId)
//                            }
//                        }
//                    }
//                }
            }
        }
    }
}