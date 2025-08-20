package com.hihihihi.gureumpage.designsystem.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme

@Composable
fun GureumCard(
    modifier: Modifier = Modifier,
    elevation: Dp = 4.dp,
    corner: Dp = 16.dp,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(corner),
                ambientColor = Color.Black.copy(alpha = 0f),
                spotColor = Color.Black.copy(alpha = 0.5f),
            ),
        shape = RoundedCornerShape(corner),
        content = content
    )
}

@Composable
fun GureumCard(
    modifier: Modifier = Modifier,
    elevation: Dp = 4.dp,
    corner: Dp = 20.dp,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Surface(
        enabled = enabled,
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(corner),
                ambientColor = Color.Black.copy(alpha = 0f),
                spotColor = Color.Black.copy(alpha = 0.5f),
            ),
        shape = RoundedCornerShape(corner),
        content = content
    )
}

@Preview(name = "LightMode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "DarkMode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GureumCardPreview() {
    GureumPageTheme {
        GureumCard {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text("예시")
            }
        }
    }
}