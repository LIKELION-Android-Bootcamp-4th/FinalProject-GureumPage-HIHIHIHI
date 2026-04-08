package com.hihihihi.presentation.ui.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.hihihihi.domain.model.GureumThemeType
import com.hihihihi.presentation.designsystem.components.Medi14Text
import com.hihihihi.presentation.designsystem.components.Semi16Text
import com.hihihihi.presentation.designsystem.theme.GureumPageTheme
import com.hihihihi.presentation.designsystem.theme.GureumTheme
import com.hihihihi.presentation.ui.model.MyPageUiModel
import com.hihihihi.presentation.ui.mypage.component.MyPageCalenderSection
import com.hihihihi.presentation.ui.mypage.component.MyPageMenuSection
import com.hihihihi.presentation.ui.mypage.component.MyPageUserProfileCard
import com.hihihihi.presentation.ui.mypage.component.NicknameChangeDialog
import com.hihihihi.presentation.utils.formatSecondsToReadableTimeWithoutSecond
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToWithdraw: (String) -> Unit,
    viewModel: MypageViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val theme by viewModel.theme.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    MypageEffect.NavigateToLogin -> onNavigateToLogin()
                    is MypageEffect.NavigateToWithdraw -> onNavigateToWithdraw(effect.userName)
                }
            }
        }
    }

    MyPageContent(
        isLoading = state.isLoading,
        errorMessage = state.errorMessage,
        myPageUiModel = state.myPageUiModel,
        dialogState = state.dialogState,
        theme = theme,
        onThemeToggle = viewModel::toggleTheme,
        onNicknameChangeClick = viewModel::onNicknameChangeClick,
        onLogoutClick = viewModel::onLogoutClick,
        onWithdrawClick = viewModel::onWithdrawClick,
        onDismissDialog = viewModel::dismissDialog,
        onChangeNickname = { new ->
            viewModel.changeNickname(new)
            viewModel.dismissDialog()
        },
        onLogout = viewModel::logout,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyPageContent(
    isLoading: Boolean,
    errorMessage: String?,
    myPageUiModel: MyPageUiModel?,
    dialogState: MyPageDialogState,
    theme: GureumThemeType,
    onThemeToggle: (GureumThemeType) -> Unit,
    onNicknameChangeClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    onDismissDialog: () -> Unit,
    onChangeNickname: (String) -> Unit,
    onLogout: () -> Unit,
) {
    val colors = GureumTheme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colors.background)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        when {
            isLoading -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            errorMessage != null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Semi16Text(
                        text = "사용자 정보를 불러오는데 실패했어요!",
                        color = GureumTheme.colors.gray600,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Medi14Text(
                        text = "잠시 후 다시 시도해주세요",
                        color = GureumTheme.colors.gray500,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            myPageUiModel != null -> {
                val data = myPageUiModel
                val timeText = remember(data.totalReadMinutes) {
                    formatSecondsToReadableTimeWithoutSecond(data.totalReadMinutes * 60)
                }
                MyPageUserProfileCard(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = "안녕하세요!",
                    badge = data.appellation?.ifBlank { "칭호 없음" } ?: "칭호 없음",
                    nickname = "${data.nickname?.ifBlank { "닉네임 없음" } ?: "닉네임 없음"}님",
                    provider = data.provider ?: "",
                    totalPages = "${data.totalPages}쪽",
                    totalBooks = "${data.totalBooks}권",
                    totalTime = timeText,
                    onEditNicknameClick = onNicknameChangeClick,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        MyPageCalenderSection(stats = myPageUiModel?.readingStats ?: emptyMap())
        Spacer(modifier = Modifier.height(28.dp))
        HorizontalDivider(thickness = 8.dp, color = colors.background10)
        MyPageMenuSection(
            theme = theme,
            onThemeToggle = onThemeToggle,
            onLogoutClick = onLogoutClick,
            onWithDrawClick = onWithdrawClick
        )
    }

    when (dialogState) {
        MyPageDialogState.None -> Unit

        MyPageDialogState.NicknameChange -> {
            NicknameChangeDialog(
                currentNickname = myPageUiModel?.nickname ?: "",
                onDismiss = onDismissDialog,
                onSave = onChangeNickname,
            )
        }

        MyPageDialogState.Logout -> {
            AlertDialog(
                onDismissRequest = onDismissDialog,
                title = { Semi16Text("로그아웃") },
                text = { Medi14Text("정말 로그아웃 하실건가요?") },
                containerColor = GureumTheme.colors.card,
                confirmButton = {
                    Medi14Text(
                        text = "로그아웃",
                        color = GureumTheme.colors.systemRed,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { onLogout() },
                    )
                },
                dismissButton = {
                    Medi14Text(
                        text = "취소",
                        color = GureumTheme.colors.gray500,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { onDismissDialog() },
                    )
                },
            )
        }
    }
}

@Preview(name = "DarkMode", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "LightMode", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun MyPageWithDataPreview() {
    val sampleData = MyPageUiModel(
        nickname = "구름이",
        appellation = "독서왕",
        provider = "google",
        readingStats = mapOf(
            LocalDate.now().minusDays(2) to 50,
            LocalDate.now().minusDays(1) to 120,
            LocalDate.now() to 80,
        ),
        totalBooks = 12,
        totalPages = 3450,
        totalReadMinutes = 1250,
    )
    GureumPageTheme {
        MyPageContent(
            isLoading = false,
            errorMessage = null,
            myPageUiModel = sampleData,
            dialogState = MyPageDialogState.None,
            theme = GureumThemeType.DARK,
            onThemeToggle = {},
            onNicknameChangeClick = {},
            onLogoutClick = {},
            onWithdrawClick = {},
            onDismissDialog = {},
            onChangeNickname = {},
            onLogout = {},
        )
    }
}
