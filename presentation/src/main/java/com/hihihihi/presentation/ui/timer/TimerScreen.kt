package com.hihihihi.presentation.ui.timer

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.hihihihi.presentation.designsystem.theme.GureumPageTheme
import com.hihihihi.presentation.designsystem.theme.GureumTheme
import com.hihihihi.presentation.ui.bookdetail.components.AddQuoteDialog
import com.hihihihi.presentation.ui.timer.component.CountdownOverlayWithHole
import com.hihihihi.presentation.ui.timer.component.MemoList
import com.hihihihi.presentation.ui.timer.component.NowReadingCard
import com.hihihihi.presentation.ui.timer.component.StopReadingConfirmDialog
import com.hihihihi.presentation.ui.timer.component.StopReadingDialog
import com.hihihihi.presentation.ui.timer.component.TimerControlsRow
import com.hihihihi.presentation.ui.timer.component.TimerRing

val LocalAppBarUpClick = compositionLocalOf { 0L }

@Composable
fun TimerScreen(
    userBookId: String,
    onExit: () -> Unit = {},
    viewModel: TimerViewModel = hiltViewModel(),
    memoViewModel: MemoViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val memoState by memoViewModel.ui.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.ensureFloatingWindowClosed(context)
        viewModel.resumeIfNeeded()
    }

    // 오버레이 권한 요청 / 플로팅 윈도우 시작
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    TimerEffect.RequestOverlayPermission -> {
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            "package:${context.packageName}".toUri(),
                        )
                        context.startActivity(intent)
                    }

                    TimerEffect.StartFloatingWindow -> {
                        viewModel.startFloatingWindowMode(context)
                        (context as? Activity)?.moveTaskToBack(true)
                    }
                }
            }
        }
    }

    LaunchedEffect(state.showMemoDialog) {
        if (state.showMemoDialog) {
            kotlinx.coroutines.delay(500)
        }
    }

    LaunchedEffect(userBookId) {
        viewModel.bind(userBookId)
        memoViewModel.clear()
        viewModel.dismissDialog()
    }

    val appBarUp = LocalAppBarUpClick.current
    var lastHandledUpTs by remember { mutableStateOf(0L) }

    LaunchedEffect(appBarUp, state.countdown) {
        if (state.countdown != null) return@LaunchedEffect
        if (appBarUp != 0L && appBarUp != lastHandledUpTs) {
            lastHandledUpTs = appBarUp
            viewModel.showDialog(TimerDialogType.BackExit)
        }
    }

    BackHandler(
        enabled = state.countdown == null && !state.showStopDialog && !state.showMemoDialog && !state.showBackExitScreen,
    ) {
        viewModel.showDialog(TimerDialogType.BackExit)
    }

    val memoLines = remember(memoState.items, userBookId) {
        memoState.items
            .filter { it.userBookId == userBookId }
            .mapIndexed { idx, q -> "#${idx + 1} - ${q.content}" }
    }

    TimerContent(
        bookTitle = state.bookTitle,
        author = state.author,
        bookImageUrl = state.bookImageUrl,
        isRunning = state.isRunning,
        countdown = state.countdown,
        displayTimeMMSS = state.displayTimeMMSS,
        startPage = state.startPage,
        totalPage = state.totalPage,
        showMemoDialog = state.showMemoDialog,
        showStopDialog = state.showStopDialog,
        showBackExitScreen = state.showBackExitScreen,
        memoLines = memoLines,
        onToggle = viewModel::toggleRun,
        onRequestStopDialog = { viewModel.showDialog(TimerDialogType.StopConfirm) },
        onDismissDialog = viewModel::dismissDialog,
        onConfirmStopAndExit = { s, e ->
            viewModel.finishAndSave(userBookId, s, e)
            viewModel.dismissDialog()
            onExit()
        },
        onStopAndExit = {
            viewModel.stop()
            viewModel.dismissDialog()
            onExit()
        },
        onShowMemoDialog = { viewModel.showDialog(TimerDialogType.Memo) },
        onSaveMemo = { page, content ->
            memoViewModel.add(
                userBookId = userBookId,
                pageNumber = page?.toIntOrNull(),
                content = content,
                title = state.bookTitle,
                author = state.author,
                imageUrl = state.bookImageUrl,
            ) { viewModel.dismissDialog() }
        },
        onOpenFloatingMode = {
            val canDraw = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context)
            viewModel.openFloatingMode(canDraw)
        },
    )
}

