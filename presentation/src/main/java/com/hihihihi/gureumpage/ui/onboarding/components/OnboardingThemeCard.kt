package com.hihihihi.gureumpage.ui.onboarding.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.components.GureumCard
import com.hihihihi.gureumpage.designsystem.components.GureumLinearProgressBar
import com.hihihihi.gureumpage.designsystem.components.Medi10Text
import com.hihihihi.gureumpage.designsystem.components.Semi12Text
import com.hihihihi.gureumpage.designsystem.theme.GureumColors
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme

@Composable
fun OnboardingThemeCard(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    isDarkTheme: Boolean,
) {
    var checked by remember { mutableStateOf(selected) }
    val colors = GureumTheme.colors

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable(enabled = checked) { checked = !checked }
            .border(
                width = 1.dp,
                color = if (checked) colors.primary else colors.gray200,
                shape = CardDefaults.shape
            ),
        colors = CardDefaults.cardColors(
            containerColor = colors.background10,
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (isDarkTheme)
                DarkThemeCard()
            else
                LightThemeCard()
        }
    }
}

@Composable
private fun LightThemeCard() {
    GureumPageTheme(darkTheme = false) {
        GureumCard {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Semi12Text("라이트 테마")
                Spacer(Modifier.height(2.dp))
                Medi10Text("라이트 테마입니다")
                Spacer(Modifier.height(10.dp))
                GureumLinearProgressBar(height = 6, progress = 0.7f)
            }
        }
        Spacer(Modifier.height(14.dp))
        Semi12Text("라이트 테마", color = GureumColors.defaultDarkColors().gray800)
        Spacer(Modifier.height(8.dp))
        Medi10Text(
            "밝고 깔끔한 인터페이스로\n편안한 독서 환경 제공",
            textAlign = TextAlign.Center,
            color = GureumColors.defaultDarkColors().gray500
        )
    }
}

@Composable
private fun DarkThemeCard() {
    GureumCard {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Semi12Text("다크 테마")
            Spacer(Modifier.height(2.dp))
            Medi10Text("다크 테마입니다")
            Spacer(Modifier.height(10.dp))
            GureumLinearProgressBar(height = 6, progress = 0.7f)
        }
    }
    Spacer(Modifier.height(14.dp))
    Semi12Text("다크 테마", color = GureumColors.defaultDarkColors().gray800)
    Spacer(Modifier.height(8.dp))
    Medi10Text(
        "눈의 피로를 줄이고 집중력을 높여주는\n어둠 속에서 빛나는 독서 경험",
        textAlign = TextAlign.Center,
        color = GureumColors.defaultDarkColors().gray500
    )
}
