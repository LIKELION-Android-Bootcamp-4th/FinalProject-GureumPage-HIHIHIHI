package com.hihihihi.gureumpage.designsystem.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

@Composable
fun GureumTextField(
    modifier: Modifier = Modifier,
    value: String,
    hint: String = "",
    maxLines: Int = 1,
    roundedCorner: Dp = 12.75.dp,
    textAlign: TextAlign = TextAlign.Start,
    textStyle: TextStyle = GureumTypography.bodyMedium,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit,
) {
    val colors = GureumTheme.colors
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = hint,
                style = GureumTypography.bodySmall,
                textAlign = textAlign,
            )
        },
        value = value,
        onValueChange = onValueChange,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = colors.textFieldOutline,
            unfocusedContainerColor = colors.searchTextField,
            unfocusedTextColor = colors.gray700,
            unfocusedPlaceholderColor = colors.gray400,

            focusedBorderColor = colors.textFieldOutline,
            focusedContainerColor = colors.searchTextField,
            focusedTextColor = colors.gray800,
            focusedPlaceholderColor = colors.gray400,
        ),
        trailingIcon = trailingIcon,
        leadingIcon = leadingIcon,
        maxLines = maxLines,
        shape = RoundedCornerShape(roundedCorner),
        textStyle = textStyle.copy(textAlign = textAlign),
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GureumTextFieldPreview() {
    GureumPageTheme {
        GureumTextField(
            hint = "힌트 텍스트",
            value = "",
            onValueChange = {}
        )
    }
}