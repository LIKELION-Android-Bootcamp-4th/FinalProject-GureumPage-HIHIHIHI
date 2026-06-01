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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
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
    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Content())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _effect = Channel<SearchEffect>(Channel.BUFFERED)
    val effect: Flow<SearchEffect> = _effect.receiveAsFlow()

    private val currentUid: String?
        get() = getCurrentUserIdUseCase()

    fun selectBook(book: SearchBook?) {
        updateContent { it.copy(selectedBook = book) }
    }

    fun search(query: String) {
        viewModelScope.launch {
            updateContent {
                it.copy(
                    query = query,
                    isSearching = true,
                    searchResults = emptyList(),
                    page = 1,
                    hasSearched = true,
                )
            }
            try {
                val results = searchBooksUseCase(query, page = 1, pageSize = PAGE_SIZE).getOrThrow()
                val dedup = results.distinctBy { it.isbn }

                updateContent {
                    it.copy(
                        searchResults = dedup,
                        isSearching = false,
                        hasMore = canLoadMore(dedup.size, 1),
                        page = 1,
                    )
                }
            } catch (_: Exception) {
                updateContent {
                    it.copy(
                        searchResults = emptyList(),
                        isSearching = false,
                        hasMore = false,
                        page = 0,
                    )
                }
            }
        }
    }

    fun loadMore() {
        val state = _uiState.value.contentOrDefault()
        if (state.isLoadingMore || state.isPaging || !state.hasMore) return

        viewModelScope.launch {
            // 요청 시점의 쿼리를 캡처해 유효성 검증에 사용
            val requestQuery = state.query
            val snapshotPage = state.page
            val snapshotResults = state.searchResults
            updateContent { it.copy(isLoadingMore = true) }

            try {
                val nextPage = snapshotPage + 1
                val newResults =
                    searchBooksUseCase(requestQuery, page = nextPage, pageSize = PAGE_SIZE).getOrThrow()

                val merged = (snapshotResults + newResults).distinctBy { it.isbn }
                val actuallyGrew = merged.size > snapshotResults.size

                _uiState.update { current ->
                    val content = current.contentOrDefault()
                    // 응답을 기다리는 동안 새 검색이 시작됐으면 결과를 버리고 로딩 플래그만 해제
                    if (content.query != requestQuery) {
                        content.copy(isLoadingMore = false)
                    } else {
                        content.copy(
                            searchResults = merged,
                            page = nextPage,
                            isLoadingMore = false,
                            hasMore = canLoadMore(merged.size, nextPage) && actuallyGrew && newResults.isNotEmpty(),
                        )
                    }
                }
            } catch (_: Exception) {
                // 일시적 오류(타임아웃 등)에서는 hasMore를 변경하지 않아 사용자가 재시도 가능하게 유지
                updateContent { it.copy(isLoadingMore = false) }
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
            updateContent { it.copy(isAddingBook = true) }

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
                    updateContent {
                        it.copy(
                            isAddingBook = false,
                            selectedBook = null,
                        )
                    }
                    _effect.send(SearchEffect.ShowMessage("책이 추가되었습니다"))
                } else {
                    val failureMessage = result.exceptionOrNull()?.message ?: "알 수 없는 오류가 발생했습니다."
                    updateContent { it.copy(isAddingBook = false) }
                    _effect.send(SearchEffect.ShowMessage(failureMessage))
                }
            } catch (e: Exception) {
                updateContent { it.copy(isAddingBook = false) }
                _effect.send(SearchEffect.ShowMessage(e.message ?: "알 수 없는 오류가 발생했습니다."))
            }
        }
    }

    fun clearMessage() {
        // Messages are delivered through SearchEffect.
    }

    private inline fun updateContent(
        crossinline transform: (SearchUiState.Content) -> SearchUiState.Content,
    ) {
        _uiState.update { current -> transform(current.contentOrDefault()) }
    }
}

sealed interface SearchEffect {
    data class ShowMessage(val message: String) : SearchEffect
}
