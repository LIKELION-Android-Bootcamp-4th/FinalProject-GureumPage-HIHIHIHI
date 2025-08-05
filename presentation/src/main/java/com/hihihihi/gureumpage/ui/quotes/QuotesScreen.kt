package com.hihihihi.gureumpage.ui.quotes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hihihihi.domain.model.Quote
import com.hihihihi.gureumpage.designsystem.theme.GureumColors
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.ui.quotes.component.DetailBottomSheet
import com.hihihihi.gureumpage.ui.quotes.component.QuoteItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesScreen(
    userId: String = "iK4v1WW1ZX4gID2HueBi",
    viewModel: QuotesViewModel = hiltViewModel()
) {
    val quotes by viewModel.quotes.collectAsState()
    LaunchedEffect(userId) {
        viewModel.getQuotes(userId)
    }

    var sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var selectedQuote by remember { mutableStateOf<Quote?>(null) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(GureumColors.defaultLightColors().background10)
        ) {
            //임시 앱바
            CenterAlignedTopAppBar(
                modifier = Modifier.height(56.dp), title = {
                    Text(
                        text = "필사 목록",
                        style = MaterialTheme.typography.titleLarge,
                        color = GureumTheme.colors.gray800
                    )
                })

            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(quotes) { item ->
                    QuoteItem(
                        item = item, onItemClick = {
                            selectedQuote = item
                            scope.launch {
                                sheetState.show()
                            }
                        })
                }
            }
        }

        //모달 시트
        if (selectedQuote != null) {
            DetailBottomSheet(
                quote = selectedQuote!!, // 선택된 Quote 데이터를 전달
                sheetState = sheetState, // BottomSheet 상태 전달
                onDismiss = { // 닫기 이벤트가 발생했을 때 처리할 로직 전달
                    scope.launch {
                        sheetState.hide()
                        selectedQuote = null // 상태를 초기화하여 BottomSheet를 닫음
                    }
                }
            )
        }
    }
}
