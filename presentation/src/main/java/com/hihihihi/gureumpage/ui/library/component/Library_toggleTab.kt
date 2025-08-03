package com.hihihihi.gureumpage.ui.library.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme

@Composable
fun ToggleTab(
    isBeforeReading: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val selectedColor = GureumTheme.colors.primary
    val unselectedColor = GureumTheme.colors.gray400
    val textColor = GureumTheme.colors.gray800

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = {onToggle(true)},
            colors = ButtonDefaults.buttonColors(
                containerColor = if(isBeforeReading) selectedColor else unselectedColor
            ),
            modifier = Modifier.weight(1f)
        ) {
            Text("읽기 전", color = textColor)
        }

        Button(
            onClick = { onToggle(false) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if(!isBeforeReading) selectedColor else unselectedColor
            ),
            modifier = Modifier.weight(1f)
        ) {
            Text("읽은 후", color = textColor)
        }
    }
}