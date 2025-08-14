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
    visibleItemsCount: Int = 3,
    unitLabel: String = "시간",
    hourValues: List<Int> = (0..23).toList(),
    minuteValues: List<Int> = (0..59).toList(),
    onValueChange: (Int, Int) -> Unit
) {
    val hourStrings = hourValues.map { it.toString().padStart(2,'0') }
    val minuteStrings = minuteValues.map { it.toString().padStart(2,'0') }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {

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
                items = hourStrings,
                visibleItemsCount = visibleItemsCount,
                startIndex = initialHour,
                modifier = Modifier
                    .weight(1f),
                textModifier = Modifier.padding(4.dp),
            )
            Text(
                unitLabel,
                style = GureumTypography.titleMedium,
                color = GureumTheme.colors.gray800,
            )
            Picker(
                state = minutePickerState,
                items = minuteStrings,
                visibleItemsCount = visibleItemsCount,
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