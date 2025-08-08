package com.hihihihi.gureumpage.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.model.SearchBook
import com.hihihihi.domain.usecase.search.SearchBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchBooksUseCase: SearchBooksUseCase
) : ViewModel() {
    private val _searchResults = MutableStateFlow<List<SearchBook>>(emptyList())
    val searchResults: StateFlow<List<SearchBook>> = _searchResults.asStateFlow()

    fun search(query: String) {
        viewModelScope.launch {
            try {
                val results = searchBooksUseCase(query)
                _searchResults.value = results
            } catch (e: Exception) {
                println("검색 오류: ${e.message}")
                _searchResults.value = emptyList()
            }
        }
    }
}
