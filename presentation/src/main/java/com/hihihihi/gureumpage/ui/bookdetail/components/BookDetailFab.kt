package com.hihihihi.gureumpage.ui.bookdetail.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme

@Composable
fun BookDetailFab(
    modifier: Modifier = Modifier,
//    actions: 액션 버튼 이미지와 설명 받기
    onActionClick: (index: Int) -> Unit
) {
    var fabExpanded by remember { mutableStateOf(false) }

    FloatingActionButton(
        modifier = modifier,
        onClick = {},
        shape = FloatingActionButtonDefaults.largeShape,
        elevation = FloatingActionButtonDefaults.elevation(4.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_plus),
            contentDescription = "fab plus",
            tint = GureumTheme.colors.white,
            modifier = Modifier.size(48.dp)
        )
    }
}

@Preview
@Composable
private fun BookDetailFabPreview() {
    GureumPageTheme {
        BookDetailFab(onActionClick = { })
    }
}