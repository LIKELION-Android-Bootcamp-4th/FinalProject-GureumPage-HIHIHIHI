package com.hihihihi.gureumpage.designsystem.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

/**
 * 조건에 따라 활성 시킬 때 사용
 */
@Composable
fun GureumButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 44.dp, max = 48.dp),
        shape = RoundedCornerShape(12.75.dp),
        enabled = enabled,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = GureumTheme.colors.gray200,
            disabledContentColor = GureumTheme.colors.gray400,
        )
    ) {
        Text(text = text, style = GureumTypography.bodyMedium)
    }
}

/**
 * 활성 버튼 옆 취소 버튼을 둘 때 사용
 */
@Composable
fun GureumCancelButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 44.dp, max = 48.dp),
        shape = RoundedCornerShape(12.75.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = GureumTheme.colors.gray200,
            contentColor = GureumTheme.colors.gray800,
        )
    ) {
        Text(text = text, style = GureumTypography.bodyMedium)
    }
}


@Preview(name = "DarkMode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "LightMode", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun GureumButtonPreview() {
    GureumPageTheme {
        Column {
            GureumButton(text = "활성 버튼") {}
            GureumButton(text = "비활성 버튼", enabled = false) {}
        }
    }
}

@Preview(name = "DarkMode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "LightMode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun GureumCancelButtonPreview() {
    GureumPageTheme {
        Row(
            modifier = Modifier.padding(20.dp)
        ) {
            GureumCancelButton(text = "그만 읽기", modifier = Modifier.fillMaxWidth(0.5f)) {}
            Spacer(modifier = Modifier.width(14.dp))
            GureumButton(text = "계속 읽기") {}
        }
    }
}