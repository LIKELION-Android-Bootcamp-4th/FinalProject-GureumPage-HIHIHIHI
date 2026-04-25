package com.hihihihi.presentation.ui.quotes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hihihihi.presentation.designsystem.components.Medi16Text
import com.hihihihi.presentation.designsystem.components.Semi18Text
import com.hihihihi.presentation.designsystem.theme.GureumTheme
import com.hihihihi.presentation.ui.home.components.ErrorView
import com.hihihihi.presentation.ui.quotes.component.QuoteContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesScreen(
    viewModel: QuotesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    //모달 관련
    var sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    when {
        uiState.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GureumTheme.colors.primary)
            }
        }

        uiState.errorMessage != null -> {
            ErrorView(message = "필사 데이터를 가져오는데 실패했어요") // 에러 발생 시 표시될 뷰
        }

        !uiState.isLoading && uiState.quotes.isEmpty() -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Semi18Text(
                    "아직 필사가 없어요",
                    color = GureumTheme.colors.gray500,
                )
                Spacer(Modifier.height(16.dp))
                Medi16Text(
                    "책에서 인상 깊은 한 줄을 남겨 보세요.",
                    color = GureumTheme.colors.gray400,
                )
            }
        }

        else -> {
            QuoteContent(
                quotes = uiState.quotes,
                selectedQuote = uiState.selectedQuote,
                sheetState = sheetState,
                scope = scope,
                onQuoteSelected = { quote -> viewModel.selectQuote(quote) },
                onDismiss = { viewModel.selectQuote(null) },
            )
        }
    }
}
