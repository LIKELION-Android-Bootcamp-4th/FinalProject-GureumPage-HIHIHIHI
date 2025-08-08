package com.hihihihi.gureumpage.ui.onboarding.pages

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.common.math.LinearTransformation.horizontal
import com.hihihihi.gureumpage.designsystem.components.GureumTextField
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.ui.onboarding.components.OnBoardingMainContents
import com.hihihihi.gureumpage.ui.onboarding.components.OnboardingBottom
import com.hihihihi.gureumpage.ui.onboarding.components.OnboardingScaffold
import com.hihihihi.gureumpage.ui.onboarding.components.OnboardingTopContents

@Composable
fun NicknamePage(navController: NavHostController) {
    var nickname by remember { mutableStateOf("") }

    OnboardingScaffold(
        topContent = {
            OnboardingTopContents(
                navController = navController,
                progress = 0.2f
            )
        },
        mainContent = {
            OnBoardingMainContents(
                titleText = "어떻게 불러드릴까요?",
                subTitleText = "앱에서 사용할 닉네임을 설정해주세요"
            ) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "2~8자 이내로 입력해주세요",
                    style = GureumTypography.bodyMedium,
                    color = GureumTheme.colors.gray400
                )
                Spacer(Modifier.height(24.dp))

                Column(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    GureumTextField(
                        value = nickname,
                        onValueChange = { nickname = it },
                        hint = "닉네임을 입력해주세요",
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        text = buildAnnotatedString {
                            pushStyle(
                                GureumTypography.titleSmall.toSpanStyle()
                                    .copy(GureumTheme.colors.gray400)
                            )
                            append(nickname.length.toString())
                            append("/8")
                        },
                        modifier = Modifier.padding(end = 6.dp),
                    )
                }
            }
        },
        bottomContent = {
            OnboardingBottom("다음 단계")
        }
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NicknamePagePreview() {
    GureumPageTheme {
        NicknamePage(rememberNavController())
    }
}