package com.hihihihi.gureumpage.ui.search

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.ui.search.component.SearchTopAppBar

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    //검색어 입력을 위한 상태
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    //포커스와 키보드 제어
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    //검색이 한번이라도 실행되었는지 여부
    var hasSearched by remember { mutableStateOf(false) }
    //화면 진입 시 검색 자동 포커스
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(topBar = {
        SearchTopAppBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onBackClick = { navController.popBackStack() },
            onCloseClick = {
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
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
        ) {
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
            }
        }
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
