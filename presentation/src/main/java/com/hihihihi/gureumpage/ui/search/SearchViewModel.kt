package com.hihihihi.gureumpage.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hihihihi.domain.model.Mindmap
import com.hihihihi.domain.model.MindmapNode
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.model.SearchBook
import com.hihihihi.domain.model.UserBook
import com.hihihihi.domain.repository.SearchRepository
import com.hihihihi.domain.usecase.mindmap.CreateMindmapUseCase
import com.hihihihi.domain.usecase.search.SearchBooksUseCase
import com.hihihihi.domain.usecase.userbook.AddUserBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchBooksUseCase: SearchBooksUseCase,
    private val addUserBookUseCase: AddUserBookUseCase,
    private val searchRepository: SearchRepository,
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

    // TODO: usecase로 일관성 맞추기
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

    fun addUserBook(
        searchBook: SearchBook,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        currentPage: Int,
        totalPage: Int,
        status: ReadingStatus,
    ) {
        viewModelScope.launch {
            Log.e("TAG", "addUserBook: ${searchBook.publisher} ${searchBook.description}", )
            try {
                val userBook = UserBook(
                    userBookId = "",
                    userId = FirebaseAuth.getInstance().currentUser?.uid!!,
                    isbn10 = "",
                    isbn13 = searchBook.isbn,
                    title = searchBook.title,
                    author = searchBook.author,
                    publisher = searchBook.publisher,
                    description = searchBook.description,
                    imageUrl = searchBook.coverImageUrl,
                    isLiked = false,
                    totalPage = totalPage,
                    currentPage = currentPage,
                    startDate = startDate,
                    endDate = endDate,
                    totalReadTime = 0,
                    status = status,
                    review = null,
                    rating = null,
                    category = searchBook.categoryName.split(">")[1],
                )

                val mindmap = Mindmap(
                    mindmapId = "",
                    userBookId = "",
                    rootNodeId = "",
                )

                val rootNode = MindmapNode(
                    mindmapNodeId = "",
                    mindmapId = "",
                    nodeTitle = searchBook.title,
                    nodeEx = searchBook.description,
                    parentNodeId = null,
                    color = null,
                    icon = null,
                    deleted = false,
                    bookImage = searchBook.coverImageUrl
                )

                addUserBookUseCase(userBook, mindmap, rootNode)
                // TODO: 성공 실패 처리 하기
            } catch (e: Exception) {

            }
        }
    }
}
