package com.hihihihi.gureumpage.ui.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.components.BodySubText
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

@Composable
fun SearchTopAppBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    onSearch: (String) -> Unit,
    focusRequester: FocusRequester
) {
    Column(
        modifier = Modifier.background(GureumTheme.colors.card)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //뒤로가기 버튼
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_left),
                    contentDescription = "뒤로가기",
                    tint = GureumTheme.colors.gray800
                )
            }
            //검색어 입력 필드
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    textStyle = TextStyle(color = GureumTheme.colors.gray800),
                    singleLine = true,
                    cursorBrush = SolidColor(GureumTheme.colors.gray400),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            onSearch(query)
                        }
                    )
                ) { innerTextField ->
                    //힌트
                    if (query.isEmpty()) {
                        BodySubText("책 검색", style = GureumTypography.bodyLarge)
                    }
                    innerTextField()
                }
            }
            IconButton(onClick = onCloseClick) {
                Icon(
                    painter = painterResource(
                        if (GureumTheme.isDarkTheme) R.drawable.ic_text_clear_dark
                        else R.drawable.ic_text_clear_light
                    ),
                    contentDescription = "검색 취소",
                    tint = Color.Unspecified
                )
            }
        }
    }
}
