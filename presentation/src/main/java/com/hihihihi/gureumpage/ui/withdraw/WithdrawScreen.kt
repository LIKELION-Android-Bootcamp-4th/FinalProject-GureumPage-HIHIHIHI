package com.hihihihi.gureumpage.ui.withdraw

import androidx.compose.foundation.Image
import com.hihihihi.gureumpage.designsystem.components.Medi12Text
import com.hihihihi.gureumpage.designsystem.components.Medi14Text
import com.hihihihi.gureumpage.designsystem.components.Semi12Text
import com.hihihihi.gureumpage.designsystem.components.Semi14Text
import com.hihihihi.gureumpage.designsystem.components.Semi16Text
import com.hihihihi.gureumpage.designsystem.components.Semi18Text
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme

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
    onWithdrawClick: () -> Unit = {},
    onReconsiderClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // 제목
        Semi18Text(
            text = "정말 떠나시나요?",
            color = GureumTheme.colors.gray900,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 부제목
        Medi14Text(
            text = "히히히히님과 함께한",
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
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(withdrawalReasons) { reason ->
                WithdrawalReasonItem(reason = reason)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    GureumTheme.colors.primary10,
                    RoundedCornerShape(12.dp)
                )
                .border(
                    1.dp,
                    GureumTheme.colors.primary50,
                    RoundedCornerShape(12.dp)
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Semi16Text(
                    text = "잠깐만요!",
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
                onClick = onReconsiderClick,
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
                onClick = onWithdrawClick,
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

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun WithdrawalReasonItem(reason: WithdrawalReason) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                GureumTheme.colors.background10,
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = reason.iconRes),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Semi14Text(
                text = reason.title,
                color = GureumTheme.colors.gray700,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Semi12Text(
                text = reason.description,
                color = GureumTheme.colors.gray500,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WithdrawalScreenPreview() {
    GureumPageTheme {
        WithdrawScreen("name", navController = rememberNavController(), onWithdrawClick = {}, onReconsiderClick = {})
    }
}