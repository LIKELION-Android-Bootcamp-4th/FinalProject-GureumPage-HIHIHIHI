package com.hihihihi.presentation.ui.onboarding.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hihihihi.presentation.designsystem.components.GureumButton
import com.hihihihi.presentation.designsystem.theme.GureumTheme
import com.hihihihi.presentation.designsystem.theme.GureumTypography

@Composable
fun OnboardingBottomContents(
    buttonText: String,
    explanation: String = "",
    isNextEnabled: Boolean = true,
    onNext: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = explanation,
            style = GureumTypography.bodyMedium,
            color = GureumTheme.colors.gray500,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        GureumButton(
            text = buttonText,
            enabled = isNextEnabled,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            onClick = onNext
        )
    }
}
