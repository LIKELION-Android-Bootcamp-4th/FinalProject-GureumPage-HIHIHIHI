package com.hihihihi.presentation.ui.mypage.component

import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.hihihihi.domain.model.GureumThemeType
import com.hihihihi.presentation.designsystem.theme.GureumTheme
import com.hihihihi.presentation.utils.openAppOnPlayStore
import com.hihihihi.presentation.utils.openSupportEmail

@Composable
fun MyPageMenuSection(
    theme: GureumThemeType,
    onThemeToggle: (GureumThemeType) -> Unit,
    onLogoutClick: () -> Unit,
    onWithDrawClick: () -> Unit
) {
    val colors = GureumTheme.colors
    val context = LocalContext.current

    val versionName = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "Unknown"
    } catch (e: PackageManager.NameNotFoundException) {
        "Unknown"
    }

    Column(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
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
            showArrow = false,
            switchChecked = (theme == GureumThemeType.DARK),
            onSwitchToggle = { onThemeToggle(it) }
        ) { }

        MyPageMenuSettingItem(
            title = "로그아웃",
            showArrow = false,
        ) {
            onLogoutClick()
        }

        MyPageMenuSettingItem(
            title = "탈퇴",
            textColor = colors.gray400,
            showArrow = false,
        ) {
            onWithDrawClick()
        }

        MyPageMenuSettingItem(
            title = "앱 버전: v$versionName",
            textColor = colors.gray400,
            showArrow = false,
        ) {}
    }
}
