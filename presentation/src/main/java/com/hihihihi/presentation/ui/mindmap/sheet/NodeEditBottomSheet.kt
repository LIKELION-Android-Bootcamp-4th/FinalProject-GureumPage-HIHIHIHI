package com.hihihihi.presentation.ui.mindmap.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hihihihi.presentation.designsystem.theme.GureumTheme
import io.github.hanhyo.composemindmap.model.MindMapNode

private val ICON_OPTIONS = listOf(
    "📌", "⭐", "💡", "🔥", "❤️", "✅", "🎯", "📚", "💬", "🌟",
    "🎨", "🌊", "🏆", "💎", "🌸", "🎭", "🔑", "⚡", "🌈", "🦋", "🍀",
)

private val COLOR_OPTIONS = listOf(
    Color(0xFFFFCDD2), // 연핑크
    Color(0xFFFFE0B2), // 연주황
    Color(0xFFFFF9C4), // 연노랑
    Color(0xFFC8E6C9), // 연초록
    Color(0xFFB3E5FC), // 연하늘
    Color(0xFFE1BEE7), // 연보라
    Color(0xFFF8BBD0), // 핑크
    Color(0xFFD7CCC8), // 연베이지
    Color(0xFFCFD8DC), // 연회색
    Color(0xFFEEEEEE), // 밝은 회색
    Color(0xFFB2EBF2), // 민트
    Color(0xFFDCEDC8), // 연두
    Color(0xFFF0F4C3), // 라임
    Color(0xFFFFECB3), // 크림옐로우
    Color(0xFFD1C4E9), // 라벤더
    Color(0xFFFFCCBC), // 살구
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NodeEditBottomSheet(
    node: MindMapNode?,
    onSave: (title: String, subtitle: String, icon: String?, color: Color?) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = GureumTheme.colors
    var title by remember { mutableStateOf(node?.title ?: "") }
    var subtitle by remember { mutableStateOf(node?.subtitle ?: "") }
    var selectedIcon by remember { mutableStateOf(node?.icon) }
    var selectedColor by remember { mutableStateOf(node?.color) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = if (node == null) "노드 추가" else "노드 수정",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.gray900,
            )

            // 아이콘 선택
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("아이콘", fontSize = 14.sp, color = colors.gray500)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp),
                ) {
                    items(ICON_OPTIONS) { icon ->
                        val isSelected = selectedIcon == icon
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) colors.primary10 else colors.gray200)
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = if (isSelected) colors.primary else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp),
                                )
                                .clickable {
                                    selectedIcon = if (isSelected) null else icon
                                },
                        ) {
                            Text(icon, fontSize = 20.sp)
                        }
                    }
                }
            }

            // 색상 선택
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("색상", fontSize = 14.sp, color = colors.gray500)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp),
                ) {
                    items(COLOR_OPTIONS) { color ->
                        val isSelected = selectedColor == color
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) colors.primary else colors.gray200,
                                    shape = CircleShape,
                                )
                                .clickable {
                                    selectedColor = if (isSelected) null else color
                                },
                        )
                    }
                }
            }

            // 제목 입력
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "제목",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.gray600,
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = {
                        Text("제목을 입력해주세요.", color = colors.gray400)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // 내용 입력
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "내용",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.gray600,
                )
                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it.take(500) },
                    placeholder = {
                        Text("책에서 마음에 드는 문장이나 생각을 자유롭게 적어보세요", color = colors.gray400)
                    },
                    minLines = 3,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "최대 500자",
                    modifier = Modifier.align(Alignment.End),
                    fontSize = 12.sp,
                    color = colors.gray400,
                )
            }

            Button(
                onClick = { onSave(title.trim(), subtitle.trim(), selectedIcon, selectedColor) },
                enabled = title.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.white,
                    disabledContainerColor = colors.gray200,
                    disabledContentColor = colors.gray400,
                ),
            ) {
                Text("저장하기", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
