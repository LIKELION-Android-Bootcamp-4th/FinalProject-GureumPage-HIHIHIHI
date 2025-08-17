package com.hihihihi.gureumpage.ui.search

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.hihihihi.domain.model.SearchBook
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.ui.search.component.AddBookBottomSheet
import com.hihihihi.gureumpage.ui.search.component.SearchItem
import com.hihihihi.gureumpage.ui.search.component.SearchTopAppBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController, viewModel: SearchViewModel = hiltViewModel()
) {
    //검색어 입력을 위한 상태
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    //포커스와 키보드 제어
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    //검색이 한번이라도 실행되었는지 여부
    var hasSearched by remember { mutableStateOf(false) }
    //검색 결과 상태
    val searchResults by viewModel.searchResults.collectAsState()
    var bookToAdd by remember { mutableStateOf<SearchBook?>(null) }

    //모달시트 상태관리
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()


    //화면 진입 시 검색 자동 포커스
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            //컴포넌트
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
                    //검색 로직
                    viewModel.search(currentQuery)
                    hasSearched = true
                })
        }, modifier = Modifier.background(GureumTheme.colors.background)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
        ) {
            //검색이 되지 않았을 경우 보여주는 안내 문구
            if (!hasSearched) {
                Text(
                    text = "책 제목, 작가, 출판사 등\n무엇으로든 검색해 보세요",
                    color = GureumTheme.colors.gray400,
                    style = GureumTypography.bodyLarge,
                    maxLines = 4,
                    modifier = Modifier
                        .padding(top = 74.dp)
                        .align(Alignment.Center),
                    overflow = TextOverflow.Ellipsis
                )
            } else {//검색이 되었을 때 보여주는 뷰
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(searchResults) { item ->
                        SearchItem(result = item, onItemClick = {}, onAddClick = { selectedBook ->
                            bookToAdd = selectedBook
                            scope.launch { sheetState.show() }
                        })
                    }
                }
                if (searchResults.isEmpty()) {
                    Text(
                        text = "검색 결과가 없습니다.",
                        color = GureumTheme.colors.gray400,
                        style = GureumTypography.bodyLarge,
                        maxLines = 4,
                        modifier = Modifier
                            .padding(top = 74.dp)
                            .align(Alignment.Center),
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
    //bookToAdd의 상태에 따라 모달시트를 보여주거나 숨김
    if (bookToAdd != null) {
        AddBookBottomSheet(
            book = bookToAdd!!,
            sheetState = sheetState,
            onDismiss = {
                scope.launch {
                    sheetState.hide()
                    bookToAdd = null
                }
            },
            onConfirm = { searchBook, startDate, endDate, currentPage, totalPage, readingState ->
                scope.launch {
                    sheetState.hide()
                    bookToAdd = null
                    viewModel.addUserBook(searchBook, startDate, endDate, currentPage, totalPage, readingState)
                }
            },
            onGetBookPageCount = { isbn, onResult ->
                viewModel.getBookPageCount(isbn, onResult)
            }
        )
    }
}


@Preview(name = "DarkMode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "LightMode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun SearchPreView() {
    GureumPageTheme {
        SearchScreen(
            navController = rememberNavController()
        )
    }
}
