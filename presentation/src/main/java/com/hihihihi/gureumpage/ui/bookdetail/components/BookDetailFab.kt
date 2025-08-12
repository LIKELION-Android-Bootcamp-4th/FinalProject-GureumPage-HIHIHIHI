package com.hihihihi.gureumpage.ui.bookdetail.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.components.Medi12Text
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme

@Composable
fun BookDetailFab(
    readingStatus: ReadingStatus,
    modifier: Modifier = Modifier,
    onActionClick: (index: Int) -> Unit
) {
    var fabExpanded by remember { mutableStateOf(false) }

    val fabItems = when (readingStatus) {
        ReadingStatus.PLANNED -> emptyList()
        ReadingStatus.READING -> listOf(
            MiniFabItem(R.drawable.ic_lightbulb_filled, "필사 추가") { onActionClick(0) },
            MiniFabItem(R.drawable.ic_graph, "마인드맵 그리기") { onActionClick(1) },
            MiniFabItem(R.drawable.ic_alarm_filled, "독서 타이머 시작") { onActionClick(2) },
            MiniFabItem(R.drawable.ic_edit_alt_filled, "독서 기록 추가") { onActionClick(3) },
        )
        ReadingStatus.FINISHED -> listOf(
            MiniFabItem(R.drawable.ic_lightbulb_filled, "필사 추가") { onActionClick(0) },
            MiniFabItem(R.drawable.ic_lightbulb_filled, "마인드맵 그리기") { onActionClick(1) },
        )
    }

    if (readingStatus == ReadingStatus.PLANNED) return

    val rotation by updateTransition(fabExpanded, label = "fabRotation")
        .animateFloat(label = "rotation") { if (it) 315f else 0f }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(horizontalAlignment = Alignment.End) {
            AnimatedVisibility(
                visible = fabExpanded,
                enter = fadeIn() + slideInVertically { it } + expandVertically(),
                exit = fadeOut() + slideOutVertically { it } + shrinkVertically()
            ) {
                MiniFabMenu(items = fabItems)
            }
            FloatingActionButton(
                onClick = { fabExpanded = !fabExpanded },
                shape = FloatingActionButtonDefaults.largeShape,
                elevation = FloatingActionButtonDefaults.elevation(4.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_plus),
                    contentDescription = "fab plus",
                    tint = GureumTheme.colors.white,
                    modifier = Modifier
                        .size(48.dp)
                        .rotate(rotation)
                )
            }
        }
    }
}


@Composable
fun MiniFabMenu(items: List<MiniFabItem>) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(end = 4.dp, bottom = 10.dp)
    ) {
        items.forEach { item ->
            MiniFabMenuItem(item)
        }
    }
}

@Composable
fun MiniFabMenuItem(item: MiniFabItem) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
                .background(GureumTheme.colors.dividerDeep)
                .padding(vertical = 6.dp, horizontal = 12.dp)
        ) {
            Medi12Text(text = item.title, color = GureumTheme.colors.gray800)
        }
        Spacer(modifier = Modifier.width(10.dp))
        SmallFloatingActionButton(
            onClick = item.onClick,
            containerColor = GureumTheme.colors.card
        ) {
            Icon(
                painter = painterResource(item.icon),
                contentDescription = item.title,
                tint = GureumTheme.colors.primary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Preview
@Composable
private fun BookDetailFabPreview() {
    GureumPageTheme {
        BookDetailFab(readingStatus = ReadingStatus.READING ,onActionClick = { })
    }
}


data class MiniFabItem(
    val icon: Int,
    val title: String,
    val onClick: () -> Unit
)