@Composable
private fun TimerContent(
    bookTitle: String,
    author: String,
    bookImageUrl: String,
    isRunning: Boolean,
    countdown: Int?,
    displayTimeMMSS: String,
    startPage: Int?,
    totalPage: Int?,
    showMemoDialog: Boolean,
    showStopDialog: Boolean,
    showBackExitScreen: Boolean,
    memoLines: List<String>,
    onToggle: () -> Unit,
    onRequestStopDialog: () -> Unit,
    onDismissDialog: (resumeTimer: Boolean) -> Unit,
    onConfirmStopAndExit: (startPage: Int, endPage: Int) -> Unit,
    onStopAndExit: () -> Unit,
    onShowMemoDialog: () -> Unit,
    onSaveMemo: (page: String?, content: String) -> Unit,
    onOpenFloatingMode: () -> Unit,
) {
    val colors = GureumTheme.colors

    var overlayRectWin by remember { mutableStateOf<Rect?>(null) }
    var cardRectWin by remember { mutableStateOf<Rect?>(null) }
    val cardRectForOverlay by remember {
        derivedStateOf {
            val o = overlayRectWin ?: return@derivedStateOf null
            val c = cardRectWin ?: return@derivedStateOf null
            Rect(offset = Offset(c.left - o.left, c.top - o.top), size = c.size)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .onGloballyPositioned { overlayRectWin = it.boundsInWindow() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(24.dp))

            NowReadingCard(
                title = bookTitle,
                author = author,
                imageUrl = bookImageUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { cardRectWin = it.boundsInWindow() },
            )

            Spacer(Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(
                    onClick = {
                        if (countdown != null) return@IconButton
                        onOpenFloatingMode()
                    },
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "플로팅 모드",
                        tint = colors.gray300,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            TimerRing(
                modifier = Modifier
                    .size(240.dp)
                    .offset(y = 12.dp),
                isRunning = isRunning,
                centerText = countdown?.toString() ?: displayTimeMMSS,
            )

            Spacer(Modifier.height(40.dp))

            MemoList(
                lines = memoLines,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )

            Spacer(Modifier.height(16.dp))

            TimerControlsRow(
                isRunning = isRunning,
                onToggle = {
                    if (countdown != null) return@TimerControlsRow
                    onToggle()
                },
                onStop = {
                    if (countdown != null) return@TimerControlsRow
                    onRequestStopDialog()
                },
                onEdit = {
                    if (countdown != null) return@TimerControlsRow
                    onShowMemoDialog()
                },
            )

            Spacer(Modifier.height(32.dp))
        }

        if (countdown != null) {
            CountdownOverlayWithHole(
                number = countdown,
                holeRect = cardRectForOverlay,
                holeCornerRadiusDp = 16f,
                modifier = Modifier.fillMaxSize(),
            )
        }

        if (showStopDialog) {
            StopReadingDialog(
                displayTime = displayTimeMMSS,
                title = bookTitle,
                author = author,
                currentPage = startPage,
                totalPage = totalPage,
                onConfirmStop = { onDismissDialog(false) },
                onDismiss = { onDismissDialog(true) },
                onConfirmStopPages = { s, e -> onConfirmStopAndExit(s, e) },
            )
        }

        if (showMemoDialog) {
            AddQuoteDialog(
                onDismiss = { onDismissDialog(false) },
                onSave = { page, content ->
                    onSaveMemo(page, content)
                },
                lastPage = totalPage,
            )
        }

        if (showBackExitScreen) {
            StopReadingConfirmDialog(
                displayTime = displayTimeMMSS,
                title = bookTitle,
                author = author,
                willSave = false,
                onContinue = { onDismissDialog(true) },
                onStop = onStopAndExit,
            )
        }
    }
}

@Preview(name = "Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TimerPreview() {
    GureumPageTheme {
        TimerContent(
            bookTitle = "채식주의자",
            author = "한강",
            bookImageUrl = "",
            isRunning = true,
            countdown = null,
            displayTimeMMSS = "12:34",
            startPage = 50,
            totalPage = 300,
            showMemoDialog = false,
            showStopDialog = false,
            showBackExitScreen = false,
            memoLines = listOf("#1 - 인상깊은 문장"),
            onToggle = {},
            onRequestStopDialog = {},
            onDismissDialog = {},
            onConfirmStopAndExit = { _, _ -> },
            onStopAndExit = {},
            onShowMemoDialog = {},
            onSaveMemo = { _, _ -> },
            onOpenFloatingMode = {},
        )
    }
}
