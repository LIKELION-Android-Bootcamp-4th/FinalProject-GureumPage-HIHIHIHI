package com.hihihihi.gureumpage.ui.quotes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import com.hihihihi.gureumpage.designsystem.components.Medi16Text
import com.hihihihi.gureumpage.designsystem.components.Semi18Text
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.ui.quotes.component.QuoteContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesScreen(
    viewModel: QuotesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    //모달 관련
    var sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var selectedQuote by remember { mutableStateOf<Quote?>(null) }

    when {
        uiState.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GureumTheme.colors.primary)
            }
        }

        uiState.errorMessage != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "에러 발생", color = GureumTheme.colors.systemRed)
            }
        }

        !uiState.isLoading && uiState.quotes.isEmpty() -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Semi18Text(
                    "아직 필사가 없어요",
                    color = GureumTheme.colors.gray500
                )
                Spacer(Modifier.height(16.dp))
                Medi16Text(
                    "책에서 인상 깊은 한 줄을 남겨 보세요.",
                    color = GureumTheme.colors.gray400
                )
            }
        }

        else -> {
            QuoteContent(
                quotes = uiState.quotes,
                selectedQuote = selectedQuote,
                sheetState = sheetState,
                scope = scope,
                onQuoteSelected = { quote ->
                    selectedQuote = quote
                },
                onDismiss = {
                    selectedQuote = null
                }
            )
        }
    }
}
