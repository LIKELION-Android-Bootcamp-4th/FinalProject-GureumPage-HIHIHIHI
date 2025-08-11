package com.hihihihi.gureumpage.ui.mypage.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hihihihi.gureumpage.common.utils.openAppOnPlayStore
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.ui.mypage.MypageViewModel

@Composable
fun MyPageMenuSection(viewModel: MypageViewModel = hiltViewModel()) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState() // 다크 모드 상태 관찰
    val colors = GureumTheme.colors
    val typography = GureumTypography
    val context = LocalContext.current

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        MyPageMenuSettingItem("평가하기") {
            openAppOnPlayStore(context)
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
            switchChecked = isDarkTheme,
            onSwitchToggle = { viewModel.toggleTheme(it)}
        ) {
            //TODO: 기능 추가
        }
        MyPageMenuSettingItem("로그아웃") {
            //TODO: 기능 추가
        }
        MyPageMenuSettingItem(
            title = "탈퇴",
            textColor = colors.gray400
        ) {
            //TODO: 기능 추가
        }
    }
}