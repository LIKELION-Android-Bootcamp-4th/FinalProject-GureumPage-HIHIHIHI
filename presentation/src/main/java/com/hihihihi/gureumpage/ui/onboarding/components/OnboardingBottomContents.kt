package com.hihihihi.gureumpage.ui.onboarding.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.components.GureumButton
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

@Composable
fun OnboardingBottom(
    buttonText: String,
    explanation: String = ""
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
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            // TODO 페이지 넘기기 이벤트
        }
    }
}