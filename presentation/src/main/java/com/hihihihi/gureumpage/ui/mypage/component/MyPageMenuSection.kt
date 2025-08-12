package com.hihihihi.gureumpage.ui.mypage.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hihihihi.domain.model.GureumThemeType
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.ui.mypage.MypageViewModel

@Composable
fun MyPageMenuSection(viewModel: MypageViewModel = hiltViewModel()) {
    val theme by viewModel.theme.collectAsState()

    val colors = GureumTheme.colors
    val typography = GureumTypography

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        MyPageMenuSettingItem("평가하기") {
            //TODO: 기능 추가
        }
        MyPageMenuSettingItem("문의하기") {
            //TODO: 기능 추가
        }
        MyPageMenuSettingItem("오픈소스 라이선스") {
            //TODO: 기능 추가
        }
        MyPageMenuSettingItem(
            title = "다크모드",
            showSwitch = true,
            switchChecked = (theme == GureumThemeType.DARK),
            onSwitchToggle = { viewModel.toggleTheme(it)}
        ) {
            //TODO: 기능 추가
        }
        MyPageMenuSettingItem("로그아웃") {
            //TODO: 기능 추가
        }
        MyPageMenuSettingItem(
            title = "탙퇴",
            textColor = colors.gray400
        ) {
            //TODO: 기능 추가
        }
    }
}