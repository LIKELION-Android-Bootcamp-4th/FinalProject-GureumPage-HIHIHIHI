package com.hihihihi.gureumpage.ui.statistics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.components.GureumButton
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.ui.home.components.Picker
import com.hihihihi.gureumpage.ui.home.components.rememberPickerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsPicker(
    initialIndex: Int,
    items: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    val bottomSheetState = rememberModalBottomSheetState()
    val pickerState = rememberPickerState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        containerColor = GureumTheme.colors.card,
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(Modifier.width(72.dp))
                Picker(
                    state = pickerState,
                    items = items,
                    visibleItemsCount = 3,
                    modifier = Modifier.weight(1f),
                    textModifier = Modifier.padding(4.dp),
                    startIndex = initialIndex,
                )
                Spacer(Modifier.width(72.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
            GureumButton(text = "설정") {
                val idx = items.indexOf(pickerState.selectedItem).let { if (it >= 0) it else initialIndex }
                onConfirm(idx)
                onDismiss()
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview
@Composable
private fun GureumStatisticsPickerPreview() {
    GureumPageTheme {
        StatisticsPicker(0, listOf("1주", "1개월", "3개월", "6개월", "1년"), {}, {})
    }
}