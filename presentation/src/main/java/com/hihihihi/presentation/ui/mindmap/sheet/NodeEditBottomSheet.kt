package com.hihihihi.presentation.ui.mindmap.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
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
    Color(0xFFFFCDD2), Color(0xFFFFE0B2), Color(0xFFFFF9C4),
    Color(0xFFC8E6C9), Color(0xFFB3E5FC), Color(0xFFE1BEE7),
    Color(0xFFF8BBD0), Color(0xFFD7CCC8), Color(0xFFCFD8DC), Color(0xFFEEEEEE),
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

    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = if (node == null) "노드 추가" else "노드 수정",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.gray900,
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("제목") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = subtitle,
                onValueChange = { subtitle = it },
                label = { Text("내용") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

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
                                .background(if (isSelected) colors.primary10 else colors.gray150)
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

            Button(
                onClick = { onSave(title.trim(), subtitle.trim(), selectedIcon, selectedColor) },
                enabled = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.white,
                    disabledContainerColor = colors.gray150,
                    disabledContentColor = colors.gray400,
                ),
            ) {
                Text("저장", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
