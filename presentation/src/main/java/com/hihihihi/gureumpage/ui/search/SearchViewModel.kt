package com.hihihihi.gureumpage.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchViewModel @Inject constructor(
) : ViewModel() {
    private val _searchResults = MutableStateFlow<List<Search>>(emptyList())
    val searchResults: StateFlow<List<Search>> = _searchResults.asStateFlow()

    fun search(query: String) {
        viewModelScope.launch {
            //TODO 실제 API 통신 로직 구현 필요함

            //테스트를 위한 더미데이터
            val dummyData = mutableListOf<Search>()
            if (query == "a") {
                dummyData.add(
                    Search(
                        imgUrl = "https://contents.kyobobook.co.kr/sih/fit-in/458x0/pdt/9791158511982.jpg",
                        title = "title1",
                        author = "author1",
                        publisher = "publisher1",
                        page = 1
                    )
                )
                dummyData.add(
                    Search(
                        imgUrl = "https://contents.kyobobook.co.kr/sih/fit-in/458x0/pdt/9791158511982.jpg",
                        title = "title2",
                        author = "author2",
                        publisher = "publisher2",
                        page = 1
                    )
                )
            }
            _searchResults.value = dummyData
        }
    }
}
