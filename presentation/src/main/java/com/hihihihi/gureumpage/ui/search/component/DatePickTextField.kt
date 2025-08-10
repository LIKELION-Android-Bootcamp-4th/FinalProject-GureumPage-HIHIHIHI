package com.hihihihi.gureumpage.ui.search.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme

@Composable
fun DatePickTextField(value: String, placeholder: String, onClick: () -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        placeholder = { Text(text = placeholder) },
        shape = RoundedCornerShape(12.dp),
        readOnly = true,
        enabled = false,
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = GureumTheme.colors.gray800,
            disabledBorderColor = GureumTheme.colors.gray200,
            disabledPlaceholderColor = GureumTheme.colors.gray500,
            disabledTrailingIconColor = GureumTheme.colors.gray500
        ),
        trailingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_calendar_outline), // 캘린더 아이콘
                contentDescription = "날짜 선택"
            )
        }
    )
}
