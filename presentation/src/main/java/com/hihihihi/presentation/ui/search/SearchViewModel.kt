package com.hihihihi.presentation.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hihihihi.domain.model.Mindmap
import com.hihihihi.domain.model.MindmapNode
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.model.SearchBook
import com.hihihihi.domain.model.UserBook
import com.hihihihi.domain.usecase.auth.GetCurrentUserIdUseCase
import com.hihihihi.domain.usecase.search.GetBookPageCountUseCase
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
    private val getBookPageCountUseCase: GetBookPageCountUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val currentUid: String?
        get() = getCurrentUserIdUseCase()

    fun selectBook(book: SearchBook?) {
        _uiState.update { it.copy(selectedBook = book) }
    }

    fun search(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                query = query,
                isSearching = true,
                searchResults = emptyList(),
                page = 1,
                hasSearched = true,
            )
            try {
                val results = searchBooksUseCase(query, page = 1, pageSize = PAGE_SIZE).getOrThrow()
                val dedup = results.distinctBy { it.isbn }

                _uiState.value = _uiState.value.copy(
                    searchResults = dedup,
                    isSearching = false,
                    hasMore = canLoadMore(dedup.size, 1),
                    page = 1,
                )
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(
                    searchResults = emptyList(),
                    isSearching = false,
                    hasMore = false,
                    page = 0,
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
                    searchBooksUseCase(state.query, page = nextPage, pageSize = PAGE_SIZE).getOrThrow()

                val before = state.searchResults
                val merged = (before + newResults).distinctBy { it.isbn }

                val actuallyGrew = merged.size > before.size

                _uiState.value = state.copy(
                    searchResults = merged,
                    page = nextPage,
                    isLoadingMore = false,
                    hasMore = canLoadMore(merged.size, nextPage) && actuallyGrew && newResults.isNotEmpty(),
                )
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isLoadingMore = false,
                    hasMore = false,
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

    fun getBookPageCount(isbn: String, onResult: (Int?) -> Unit) {
        viewModelScope.launch {
            val result = getBookPageCountUseCase(isbn)
            onResult(result.getOrNull())
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
                val uid = currentUid ?: throw Exception("로그인이 필요합니다.")

                val userBook = UserBook(
                    userBookId = "",
                    userId = uid,
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
                    category = searchBook.categoryName.split(">").getOrNull(1)?.trim() ?: "",
                )

                val mindmap = Mindmap(
                    userId = uid,
                    mindmapId = "",
                    userBookId = "",
                    rootNodeId = "",
                )

                val rootNode = MindmapNode(
                    userId = uid,
                    mindmapNodeId = "",
                    mindmapId = "",
                    nodeTitle = searchBook.title,
                    nodeEx = searchBook.description,
                    parentNodeId = null,
                    color = null,
                    icon = null,
                    deleted = false,
                    bookImage = searchBook.coverImageUrl,
                )

                val result = addUserBookUseCase(uid, userBook, mindmap, rootNode)

                if (result.isSuccess) {
                    _uiState.value =
                        _uiState.value.copy(
                            isAddingBook = false,
                            addBookMessage = "책이 추가되었습니다",
                            isAddBookSuccess = true,
                            selectedBook = null,
                        )
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "알 수 없는 오류가 발생했습니다."
                    _uiState.value =
                        _uiState.value.copy(
                            isAddingBook = false,
                            addBookMessage = errorMessage,
                            isAddBookSuccess = false,
                        )
                }
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isAddingBook = false,
                        addBookMessage = e.message ?: "알 수 없는 오류가 발생했습니다.",
                        isAddBookSuccess = false,
                    )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(
            addBookMessage = "",
            isAddBookSuccess = false,
        )
    }
}
