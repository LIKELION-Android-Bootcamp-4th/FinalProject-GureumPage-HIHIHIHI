package com.hihihihi.gureumpage.designsystem.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

@Composable
fun GureumGenreChip(
    text: String,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    enabled: Boolean = false,
    onClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(30.dp),
        enabled = enabled,
        onClick = onClick,
    ) {
        Text(
            text = "# $text",
            style = GureumTypography.bodySmall,
            color = GureumTheme.colors.gray800,
            modifier = textModifier.padding(vertical = 8.dp, horizontal = 16.dp)
        )
    }
}


@Preview(name = "LightMode", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "DarkMode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GureumGenreChipPreview() {
    GureumPageTheme {
        GureumGenreChip("로맨스")
    }
}