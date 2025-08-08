package com.hihihihi.gureumpage.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography



@Composable
fun GureumNumberPicker(
    initialHour: Int = 0,
    initialMinute: Int = 0,
    onValueChange: (Int, Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        val hourValues = remember { (0..23).map { it.toString().padStart(2, '0') } }
        val minuteValues = remember { (0..59).map { it.toString().padStart(2, '0') } }

        val hourPickerState = rememberPickerState()
        val minutePickerState = rememberPickerState()

        LaunchedEffect(hourPickerState.selectedItem, minutePickerState.selectedItem) {
            val hour = hourPickerState.selectedItem.toIntOrNull() ?: 0
            val minute = minutePickerState.selectedItem.toIntOrNull() ?: 0
            onValueChange(hour, minute)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Picker(
                state = hourPickerState,
                items = hourValues,
                visibleItemsCount = 3,
                startIndex = initialHour,
                modifier = Modifier
                    .weight(1f),
                textModifier = Modifier.padding(4.dp),
            )
            Text(
                "시간",
                style = GureumTypography.titleMedium,
                color = GureumTheme.colors.gray800,
            )
            Picker(
                state = minutePickerState,
                items = minuteValues,
                visibleItemsCount = 3,
                startIndex = initialMinute,
                modifier = Modifier
                    .weight(1f),
                textModifier = Modifier.padding(4.dp),
            )
            Text(
                "분",
                style = GureumTypography.titleMedium,
                color = GureumTheme.colors.gray800
            )
        }
    }
}