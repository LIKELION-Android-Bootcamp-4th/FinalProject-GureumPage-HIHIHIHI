package com.hihihihi.gureumpage.ui.timer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.ui.bookdetail.components.AddQuoteDialog
import com.hihihihi.gureumpage.ui.timer.component.MemoList
import com.hihihihi.gureumpage.ui.timer.component.NowReadingCard
import com.hihihihi.gureumpage.ui.timer.component.StopReadingConfirmDialog
import com.hihihihi.gureumpage.ui.timer.component.StopReadingDialog
import com.hihihihi.gureumpage.ui.timer.component.TimerControlsRow
import com.hihihihi.gureumpage.ui.timer.component.TimerRing

val LocalAppBarUpClick = compositionLocalOf { 0L }

@Composable
fun TimerScreen(
    userBookId: String,
    onEditMemo: () -> Unit = {},
    onExit: () -> Unit = {},
    viewModel: TimerViewModel = hiltViewModel(),
    memoViewModel: MemoViewModel = hiltViewModel(),
) {
    val colors = GureumTheme.colors

    // UI 상태 수집
    val state by viewModel.uiState.collectAsState()

    val memoState by memoViewModel.ui.collectAsState()

    // 정지 확인 다이얼로그 상태
    var showStopDialog by rememberSaveable { mutableStateOf(false) }
    var wasRunningBeforeDialog by rememberSaveable { mutableStateOf(false) }
    //필사 다이얼로그
    var showAddQuoteDialog by remember { mutableStateOf(false) }
    var exitAfterConfirm by remember { mutableStateOf(false) }

    var showBackExitScreen by rememberSaveable { mutableStateOf(false) }
    var wasRunningBeforeBack by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(userBookId) { memoViewModel.observe(userBookId) }

    LaunchedEffect(Unit) {
        showBackExitScreen = false
        showStopDialog = false
        showAddQuoteDialog = false
    }

    val appBarUp = LocalAppBarUpClick.current
    var lastHandledUpTs by remember { mutableStateOf(0L) }
    LaunchedEffect(appBarUp) {
        if (appBarUp != 0L && appBarUp != lastHandledUpTs) {
            lastHandledUpTs = appBarUp
            wasRunningBeforeBack = state.isRunning
            if (state.isRunning) viewModel.pause()
            showBackExitScreen = true
        }
    }

    BackHandler(enabled = !showStopDialog && !showAddQuoteDialog && !showBackExitScreen) {
        wasRunningBeforeBack = state.isRunning
        if (state.isRunning) viewModel.pause()
        showBackExitScreen = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))

        // 현재 읽는 책 카드
        NowReadingCard(
            title = state.bookTitle,
            author = state.author,
            imageUrl = state.bookImageUrl,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // 원형 타이머
        TimerRing(
            modifier = Modifier
                .size(240.dp)
                .offset(y = 12.dp),
            progress = state.progress,
            centerText = state.displayTimeMMSS
        )

        Spacer(Modifier.height(40.dp))

        // 메모 영역

        val lines = remember(memoState.items) {
            memoState.items.mapIndexed { idx, q ->
                "#${idx + 1} - ${q.content}"
            }
        }
        MemoList(lines = lines)

        Spacer(Modifier.weight(1f))

        // 컨트롤(시작/일시정지, 정지, 메모편집)
        TimerControlsRow(
            isRunning = state.isRunning,
            onToggle = viewModel::toggleRun,   // 시작/일시정지
            onStop = {
                //다이얼로그 띄우기 전 일시 정지
                wasRunningBeforeDialog = state.isRunning
                if (state.isRunning) viewModel.pause()
                showStopDialog = true
            },// 정지 확인 다이얼로그 오픈
            onEdit = {
                wasRunningBeforeDialog = state.isRunning
                if (state.isRunning) viewModel.pause()
                showAddQuoteDialog = true
            }
        )

        Spacer(Modifier.height(24.dp))
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
            }
        )
    }
    //필사 다이얼로그
    if (showAddQuoteDialog) {
        AddQuoteDialog(
            onDismiss = {
                showAddQuoteDialog = false
                // 다이얼로그 전 실행 중이었다면 재개
                if (wasRunningBeforeDialog) viewModel.start()
            },
            onSave = { page, content ->
                val id = userBookId ?: return@AddQuoteDialog run {
                    showAddQuoteDialog = false
                    if (wasRunningBeforeDialog) viewModel.start()
                }

                memoViewModel.add(
                    userBookId = id,
                    pageNumber = page?.toIntOrNull(),
                    content = content,
                    title = state.bookTitle,
                    author = state.author,
                    imageUrl = state.bookImageUrl
                ) {
                    // 저장 성공 후 다이얼로그 닫고 타이머 재개
                    showAddQuoteDialog = false
                    if (wasRunningBeforeDialog) viewModel.start()
                }
            }
        )
    }
    //뒤로가기 다이얼로그
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
                onExit() // popBackStack 등 전달된 동작 수행
            }
        )
    }
}