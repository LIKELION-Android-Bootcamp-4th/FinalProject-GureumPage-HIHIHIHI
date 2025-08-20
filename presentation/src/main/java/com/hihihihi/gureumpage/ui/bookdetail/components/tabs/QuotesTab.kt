package com.hihihihi.gureumpage.ui.bookdetail.components.tabs

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hihihihi.domain.model.Quote
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.components.ExpandableText
import com.hihihihi.gureumpage.designsystem.components.GureumCard
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.ui.bookdetail.mock.dummyQuotes
import kotlin.math.exp

@Composable
fun QuotesTab(
    quotes: List<Quote>,
    onEdit: (quoteId: String) -> Unit,
    onDelete: (quoteId: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 20.dp)
    ) {
        quotes.forEach { quote ->
            val expanded = remember(quote.id) { mutableStateOf(false) }
            QuoteCard(
                id = quote.id,
                date = quote.createdAt?.toLocalDate().toString(), //TODO !!처리..
                page = quote.pageNumber,
                quote = quote.content,
                expanded = expanded,
                onEdit = onEdit,
                onDelete = onDelete,

            )
        }
    }
}

@Composable
private fun QuoteCard(
    id: String,
    date: String,
    page: Int?,
    quote: String,
    expanded: MutableState<Boolean>,
    onEdit: (quoteId: String) -> Unit,
    onDelete: (quoteId: String) -> Unit,
) {
    GureumCard(
        modifier = Modifier.padding(top = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, bottom = 14.dp, top = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = date,
                    style = GureumTypography.bodySmall,
                    color = GureumTheme.colors.gray400,
                )
                Spacer(Modifier.width(12.dp))

                page?.let {
                    Text(
                        text = "${it}P",
                        style = GureumTypography.bodySmall,
                        color = GureumTheme.colors.gray400,
                    )
                }

                Spacer(Modifier.weight(1f))

                Box(modifier = Modifier.wrapContentSize()) {
                    IconButton(
                        onClick = { expanded.value = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_horizontal_ellipsis_outline),
                            contentDescription = "dot_menu",
                            tint = GureumTheme.colors.gray600
                        )
                    }
                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("수정") },
                            onClick = { expanded.value = false; onEdit(id)}
                        )
                        DropdownMenuItem(
                            text = { Text("삭제") },
                            onClick = {expanded.value = false; onDelete(id)}
                        )
                    }
                }
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp),
                text = quote,
                style = GureumTypography.bodyMedium,
            )
        }
    }
}


@Preview(name = "Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun QuotesTabPreview() {
    GureumPageTheme {
//        QuotesTab(
//            quotes = dummyQuotes,
//            onMenuClick = { id -> println("Menu clicked for Quote id: $id") }
//        )
    }
}
