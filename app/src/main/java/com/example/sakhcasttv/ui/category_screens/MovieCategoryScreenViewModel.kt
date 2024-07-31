package com.example.sakhcasttv.ui.category_screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.sakhcasttv.data.paging.MoviesPagingSource
import com.example.sakhcasttv.data.repository.SakhCastRepository
import com.example.sakhcasttv.model.MovieCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieCategoryScreenViewModel
@Inject constructor(private val sakhCastRepository: SakhCastRepository) : ViewModel() {

    private var _moviePagingData = MutableStateFlow<Flow<PagingData<MovieCard>>?>(null)
    val moviePagingData = _moviePagingData.asStateFlow()

    private var _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun initCategory(categoryName: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            if (categoryName != null) {
                try {
                    val pagingSource = MoviesPagingSource(sakhCastRepository, categoryName)
                    val pager = Pager(
                        config = PagingConfig(
                            pageSize = 40,
                            prefetchDistance = 1,
                            initialLoadSize = 40,
                            enablePlaceholders = false
                        ),
                        pagingSourceFactory = { pagingSource }
                    )
                    val flow = pager.flow.cachedIn(viewModelScope)
                    _moviePagingData.value = flow
                } catch (e: Exception) {
                    println("Error initializing category: ${e.message}")
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

}