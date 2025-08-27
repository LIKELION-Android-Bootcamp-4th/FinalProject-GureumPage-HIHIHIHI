package com.hihihihi.gureumpage.ui.home.components

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.common.utils.pxToDp
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Composable
fun rememberPickerState() = remember { PickerState() }

class PickerState {
    var selectedItem by mutableStateOf("")
    var selectedIndex by mutableStateOf(0)
}

@Composable
fun Picker(
    items: List<String>,
    state: PickerState = rememberPickerState(),
    modifier: Modifier = Modifier,
    startIndex: Int = 0,
    visibleItemsCount: Int = 3,
    textModifier: Modifier = Modifier,
    infiniteScroll: Boolean = true,
) {
    val visibleItemsMiddle = visibleItemsCount / 2
    val (listScrollCount, paddingItemsCount) = if (infiniteScroll) {
        Integer.MAX_VALUE to 0
    } else {
        // 제한된 스크롤일 때는 앞뒤에 패딩 아이템 추가
        // visibleItemsCount = 3 때문에 앞 뒤 있을 때 중간 값이 선택 됨
        items.size + (visibleItemsMiddle * 2) to visibleItemsMiddle
    }
    val listScrollMiddle = listScrollCount / 2
    val listStartIndex = if (infiniteScroll) {
        listScrollMiddle - listScrollMiddle % items.size - visibleItemsMiddle + startIndex
    } else {
        // 제한 스크롤일 땐 첫 번째 인덱스로
        startIndex
    }

    fun getItem(index: Int): String? = if (infiniteScroll) {
        items[index % items.size]
    } else {
        // 패딩 영역이면 null, 아니면 실제 아이템 반환
        val adjustedIndex = index - paddingItemsCount
        if (adjustedIndex in 0 until items.size) items[adjustedIndex]
        else null

    }


    val listState = rememberLazyListState(initialFirstVisibleItemIndex = listStartIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    val itemHeightPixels = remember { mutableIntStateOf(0) }
    val itemHeightDp = pxToDp(itemHeightPixels.value)

    val fadingEdgeGradient = remember {
        Brush.verticalGradient(
            0f to Color.Transparent,
            0.5f to Color.Black,
            1f to Color.Transparent
        )
    }

    // 피커가 열릴 때 초기 인덱스로 스크롤
    LaunchedEffect(items, startIndex) {
        listState.scrollToItem(listStartIndex)
        state.selectedIndex = startIndex
        state.selectedItem = getItem(listStartIndex + visibleItemsMiddle) ?: ""
    }

    LaunchedEffect(listState, infiniteScroll) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { index ->
                if (infiniteScroll) {
                    (index + visibleItemsMiddle) % items.size
                } else {
                    val centerIndex = index + visibleItemsMiddle
                    val adjustedIndex = centerIndex - paddingItemsCount
                    adjustedIndex.coerceIn(0, items.size - 1)
                }
            }
            .distinctUntilChanged()
            .collect { index ->
                state.selectedIndex = index
                state.selectedItem = items[index]
            }
    }

    Box(modifier = modifier) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeightDp * visibleItemsCount)
                .fadingEdge(fadingEdgeGradient)
        ) {
            items(listScrollCount) { index ->
                Text(
                    text = getItem(index) ?: "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = GureumTypography.headlineLarge,
                    modifier = Modifier
                        .onSizeChanged { size -> itemHeightPixels.value = size.height }
                        .then(textModifier)
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier
                .padding(top = itemHeightDp * visibleItemsMiddle)
                .height(1.dp),
            color = GureumTheme.colors.primary
        )

        HorizontalDivider(
            modifier = Modifier
                .padding(top = (itemHeightDp * visibleItemsMiddle) + itemHeightDp)
                .height(1.dp),
            color = GureumTheme.colors.primary
        )
    }
}

fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }

