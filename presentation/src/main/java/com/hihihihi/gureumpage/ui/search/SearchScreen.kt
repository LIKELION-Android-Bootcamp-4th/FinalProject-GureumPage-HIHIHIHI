package com.hihihihi.gureumpage.ui.search

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.hihihihi.domain.model.SearchBook
import com.hihihihi.gureumpage.designsystem.components.Medi16Text
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



    val statusBarColor = GureumTheme.colors.card
    val useDarkIcons = !isSystemInDarkTheme()
    val view = LocalView.current
    val window = (view.context as Activity).window
    SideEffect {
        window.statusBarColor = statusBarColor.toArgb()
        WindowCompat.getInsetsController(window, view)
            .isAppearanceLightStatusBars = useDarkIcons
    }

    //화면 진입 시 검색 자동 포커스
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
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
                //검색 로직
                viewModel.search(currentQuery)
                hasSearched = true
            }
        )
        //검색이 되지 않았을 경우 보여주는 안내 문구
        when{
            !hasSearched -> {
                Spacer(Modifier.height(74.dp))
                Medi16Text(
                    text = "책 제목, 작가, 출판사 등\n무엇으로든 검색해 보세요",
                    color = GureumTheme.colors.gray400,
                    textAlign = TextAlign.Center
                )
            }
            uiState.isSearching ->{
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
                ) {
                    items(uiState.searchResults) { item ->
                        SearchItem(
                            result = item,
                            onItemClick = { selectedBook ->
                                bookToAdd = selectedBook
                                scope.launch { sheetState.show() }
                            },
                        )
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
