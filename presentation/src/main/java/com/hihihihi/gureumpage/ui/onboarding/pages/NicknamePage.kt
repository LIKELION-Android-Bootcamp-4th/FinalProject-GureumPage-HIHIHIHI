package com.hihihihi.gureumpage.ui.onboarding.pages

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.common.utils.validateNickname
import com.hihihihi.gureumpage.designsystem.components.GureumTextField
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.ui.onboarding.OnBoardingViewModel
import com.hihihihi.gureumpage.ui.onboarding.components.OnBoardingMainContents

@Composable
fun NicknamePage(viewModel: OnBoardingViewModel) {

    OnBoardingMainContents(
        gureumRes = R.drawable.ic_cloud_question,
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
                value = viewModel.nickname,
                onValueChange = {
                    viewModel.updateNickname(it)
                },
                hint = "닉네임을 입력해주세요",
                textAlign = TextAlign.Center,
                imeAction = ImeAction.Done,
                isError = !viewModel.nickname.validateNickname(),
                onSubmit = { viewModel.saveNickname() }
            ) {
                Text(
                    text = buildAnnotatedString {
                        pushStyle(
                            GureumTypography.titleSmall.toSpanStyle()
                                .copy(GureumTheme.colors.gray400)
                        )
                        append(viewModel.nickname.length.toString())
                        append("/8")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NicknamePagePreview() {
    GureumPageTheme {
//        NicknamePage(OnBoardingViewModel())
    }
}