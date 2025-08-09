package com.hihihihi.gureumpage.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.model.SearchBook
import com.hihihihi.domain.repository.SearchRepository
import com.hihihihi.domain.usecase.search.SearchBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchBooksUseCase: SearchBooksUseCase,
    private val searchRepository: SearchRepository
) : ViewModel() {
    private val _searchResults = MutableStateFlow<List<SearchBook>>(emptyList())
    val searchResults: StateFlow<List<SearchBook>> = _searchResults.asStateFlow()

    fun search(query: String) {
        viewModelScope.launch {
            try {
                val results = searchBooksUseCase(query)
                _searchResults.value = results
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            }
        }
    }
    
    fun getBookPageCount(isbn: String, onResult: (Int?) -> Unit) {
        viewModelScope.launch {
            try {
                val pageCount = searchRepository.getBookPageCount(isbn)
                onResult(pageCount)
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }
}
