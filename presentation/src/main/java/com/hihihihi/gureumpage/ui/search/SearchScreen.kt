package com.hihihihi.gureumpage.ui.search

import android.app.Activity
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.luminance
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.hihihihi.domain.model.SearchBook
import com.hihihihi.gureumpage.designsystem.components.GureumCard
import com.hihihihi.gureumpage.designsystem.components.Medi14Text
import com.hihihihi.gureumpage.designsystem.components.Medi16Text
import com.hihihihi.gureumpage.designsystem.components.Semi14Text
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.ui.search.component.AddBookBottomSheet
import com.hihihihi.gureumpage.ui.search.component.SearchItem
import com.hihihihi.gureumpage.ui.search.component.SearchTopAppBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    //검색어 입력을 위한 상태
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    //포커스와 키보드 제어
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    //검색이 한번이라도 실행되었는지 여부
    var hasSearched by remember { mutableStateOf(false) }
    //검색 결과 상태
    var bookToAdd by remember { mutableStateOf<SearchBook?>(null) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    //모달시트 상태관리
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    LaunchedEffect(uiState.addBookMessage) {
        uiState.addBookMessage?.let { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                viewModel.clearMessage()
            }
        }
    }

    LaunchedEffect(uiState.isAddBookSuccess) {
        if (uiState.isAddBookSuccess && !uiState.isAddingBook) {
            scope.launch {
                sheetState.hide()
                bookToAdd = null
            }
        }
    }


    val statusBarColor = GureumTheme.colors.card.toArgb()
    val lightIcons = statusBarColor.luminance > 0.5f
    val window = (LocalView.current.context as Activity).window
    DisposableEffect(statusBarColor, lightIcons) {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = statusBarColor
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = lightIcons
            isAppearanceLightNavigationBars = lightIcons
        }
        onDispose { }
    }

    var endToastShown by remember { mutableStateOf(false) }
    LaunchedEffect(uiState.query) {
        endToastShown = false
    }

    LaunchedEffect(uiState.hasMore, uiState.searchResults.size) {
        if (!uiState.hasMore &&
            uiState.searchResults.isNotEmpty() &&
            !endToastShown &&
            !uiState.isLoadingMore
        ) {

            Toast.makeText(context, "모든 검색 결과를 불러왔습니다.", Toast.LENGTH_SHORT).show()
            endToastShown = true
        }
    }

    //화면 진입 시 검색 자동 포커스
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.collect { lastVisibleIndex ->
            // 여기서는 스크롤 위치만 감지하고, 실제 로딩 조건은 별도로 체크
            if (lastVisibleIndex != null) {
                val currentState = viewModel.uiState.value // viewModel에서 직접 가져오기
                val totalItems = currentState.searchResults.size

                if (totalItems > 0) {
                    val reachedBottom = lastVisibleIndex >= totalItems - 2

                    if (reachedBottom && currentState.hasMore && !currentState.isLoadingMore) {
                        viewModel.loadMore()
                    }
                }
            }
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val totalItems = layoutInfo.totalItemsCount

            lastVisibleIndex != null && lastVisibleIndex >= totalItems - 3
        }.collect { shouldLoadMore ->
            if (shouldLoadMore) {
                val currentState = viewModel.uiState.value

                val canLoad = currentState.hasMore &&
                        !currentState.isLoadingMore &&
                        !currentState.isSearching &&
                        currentState.searchResults.isNotEmpty()

                if (canLoad) {
                    viewModel.loadMore()
                }
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        SearchTopAppBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onBackClick = { navController.popBackStack() },
            onCloseClick = {
                searchQuery = ""
                hasSearched = false
                focusManager.clearFocus()
                keyboardController?.hide()
            },
            focusRequester = focusRequester,
            onSearch = { currentQuery ->
                //검색 버튼이 눌렸을 때
                //키보드 내리고 포커스 해제
                keyboardController?.hide()
                focusManager.clearFocus()
                endToastShown = false
                //검색 로직
                viewModel.search(currentQuery)
                hasSearched = true
            }
        )
        //검색이 되지 않았을 경우 보여주는 안내 문구
        when {
            !hasSearched -> {
                Spacer(Modifier.height(74.dp))
                Medi16Text(
                    text = "책 제목, 작가, 출판사 등\n무엇으로든 검색해 보세요",
                    color = GureumTheme.colors.gray400,
                    textAlign = TextAlign.Center
                )
            }

            uiState.isSearching -> {
                Spacer(Modifier.height(74.dp))
                CircularProgressIndicator(
                    color = GureumTheme.colors.primary
                )
                Spacer(Modifier.height(32.dp))
                Medi16Text(
                    text = "검색 중입니다...",
                    color = GureumTheme.colors.gray400,
                    textAlign = TextAlign.Center
                )
            }

            uiState.searchResults.isEmpty() -> {
                Spacer(Modifier.height(74.dp))
                Medi16Text(
                    text = "검색 결과가 없습니다.",
                    color = GureumTheme.colors.gray400,
                    textAlign = TextAlign.Center
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    state = listState
                ) {
                    itemsIndexed(
                        items = uiState.searchResults,
                        key = { index, item -> "${item.isbn}-$index" }
                    ) { _, item ->
                        SearchItem(
                            result = item,
                            onItemClick = { selectedBook ->
                                bookToAdd = selectedBook
                                scope.launch { sheetState.show() }
                            }
                        )
                    }

                    if (uiState.isLoadingMore) {
                        item(key = "footer") {
                            if (uiState.isLoadingMore) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(color = GureumTheme.colors.primary)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Medi14Text(
                                        text = "더 많은 결과를 불러오는 중...",
                                        color = GureumTheme.colors.gray400,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }

                    if (!uiState.hasMore && uiState.searchResults.isNotEmpty() && !uiState.isLoadingMore) {
                        item(key = "end_notice") {
                            GureumCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Semi14Text(
                                        text = "총 ${uiState.searchResults.size}개의 검색 결과",
                                        color = GureumTheme.colors.gray500
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Medi14Text(
                                        text = "더 정확한 검색을 위해 구체적인 키워드를 사용해보세요",
                                        color = GureumTheme.colors.gray400,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        //bookToAdd의 상태에 따라 모달시트를 보여주거나 숨김
        if (bookToAdd != null) {
            AddBookBottomSheet(
                book = bookToAdd!!,
                sheetState = sheetState,
                isLoading = uiState.isAddingBook,
                onDismiss = {
                    scope.launch {
                        sheetState.hide()
                        bookToAdd = null
                    }
                },
                onConfirm = { book ->
                    scope.launch {
                        viewModel.addUserBook(
                            book.searchBook,
                            book.startDate,
                            book.endDate,
                            book.currentPage,
                            book.totalPage,
                            book.status
                        )
                    }
                },
                onGetBookPageCount = { isbn, onResult ->
                    viewModel.getBookPageCount(isbn, onResult)
                }
            )
        }
    }
}


@Preview(name = "DarkMode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "LightMode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun SearchPreView() {
    GureumPageTheme {
//        SearchScreen(
//            navController = rememberNavController(),
//        )
    }
}
