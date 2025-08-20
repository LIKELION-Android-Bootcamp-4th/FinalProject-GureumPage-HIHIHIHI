package com.hihihihi.gureumpage.ui.mypage.component

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hihihihi.domain.model.GureumThemeType
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.hihihihi.gureumpage.common.utils.openAppOnPlayStore
import com.hihihihi.gureumpage.common.utils.openSupportEmail
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.ui.mypage.MypageViewModel

@Composable
fun MyPageMenuSection(viewModel: MypageViewModel = hiltViewModel(),onLogoutClick: () ->Unit ,onWithDrawClick: () -> Unit) {
    val theme by viewModel.theme.collectAsState()
    val colors = GureumTheme.colors
    val context = LocalContext.current

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        MyPageMenuSettingItem("평가하기") {
            openAppOnPlayStore(context)
        }
        MyPageMenuSettingItem("문의하기") {
            openSupportEmail(context)
        }
        MyPageMenuSettingItem("오픈소스 라이선스") {
            try {
                OssLicensesMenuActivity.setActivityTitle("오픈소스 라이선스")
                context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
            } catch (t: Throwable) {
                Toast.makeText(context, "라이선스 화면을 열 수 없습니다: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        }
        MyPageMenuSettingItem(
            title = "다크모드",
            showSwitch = true,
            switchChecked = (theme == GureumThemeType.DARK),
            onSwitchToggle = { viewModel.toggleTheme(it) }
        ) {

        }
        MyPageMenuSettingItem("로그아웃") {
            onLogoutClick()
        }
        MyPageMenuSettingItem(
            title = "탈퇴",
            textColor = colors.gray400
        ) {
            onWithDrawClick()
        }
    }
}