package com.hihihihi.gureumpage.ui.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.components.GureumButton
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingGoalPicker(
    initialHour: Int = 0,
    initialMinute: Int = 0,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState()

    var selectedHour by remember { mutableStateOf(initialHour) }
    var selectedMinute by remember { mutableStateOf(initialMinute) }

    LaunchedEffect(initialHour, initialMinute) {
        selectedHour = initialHour
        selectedMinute = initialMinute
    }

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
            GureumNumberPicker(
                initialHour = selectedHour,
                initialMinute = selectedMinute,
                onValueChange = { hour, minute ->
                    selectedHour = hour
                    selectedMinute = minute
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
            GureumButton(text = "설정") {
                onConfirm(selectedHour, selectedMinute)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}