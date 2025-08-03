package com.hihihihi.gureumpage.designsystem.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme

/**
 * 스크린 목차 타이틀, 책 상세 책 제목, 마이페이지 버튼 리스트 등
 */
@Composable
fun TitleText(
    text: String,
    style: TextStyle = MaterialTheme.typography.titleLarge,
    maxLine: Int = Int.MAX_VALUE,
    isUnderline: Boolean = false,
) {
    val underlineColor: Color = GureumTheme.colors.primary50
    val underlineModifier = if (isUnderline) {
        Modifier.drawBehind {
            val underlineHeight = 9.dp.toPx()
            drawRect(
                color = underlineColor,
                topLeft = Offset(0f, size.height - underlineHeight),
                size = androidx.compose.ui.geometry.Size(width = size.width, underlineHeight),
            )
        }
    } else {
        Modifier
    }

    Text(
        text = text,
        style = style,
        maxLines = maxLine,
        modifier = underlineModifier,
    )
}

@Preview(name = "DarkMode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "LightMode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun GureumTextPreview() {
    GureumPageTheme {
        Column {
            TitleText("필사한 문장", isUnderline = true)
            TitleText("필사한 문장")
        }
    }
}