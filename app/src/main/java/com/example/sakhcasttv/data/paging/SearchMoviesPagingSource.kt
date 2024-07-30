package com.example.sakhcasttv.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.sakhcasttv.data.repository.SakhCastRepository
import com.example.sakhcasttv.model.MovieCard

class SearchMoviesPagingSource(
    private val sakhCastRepository: SakhCastRepository,
    private val textInput: String
) : PagingSource<Int, MovieCard>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieCard> {
        try {
            val page = params.key ?: 0
            val seriesList = searchMovies(textInput, page)
            val nextKey = if (seriesList?.items?.size == 40) page + 1 else null

            return LoadResult.Page(
                data = seriesList?.items ?: emptyList(),
                prevKey = if (page == 0) null else page - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    private suspend fun searchMovies(textInput: String, page: Int) =
        sakhCastRepository.searchMovie(textInput, page)

    override fun getRefreshKey(state: PagingState<Int, MovieCard>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}