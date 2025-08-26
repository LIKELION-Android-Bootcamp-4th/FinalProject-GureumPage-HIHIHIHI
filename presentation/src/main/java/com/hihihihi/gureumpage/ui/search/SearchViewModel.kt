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
import com.hihihihi.domain.usecase.search.SearchBooksUseCase
import com.hihihihi.domain.usecase.userbook.AddUserBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

private const val PAGE_SIZE = 10
private const val MAX_TOTAL_RESULTS = 200

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchBooksUseCase: SearchBooksUseCase,
    private val addUserBookUseCase: AddUserBookUseCase,
    private val searchRepository: SearchRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUid: String
        get() = auth.currentUser!!.uid

    fun search(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                query = query,
                isSearching = true,
                searchResults = emptyList(),
                page = 1
            )
            try {
                val results = searchBooksUseCase(query, page = 1, pageSize = PAGE_SIZE)
                val dedup = results.distinctBy { it.isbn }

                Log.d("SearchVM", "query=$query, results.size=${results.size}")

                _uiState.value = _uiState.value.copy(
                    searchResults = dedup,
                    isSearching = false,
                    hasMore = canLoadMore(dedup.size, 1),
                    page = 1
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    searchResults = emptyList(),
                    isSearching = false,
                    hasMore = false,
                    page = 0
                )
            }
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isLoadingMore || state.isPaging || !state.hasMore) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }

            try {
                val nextPage = state.page + 1
                val newResults =
                    searchBooksUseCase(state.query, page = nextPage, pageSize = PAGE_SIZE)

                val before = state.searchResults
                val merged = (before + newResults).distinctBy { it.isbn }

                val actuallyGrew = merged.size > before.size

                _uiState.value = state.copy(
                    searchResults = merged,
                    page = nextPage,
                    isLoadingMore = false,
                    hasMore = canLoadMore(merged.size, nextPage) && actuallyGrew && newResults.isNotEmpty()
                )
            } catch (e: Exception) {
                Log.e("SearchVM", "LoadMore failed", e)
                _uiState.value = state.copy(
                    isLoadingMore = false,
                    hasMore = false
                )
            }
        }
    }

    private fun canLoadMore(currentResultSize: Int, currentPage: Int): Boolean {
        if (currentResultSize >= MAX_TOTAL_RESULTS) return false

        if (currentPage >= 20) return false

        if (currentResultSize % PAGE_SIZE != 0 && currentPage > 1) return false

        return true
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
            _uiState.value = _uiState.value.copy(isAddingBook = true)

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
                    userId = FirebaseAuth.getInstance().currentUser?.uid!!,
                    mindmapId = "",
                    userBookId = "",
                    rootNodeId = "",
                )

                val rootNode = MindmapNode(
                    userId = FirebaseAuth.getInstance().currentUser?.uid!!,
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

                val result = addUserBookUseCase(currentUid, userBook, mindmap, rootNode)

                if (result.isSuccess) {
                    _uiState.value =
                        _uiState.value.copy(
                            isAddingBook = false,
                            addBookMessage = "책이 추가되었습니다",
                            isAddBookSuccess = true
                        )
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "알 수 없는 오류가 발생했습니다."
                    _uiState.value =
                        _uiState.value.copy(
                            isAddingBook = false,
                            addBookMessage = errorMessage,
                            isAddBookSuccess = false
                        )
                }
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isAddingBook = false,
                        addBookMessage = e.message ?: "알 수 없는 오류가 발생했습니다.",
                        isAddBookSuccess = false
                    )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(
            addBookMessage = "",
            isAddBookSuccess = false
        )
    }
}
