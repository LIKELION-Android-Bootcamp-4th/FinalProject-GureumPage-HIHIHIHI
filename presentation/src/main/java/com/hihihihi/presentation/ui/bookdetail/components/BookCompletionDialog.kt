package com.hihihihi.presentation.ui.bookdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hihihihi.presentation.designsystem.components.BookCoverImage
import com.hihihihi.presentation.designsystem.components.GureumButton
import com.hihihihi.presentation.designsystem.components.GureumCancelButton
import com.hihihihi.presentation.designsystem.components.GureumCard
import com.hihihihi.presentation.designsystem.components.Medi12Text
import com.hihihihi.presentation.designsystem.components.Medi14Text
import com.hihihihi.presentation.designsystem.components.Semi16Text
import com.hihihihi.presentation.designsystem.theme.GureumTheme
import com.hihihihi.presentation.ui.model.UserBookUiModel

@Composable
fun BookCompletionDialog(
    userBook: UserBookUiModel,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val confettiComposition by rememberLottieComposition(
        LottieCompositionSpec.Asset("confetti.json"),
    )

    val confettiProgress by animateLottieCompositionAsState(
        composition = confettiComposition,
        iterations = 1,
    )

    val isConfettiFinished = confettiProgress == 1f


    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
    ) {
        Box {
            GureumCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Semi16Text(
                        text = "🎉 축하합니다! 🎉",
                        style = MaterialTheme.typography.headlineSmall,
                        color = GureumTheme.colors.primary,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Medi14Text(
                        text = "책을 모두 읽으셨네요!",
                        color = GureumTheme.colors.gray800,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    BookCoverImage(
                        imageUrl = userBook.imageUrl,
                        modifier = Modifier
                            .size(120.dp, 160.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Medi12Text(
                        text = userBook.title,
                        color = GureumTheme.colors.gray800,
                        maxLine = 2,
                        textAlign = TextAlign.Center,
                    )

                    Medi12Text(
                        text = userBook.author,
                        color = GureumTheme.colors.gray700,
                        maxLine = 1,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Medi12Text(
                        text = "${userBook.currentPage} / ${userBook.totalPage} 페이지 완독",
                        color = GureumTheme.colors.primary,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Medi14Text(
                        text = "책 상태를 '읽은 책'으로 변경하시겠습니까?",
                        color = GureumTheme.colors.gray800,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        GureumCancelButton(
                            "나중에",
                            modifier = Modifier.weight(1f),
                        ) {
                            onDismiss()
                        }

                        GureumButton(
                            "변경하기",
                            modifier = Modifier.weight(1f),
                        ) {
                            onConfirm()
                        }
                    }
                }
            }

            if (!isConfettiFinished) {
                LottieAnimation(
                    composition = confettiComposition,
                    progress = { confettiProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .align(Alignment.TopCenter),
                )
            }
        }
    }
}
