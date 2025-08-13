package com.hihihihi.gureumpage.designsystem.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

@Composable
fun GureumTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String = "",
    minLines: Int = 1,
    maxLines: Int = 1,
    roundedCorner: Dp = 12.75.dp,
    isError: Boolean = false,
    textAlign: TextAlign = TextAlign.Start,
    textStyle: TextStyle = GureumTypography.bodyMedium,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onSubmit: () -> Unit = {},
    supportingText: @Composable (() -> Unit)? = null,
) {
    val colors = GureumTheme.colors
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var hasInteracted by remember { mutableStateOf(false) } // 텍스트 필드와 상호작용 여부

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
        onValueChange = {
            if (!hasInteracted) hasInteracted = true
            onValueChange(it)
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = colors.textFieldOutline,
            unfocusedContainerColor = colors.searchTextField,
            unfocusedTextColor = colors.gray700,
            unfocusedPlaceholderColor = colors.gray400,

            focusedBorderColor = colors.textFieldOutline,
            focusedContainerColor = colors.searchTextField,
            focusedTextColor = colors.gray800,
            focusedPlaceholderColor = colors.gray400,

            errorBorderColor = colors.systemRed,
            errorContainerColor = colors.systemRed.copy(alpha = 0.05f),
            errorTextColor = colors.gray800,
            errorSupportingTextColor = colors.systemRed/*.copy(alpha = 0.5f)*/
        ),
        trailingIcon = trailingIcon,
        leadingIcon = leadingIcon,
        minLines = minLines,
        maxLines = maxLines,
        shape = RoundedCornerShape(roundedCorner),
        textStyle = textStyle.copy(textAlign = textAlign),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onNext = {
                hasInteracted = true
                focusManager.moveFocus(FocusDirection.Next)
            },
            onDone = {
                hasInteracted = true
                keyboardController?.hide()
                focusManager.clearFocus()
                onSubmit()
            }
        ),
        supportingText = supportingText,
        isError = isError && hasInteracted,
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