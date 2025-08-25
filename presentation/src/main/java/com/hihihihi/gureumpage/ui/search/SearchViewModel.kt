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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.math.min


private const val INITIAL_COUNT = 10
private const val LOAD_COUNT = 10
private const val PAGE_SIZE = 10

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
                page = 1,
                isSearching = true,
                isPaging = false,
                isLoadingMore = false,
                hasMore = true,
                visibleCount = 0
            )
            try {
                val results = searchBooksUseCase(query)
                Log.d("SearchVM", "query=$query, results.size=${results.size}")
                val initialShown = min(INITIAL_COUNT, results.size)
                _uiState.value = _uiState.value.copy(
                    searchResults = results,
                    visibleCount = initialShown,
                    isSearching = false,
                    hasMore = results.size >= PAGE_SIZE,
                    page = 1
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    searchResults = emptyList(),
                    visibleCount = 0,
                    isSearching = false,
                    hasMore = false
                )
            }
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isLoadingMore || state.isPaging) return
        if (!state.hasMore) return

        viewModelScope.launch {
            _uiState.value = state.copy(isLoadingMore = true)
            delay(120)

            try {
                val nextPage = state.page + 1
                // 다음 페이지 호출
                val newPage = searchBooksUseCase(state.query, page = nextPage, pageSize = PAGE_SIZE)
                Log.d("SearchVM", "loadMore page=$nextPage size=${newPage.size}")

                val merged = state.searchResults + newPage
                // 기존 “나눠 보여주기” UX 유지: 한 번에 10개씩만 더 보이게
                val nextVisible = min(merged.size, state.visibleCount + LOAD_COUNT)

                _uiState.value = state.copy(
                    searchResults = merged,
                    visibleCount = nextVisible,
                    page = nextPage,
                    // 새 페이지가 꽉 찼으면 더 있을 가능성
                    hasMore = newPage.size >= PAGE_SIZE,
                    isLoadingMore = false,
                    isPaging = false
                )
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isLoadingMore = false,
                    isPaging = false
                )
            }
        }
    }

    fun showMoreWithinFetched() {
        val state = _uiState.value
        val newVisible = min(state.searchResults.size, state.visibleCount + LOAD_COUNT)
        _uiState.value = state.copy(visibleCount = newVisible)
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
