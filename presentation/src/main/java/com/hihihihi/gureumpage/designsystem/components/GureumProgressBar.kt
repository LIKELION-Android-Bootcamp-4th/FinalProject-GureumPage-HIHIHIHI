package com.hihihihi.gureumpage.designsystem.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme

@Composable
fun GureumProgressBar(
    height: Int,
    progress: Float,
) {
    LinearProgressIndicator(
        progress = {
            progress
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(height.dp), // 필요에 따라 높이 조절
        color = GureumTheme.colors.primary, // 진행된 부분 색상
        trackColor = GureumTheme.colors.primary50, // 배경 (트랙) 색상
        gapSize = (-15).dp, // 사이 갭 없애기
        drawStopIndicator = {} // 끝에 점 없애기
    )
}

@Preview(name = "LightMode", showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "DarkMode", showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GureumProgressBar6Preview() {
    val currentPage = 121
    val totalPage = 270
    GureumPageTheme {
        GureumProgressBar(height = 6, progress = currentPage.toFloat()/totalPage)
    }
}


@Preview(name = "LightMode", showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "DarkMode", showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GureumProgressBar12Preview() {
    val currentPage = 121
    val totalPage = 270
    GureumPageTheme {
        GureumProgressBar(height = 12, progress = currentPage.toFloat()/totalPage)
    }
}

