package com.hihihihi.gureumpage.ui.quotes.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hihihihi.domain.model.Quote
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteContent(
    quotes: List<Quote>,
    selectedQuote: Quote?,
    scope: CoroutineScope,
    onQuoteSelected: (Quote) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(GureumTheme.colors.background)
        ) {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(quotes) { item ->
                    QuoteItem(
                        item = item, onItemClick = {
                            onQuoteSelected(item)
                            scope.launch {
                                sheetState.show()
                            }
                        }
                    )
                }
            }
        }

        //모달 시트
        if (selectedQuote != null) {
            DetailBottomSheet(
                quote = selectedQuote,
                sheetState = sheetState,
                onDismiss = {
                    scope.launch {
                        sheetState.hide()
                        onDismiss()
                    }
                }
            )
        }
    }
} 
