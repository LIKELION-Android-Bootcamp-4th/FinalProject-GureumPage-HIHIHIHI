package com.hihihihi.gureumpage.ui.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.ui.mypage.component.MyPageCalenderSection
import com.hihihihi.gureumpage.ui.mypage.component.MyPageMenuSection
import com.hihihihi.gureumpage.ui.mypage.component.MyPageUserProfileCard


@Composable
fun MyPageScreen() {
    val colors = GureumTheme.colors
    val typography = GureumTypography

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colors.background)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        //앱 바 ui 확인용
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Text(
                text = "마이페이지",
                style = GureumTypography.headlineSmall,
                color = colors.gray800,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        MyPageUserProfileCard(
            title = "안녕하세요!",
            badge = "새벽 독서가",
            nickname = "히히히히님",
            totalPages = "1892쪽",
            totalBooks = "16권",
            totalTime = "3,744시간"
        )

        Spacer(modifier = Modifier.height(16.dp))

        MyPageCalenderSection()

        Spacer(modifier = Modifier.height(28.dp))

        MyPageMenuSection()
    }
}