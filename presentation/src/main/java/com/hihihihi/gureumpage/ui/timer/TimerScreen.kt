package com.hihihihi.gureumpage.ui.timer

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.ui.bookdetail.components.AddQuoteDialog
import com.hihihihi.gureumpage.ui.timer.component.MemoList
import com.hihihihi.gureumpage.ui.timer.component.NowReadingCard
import com.hihihihi.gureumpage.ui.timer.component.StopReadingConfirmDialog
import com.hihihihi.gureumpage.ui.timer.component.StopReadingDialog
import com.hihihihi.gureumpage.ui.timer.component.TimerControlsRow
import com.hihihihi.gureumpage.ui.timer.component.TimerRing
import com.hihihihi.gureumpage.ui.timer.component.CountdownOverlayWithHole

val LocalAppBarUpClick = compositionLocalOf { 0L }

@Composable
fun TimerScreen(
    userBookId: String,
    onExit: () -> Unit = {},
    viewModel: TimerViewModel = hiltViewModel(),
    memoViewModel: MemoViewModel = hiltViewModel(),
) {
    val colors = GureumTheme.colors
    val context = LocalContext.current

    // UI 상태 수집
    val state by viewModel.uiState.collectAsState()
    val memoState by memoViewModel.ui.collectAsState()
    val sharedTimerState by viewModel.sharedTimerState.collectAsState()

    // 정지 확인 다이얼로그 상태
    var showStopDialog by rememberSaveable { mutableStateOf(false) }
    var wasRunningBeforeDialog by rememberSaveable { mutableStateOf(false) }

    // 필사 다이얼로그 - showMemoDialog 상태 사용
    var showBackExitScreen by rememberSaveable { mutableStateOf(false) }
    var wasRunningBeforeBack by rememberSaveable { mutableStateOf(false) }

    var overlayRectWin by remember { mutableStateOf<Rect?>(null) }
    var cardRectWin by remember { mutableStateOf<Rect?>(null) }

    val cardRectForOverlay by remember(overlayRectWin, cardRectWin) {
        mutableStateOf(
            if (overlayRectWin != null && cardRectWin != null) {
                val o = overlayRectWin!!
                val c = cardRectWin!!
                Rect(
                    offset = Offset(c.left - o.left, c.top - o.top),
                    size = c.size
                )
            } else null
        )
    }

    LaunchedEffect(Unit) {
        viewModel.ensureFloatingWindowClosed(context)
        viewModel.resumeIfNeeded()
    }

    LaunchedEffect(state.showMemoDialog) {
        if (state.showMemoDialog) {
            kotlinx.coroutines.delay(500)
        }
    }

    LaunchedEffect(userBookId) {
        viewModel.bind(userBookId)
        memoViewModel.clear()
        showBackExitScreen = false
        showStopDialog = false
    }

    val appBarUp = LocalAppBarUpClick.current
    var lastHandledUpTs by remember { mutableStateOf(0L) }

    LaunchedEffect(appBarUp, state.countdown) {
        if (state.countdown != null) return@LaunchedEffect
        if (appBarUp != 0L && appBarUp != lastHandledUpTs) {
            lastHandledUpTs = appBarUp
            wasRunningBeforeBack = state.isRunning
            if (state.isRunning) viewModel.pause()
            showBackExitScreen = true
        }
    }

    // 하드웨어 뒤로가기
    BackHandler(
        enabled = state.countdown == null && !showStopDialog && !state.showMemoDialog && !showBackExitScreen
    ) {
        wasRunningBeforeBack = state.isRunning
        if (state.isRunning) viewModel.pause()
        showBackExitScreen = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .onGloballyPositioned { overlayRectWin = it.boundsInWindow() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            // 현재 읽는 책 카드
            NowReadingCard(
                title = state.bookTitle,
                author = state.author,
                imageUrl = state.bookImageUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coords ->
                        cardRectWin = coords.boundsInWindow()
                    }
            )

            Spacer(Modifier.height(24.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {
                        if (state.countdown != null) return@IconButton

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (!Settings.canDrawOverlays(context)) {
                                val intent = Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    "package:${context.packageName}".toUri()
                                )
                                context.startActivity(intent)
                                return@IconButton
                            }
                        }

                        viewModel.startFloatingWindowMode(context)
                        (context as? Activity)?.moveTaskToBack(true)
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "플로팅 모드",
                        tint = colors.gray300,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // 원형 타이머
            TimerRing(
                modifier = Modifier
                    .size(240.dp)
                    .offset(y = 12.dp),
                isRunning = state.isRunning,
                centerText = state.countdown?.toString() ?: state.displayTimeMMSS
            )

            Spacer(Modifier.height(40.dp))

            // 메모 영역
            val lines = remember(memoState.items, userBookId) {
                memoState.items
                    .filter { q -> q.userBookId == userBookId }
                    .mapIndexed { idx, q -> "#${idx + 1} - ${q.content}" }
            }

            MemoList(
                lines = lines,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Spacer(Modifier.height(16.dp))

            // 컨트롤(시작/일시정지, 정지, 메모편집)
            TimerControlsRow(
                isRunning = state.isRunning,
                onToggle = {
                    if (state.countdown != null) return@TimerControlsRow
                    viewModel.toggleRun()
                },
                onStop = {
                    if (state.countdown != null) return@TimerControlsRow
                    wasRunningBeforeDialog = state.isRunning
                    if (state.isRunning) viewModel.pause()
                    showStopDialog = true
                },
                onEdit = {
                    if (state.countdown != null) return@TimerControlsRow
                    viewModel.showMemoDialog()
                }
            )

            Spacer(Modifier.height(32.dp))
        }

        if (state.countdown != null) {
            CountdownOverlayWithHole(
                number = state.countdown!!,
                holeRect = cardRectForOverlay,
                holeCornerRadiusDp = 16f,
                modifier = Modifier.fillMaxSize()
            )
        }

        // 정지 확인 다이얼로그
        if (showStopDialog) {
            StopReadingDialog(
                displayTime = state.displayTimeMMSS,
                title = state.bookTitle,
                author = state.author,
                currentPage = state.startPage,
                totalPage = state.totalPage,
                onConfirmStop = { showStopDialog = false },
                onDismiss = {
                    showStopDialog = false
                    if (wasRunningBeforeDialog) viewModel.start()
                },
                onConfirmStopPages = { s, e ->
                    viewModel.finishAndSave(userBookId, s, e)
                    showStopDialog = false
                    onExit()
                }
            )
        }

        if (state.showMemoDialog) {
            AddQuoteDialog(
                onDismiss = {
                    viewModel.dismissMemoDialog()
                },
                onSave = { page, content ->
                    memoViewModel.add(
                        userBookId = userBookId,
                        pageNumber = page?.toIntOrNull(),
                        content = content,
                        title = state.bookTitle,
                        author = state.author,
                        imageUrl = state.bookImageUrl,
                    ) {
                        // 저장 성공 후 다이얼로그 닫기
                        viewModel.dismissMemoDialog()
                    }
                },
                lastPage = state.totalPage
            )
        }

        // 뒤로가기 다이얼로그
        if (showBackExitScreen) {
            StopReadingConfirmDialog(
                displayTime = state.displayTimeMMSS,
                title = state.bookTitle,
                author = state.author,
                willSave = false,
                onContinue = {
                    showBackExitScreen = false
                    if (wasRunningBeforeBack) viewModel.start()
                },
                onStop = {
                    viewModel.stop()
                    showBackExitScreen = false
                    onExit()
                }
            )
        }
    }
}