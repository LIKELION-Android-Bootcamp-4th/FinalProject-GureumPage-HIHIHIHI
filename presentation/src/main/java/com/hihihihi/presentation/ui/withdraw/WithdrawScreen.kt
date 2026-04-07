package com.hihihihi.presentation.ui.withdraw

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hihihihi.presentation.R
import com.hihihihi.presentation.designsystem.components.Medi12Text
import com.hihihihi.presentation.designsystem.components.Medi14Text
import com.hihihihi.presentation.designsystem.components.Semi12Text
import com.hihihihi.presentation.designsystem.components.Semi14Text
import com.hihihihi.presentation.designsystem.components.Semi16Text
import com.hihihihi.presentation.designsystem.components.Semi18Text
import com.hihihihi.presentation.designsystem.theme.GureumPageTheme
import com.hihihihi.presentation.designsystem.theme.GureumTheme
import com.hihihihi.presentation.navigation.NavigationRoute
import kotlinx.coroutines.delay

data class WithdrawalReason(
    val iconRes: Int,
    val title: String,
    val description: String
)

private val withdrawalReasons = listOf(
    WithdrawalReason(R.drawable.ic_record, "독서 기록", "읽은 책과 진행 상황"),
    WithdrawalReason(R.drawable.ic_memo, "필사 & 마인드맵", "인상깊은 문장들과 소중한 마인드맵 기록"),
    WithdrawalReason(R.drawable.ic_goal, "독서 시간 기록", "누적된 모든 독서 시간"),
    WithdrawalReason(R.drawable.ic_statistics, "독서 분석", "장르별 분석과 독서 패턴")
)

@Composable
fun WithdrawScreen(
    userName: String,
    navController: NavHostController,
    viewModel: WithdrawViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    var currentHighlightedIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            currentHighlightedIndex = (currentHighlightedIndex + 1) % withdrawalReasons.size

            listState.animateScrollToItem(
                index = currentHighlightedIndex,
                scrollOffset = -50
            )
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.withdrawEvent.collect {
            navController.navigate(NavigationRoute.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 제목
        Semi18Text(
            text = "정말 떠나시나요?",
            color = GureumTheme.colors.gray900,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 부제목
        Medi14Text(
            text = "${userName}님과 함께한",
            color = GureumTheme.colors.gray400,
        )

        Medi14Text(
            text = "소중한 독서 여정이 모두 사라져요",
            color = GureumTheme.colors.gray400,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 경고 박스
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    GureumTheme.colors.systemRed.copy(alpha = 0.1f),
                    RoundedCornerShape(12.dp)
                )
                .border(
                    1.dp,
                    GureumTheme.colors.systemRed.copy(alpha = 0.5f),
                    RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Semi16Text(
                    text = "계정 탈퇴 시 삭제되는 데이터",
                    color = GureumTheme.colors.systemRed
                )
                Spacer(modifier = Modifier.height(4.dp))
                Semi12Text(
                    text = "한 번 삭제된 데이터는 복구될 수 없어요",
                    color = GureumTheme.colors.gray700,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(withdrawalReasons) { index, reason ->
                WithdrawalReasonItem(
                    reason = reason,
                    isHighlighted = index == currentHighlightedIndex
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    GureumTheme.colors.point.copy(alpha = 0.2f),
                    RoundedCornerShape(12.dp)
                )
                .border(
                    1.dp,
                    GureumTheme.colors.point.copy(alpha = 0.5f),
                    RoundedCornerShape(12.dp)
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Semi16Text(
                    text = "잠깐만요! 🥺",
                    color = GureumTheme.colors.gray900,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Medi14Text(
                    text = "지금까지 쌓아온 독서 기록들이 정말 아까워요.",
                    color = GureumTheme.colors.gray500,
                )
                Medi14Text(
                    text = "다시 한 번 생각해보시는 건 어떨까요?",
                    color = GureumTheme.colors.gray500,
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 버튼들
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 다시 생각해볼게요 버튼
            Button(
                onClick = { navController.popBackStack() },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GureumTheme.colors.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Medi14Text(
                    text = "다시 생각해볼게요",
                    color = GureumTheme.colors.white,
                )
            }

            // 정말 탈퇴할래요 버튼
            Button(
                onClick = { viewModel.withdrawUser() },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GureumTheme.colors.gray200
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Medi14Text(
                    text = "정말 탈퇴할래요",
                    color = GureumTheme.colors.white,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 하단 안내 텍스트
        Medi12Text(
            text = "탈퇴 후에도 언제든 다시 돌아와서",
            color = GureumTheme.colors.gray500,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Medi12Text(
            text = "새로운 독서 여정을 시작할 수 있어요",
            color = GureumTheme.colors.gray500,
        )
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .background(
                        GureumTheme.background.color,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        color = GureumTheme.colors.primary
                    )

                    if (uiState.loadingMessage?.isNotEmpty() == true) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Medi14Text(
                            text = uiState.loadingMessage!!,
                            color = GureumTheme.colors.gray700
                        )
                    }
                }
            }
        }
    }

    SnackbarHost(
        hostState = snackbarHostState,
    ) { data ->
        Snackbar(
            snackbarData = data,
            containerColor = GureumTheme.colors.systemRed,
            contentColor = Color.White
        )
    }
}

@Composable
fun WithdrawalReasonItem(
    reason: WithdrawalReason,
    isHighlighted: Boolean = false
) {
    val scale by animateFloatAsState(
        targetValue = if (isHighlighted) 1f else 0.9f,
        animationSpec = tween(300),
        label = "scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isHighlighted) {
            GureumTheme.colors.primary10
        } else {
            GureumTheme.colors.background10
        },
        animationSpec = tween(500),
        label = "backgroundColor"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isHighlighted) {
            GureumTheme.colors.primary10
        } else {
            Color.Transparent
        },
        animationSpec = tween(500),
        label = "borderColor"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .background(
                backgroundColor,
                RoundedCornerShape(12.dp)
            )
            .then(
                if (isHighlighted) {
                    Modifier.border(
                        2.dp,
                        borderColor,
                        RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier
                }
            )
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = reason.iconRes),
            contentDescription = null,
            modifier = Modifier.size(if (isHighlighted) 36.dp else 32.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Semi14Text(
                text = reason.title,
                color = if (isHighlighted) GureumTheme.colors.gray900 else GureumTheme.colors.gray700,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Semi12Text(
                text = reason.description,
                color = if (isHighlighted) GureumTheme.colors.gray600 else GureumTheme.colors.gray500,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WithdrawalScreenPreview() {
    GureumPageTheme {
        WithdrawScreen(
            "name",
            navController = rememberNavController(),
        )
    }
}
