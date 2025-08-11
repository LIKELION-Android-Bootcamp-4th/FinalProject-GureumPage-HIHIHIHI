package com.hihihihi.gureumpage.ui.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hihihihi.gureumpage.common.utils.formatSecondsToReadableTimeWithoutSecond
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import com.hihihihi.gureumpage.ui.mypage.component.MyPageCalenderSection
import com.hihihihi.gureumpage.ui.mypage.component.MyPageMenuSection
import com.hihihihi.gureumpage.ui.mypage.component.MyPageUserProfileCard
import com.hihihihi.gureumpage.ui.mypage.component.NicknameChangeDialog


@Composable
fun MyPageScreen(
    viewModel: MypageViewModel = hiltViewModel()
) {
    val colors = GureumTheme.colors
    val typography = GureumTypography
    val readingStats by viewModel.readingStats.collectAsState()
    val state by viewModel.uiState.collectAsState()

    var showNicknameDialog by rememberSaveable  { mutableStateOf(false) } // 다이얼로그 상태

    val timeText = remember(state.totalReadMinutes) {
        formatSecondsToReadableTimeWithoutSecond(state.totalReadMinutes * 60)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colors.background)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))


        when {
            state.loading -> Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            state.error != null -> Text(
                text = "오류: ${state.error}",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            else -> {
                //프로필 카드 ( 연필 아이콘 클릭 -> 다이얼로그 오픈)
                MyPageUserProfileCard(
                    title = "안녕하세요!",
                    badge = state.appellation.ifBlank { "칭호 없음" },
                    nickname = "${state.nickname.ifBlank { "닉네임 없음" }}님",
                    totalPages = "${state.totalPages}쪽",
                    totalBooks = "${state.totalBooks}권",
                    totalTime = timeText,
                    onEditNicknameClick = { showNicknameDialog = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        MyPageCalenderSection(stats = readingStats)

        Spacer(modifier = Modifier.height(28.dp))

        Divider(
            thickness = 8.dp,
            color = colors.background10
        )

        MyPageMenuSection()
    }

    //닉네임 벼경 다이얼로그
    if (showNicknameDialog) {
        NicknameChangeDialog(
            currentNickname = state.nickname,
            onDismiss = { showNicknameDialog = false },
            onSave = { new ->
                viewModel.changeNickname(new)
                showNicknameDialog = false
            }
        )
    }
}