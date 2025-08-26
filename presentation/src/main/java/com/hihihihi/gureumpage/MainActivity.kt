package com.hihihihi.gureumpage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.collection.isNotEmpty
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hihihihi.domain.model.GureumThemeType
import com.hihihihi.domain.usecase.user.GetThemeFlowUseCase
import com.hihihihi.gureumpage.common.utils.NetworkManager
import com.hihihihi.gureumpage.designsystem.components.GureumAppBar
import com.hihihihi.gureumpage.designsystem.components.Medi14Text
import com.hihihihi.gureumpage.designsystem.components.Semi16Text
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.navigation.BottomNavItem
import com.hihihihi.gureumpage.navigation.GureumBottomNavBar
import com.hihihihi.gureumpage.navigation.GureumNavGraph
import com.hihihihi.gureumpage.navigation.NavigationRoute
import com.hihihihi.gureumpage.notification.common.Channels
import com.hihihihi.gureumpage.ui.timer.FloatingTimerService
import com.hihihihi.gureumpage.ui.timer.LocalAppBarUpClick
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @HiltViewModel
    class GureumThemeViewModel @Inject constructor(
        getTheme: GetThemeFlowUseCase,
        private val networkManager: NetworkManager
    ) : ViewModel() {
        val theme = getTheme().stateIn(viewModelScope, SharingStarted.Lazily, GureumThemeType.DARK)

        val showNetworkWarning = networkManager.showNetworkWarning

        fun dismissNetworkWarning() {
            networkManager.dismissNetworkWarning()
        }
    }

    private var _navController: NavHostController? = null
    private var pendingDeepLink: Intent? = null

    @SuppressLint("ContextCastToActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_GureumPage)
        Channels.ensureAll(this)
        enableEdgeToEdge()
        setContent {
            val viewModel = hiltViewModel<GureumThemeViewModel>()
            val initIntent = remember { intent }

            val showNetworkWarning by viewModel.showNetworkWarning.collectAsState()
            val currentTheme by viewModel.theme.collectAsState()

            val isDark = currentTheme != GureumThemeType.LIGHT
            val window = (LocalContext.current as Activity).window

            DisposableEffect(isDark) {
                WindowCompat.setDecorFitsSystemWindows(window, false)
                WindowInsetsControllerCompat(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !isDark
                    isAppearanceLightNavigationBars = !isDark
                }
                onDispose { }
            }

            val navController = rememberNavController() //DeepLink 라우팅 처리 위해 navController 상위에서 선언
            LaunchedEffect(navController) { _navController = navController }

            LaunchedEffect(Unit) {
                if (routeIfWidgetDeepLink(initIntent)) return@LaunchedEffect
                if (routeIfNotificationDeepLink(initIntent)) return@LaunchedEffect

                // 보류분 처리도 동일 정책
                pendingDeepLink?.let { pending ->
                    val handled = routeIfWidgetDeepLink(pending) || routeIfNotificationDeepLink(pending)
                    pendingDeepLink = null
                    if (handled) return@LaunchedEffect
                }
            }

            // 모드 상태에 따라 GureumPageTheme 에 반영
            GureumPageTheme(darkTheme = isDark) {
                Surface(modifier = Modifier.fillMaxSize(), color = GureumTheme.colors.background) {
                    GureumPageApp(navController, initIntent)

                    if (showNetworkWarning) {
                        NetworkWarningBanner(
                            onDismiss = { viewModel.dismissNetworkWarning() }
                        )
                    }

                    Log.d("DeepLink", "onCreate - initialized App")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this, FloatingTimerService::class.java)
        stopService(intent)
    }

    // 백그라운드에서 알림으로 들어올 시 딥링크 처리
    @SuppressLint("RestrictedApi")
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        if (_navController != null && _navController!!.graph.nodes.isNotEmpty()) {
            if (routeIfWidgetDeepLink(intent)) return
            if (routeIfNotificationDeepLink(intent)) return
        } else {
            // 그래프 준비 전이면 보류
            pendingDeepLink = intent
        }
    }

    // 위젯 스킴(gureumpage://app/…)이면 수동 라우팅
    private fun routeIfWidgetDeepLink(intent: Intent): Boolean {
        val uri = intent.data ?: return false
        return routeWidgetUri(uri)
    }

    private fun routeWidgetUri(uri: Uri): Boolean {

        Log.d("DeepLink", "routeDeepLink - uri : $uri")
        when {
            // 책 상세: gureumpage://app/book/{bookId}?from=widget
            uri.toString().matches(Regex("gureumpage://app/book/[^/?]+.*")) -> {
                val bookId = uri.pathSegments.lastOrNull()

                bookId?.let {
                    Log.d("DeepLink", "Route BookDetail: bookId=$bookId")
                    _navController?.navigate(NavigationRoute.BookDetail.createRoute(it)) {
                        Log.d("DeepLink", "Routeed BookDetail: bookId=$bookId")
                        popUpTo(NavigationRoute.Splash.route) { inclusive = true }
                    }
                }
                return true
            }

            // 놓친 기록: gureumpage://app/book/missedRecord/{bookId}?from=widget
            uri.toString().matches(Regex("gureumpage://app/book/missedRecord/[^/?]+.*")) -> {
                val bookId = uri.pathSegments.lastOrNull()
                Log.d("DeepLink", "Missed record: bookId=$bookId")

                bookId?.let {
                    _navController?.navigate(NavigationRoute.BookDetail.createRoute(it))
                }
                return true
            }

            // 타이머: gureumpage://app/book/timer/{bookId}?from=widget
            uri.toString().matches(Regex("gureumpage://app/book/timer/[^/?]+.*")) -> {
                Log.d("DeepLink", "Timer")
                _navController?.navigate(NavigationRoute.Timer.route)
                return true
            }

            // 필사 추가: gureumpage://app/book/addQuote/{bookId}?from=widget
            uri.toString().matches(Regex("gureumpage://app/book/addQuote/[^/?]+.*")) -> {
                Log.d("DeepLink", "Add quote")
                _navController?.navigate(NavigationRoute.Quotes.route)
                return true
            }

            else -> {
                Log.d("DeepLink", "No matching pattern: $uri")
                return false
            }
        }
    }

    private fun routeIfNotificationDeepLink(intent: Intent): Boolean {
        val uri = intent.data ?: return false
        // 위젯 스킴은 제외
        if (uri.scheme == "gureumpage" && uri.host == "app") return false

        return routeNotificationUri(uri).also { handled ->
            if (handled) {
                // 재진입 방지
                intent.data = null
                setIntent(intent)
            }
        }
    }

    private fun routeNotificationUri(uri: Uri): Boolean {
        val nc = _navController ?: return false
        val target = when {
            uri.host == "home" || uri.pathSegments.firstOrNull() == "home" ->
                NavigationRoute.Home.route

            uri.host == "bookdetail" || uri.pathSegments.firstOrNull() == "bookdetail" ->
                uri.lastPathSegment?.let { NavigationRoute.BookDetail.createRoute(it) }

            uri.host in setOf("statistics", "stats") ||
                    uri.pathSegments.firstOrNull() in setOf("statistics", "stats") ->
                when (uri.pathSegments.getOrNull(1)) {
                    "weekly" -> NavigationRoute.StatisticsWeekly.route
                    "monthly" -> NavigationRoute.StatisticsMonthly.route
                    "yearly" -> NavigationRoute.StatisticsYearly.route
                    else -> NavigationRoute.StatisticsWeekly.route
                }

            else -> null
        } ?: return false

        if (target == NavigationRoute.Home.route) {
            nc.navigate(NavigationRoute.Home.route) {
                popUpTo(NavigationRoute.Splash.route) { inclusive = true }
                launchSingleTop = true
                restoreState = true
            }
            return true
        }

        // Home 보이지 않게 쌓고 → Target
        nc.navigate(NavigationRoute.Home.route) {
            popUpTo(NavigationRoute.Splash.route) { inclusive = true }
            launchSingleTop = true
            restoreState = true
        }
        nc.navigate(target) {
            launchSingleTop = true
            restoreState = true
        }
        return true
    }
}

@Composable
fun GureumPageApp(navController: NavHostController, initIntent: Intent) {
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    var lastBackMillis by remember { mutableLongStateOf(0L) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val hideBottomBarRoutes = listOf(
        NavigationRoute.Login.route,
        NavigationRoute.OnBoarding.route,
        NavigationRoute.BookDetail.route,
        NavigationRoute.Timer.route,
        NavigationRoute.MindMap.route,
        NavigationRoute.Withdraw.route,
        NavigationRoute.Search.route,
        NavigationRoute.Splash.route
    )

    val bottomRoutes = remember { BottomNavItem.items.map { it.route }.toSet() }
    val authRoutes = remember { setOf(NavigationRoute.Login.route, NavigationRoute.OnBoarding.route) }

    var initialHandle by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(initialHandle) {
        if (!initialHandle) {
//            navController.handleDeepLink(initIntent)
            initialHandle = true
        }
    }

    var timerAppbarUp by remember { mutableStateOf(0L) }

    LaunchedEffect(currentRoute) {
        if (currentRoute != NavigationRoute.Timer.route) {
            timerAppbarUp = 0L
        }
    }

    BackHandler(enabled = currentRoute !in authRoutes) {
        when {
            // 바텀 내비 아이템 중 홈이 아닐 때 -> 홈으로 스위칭
            currentRoute in bottomRoutes && currentRoute != NavigationRoute.Home.route -> {
                navController.navigate(NavigationRoute.Home.route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }

            // 홈이면 → 종료
            currentRoute == NavigationRoute.Home.route -> {
                val now = System.currentTimeMillis()
                if (now - lastBackMillis < 2000L) (context as? Activity)?.finish()
                else {
                    lastBackMillis = now
                    Toast.makeText(context, "한 번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show()
                }
            }

            // 그 외 화면
            else -> {
                val popped = navController.popBackStack()
                if (!popped) {
                    // 항상 홈을 거쳐 종료하기
                    if (currentRoute != NavigationRoute.Home.route) {
                        navController.navigate(NavigationRoute.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else {
                        (context as? Activity)?.finish()
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = GureumTheme.colors.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            when (currentRoute) {
                NavigationRoute.Library.route -> GureumAppBar(title = "서재")
                NavigationRoute.Quotes.route -> GureumAppBar(title = "필사 목록")
                NavigationRoute.StatisticsWeekly.route -> GureumAppBar(title = "통계")
                NavigationRoute.MyPage.route -> GureumAppBar(title = "마이페이지")
                NavigationRoute.MindMap.route -> GureumAppBar(navController, "마인드맵", true)
                NavigationRoute.Timer.route -> GureumAppBar(
                    navController = navController,
                    title = "독서 스톱워치",
                    showUpButton = true,
                    onUpClick = {
                        timerAppbarUp = System.currentTimeMillis()
                    }
                )

                NavigationRoute.BookDetail.route -> GureumAppBar(navController, "", true)
                NavigationRoute.Withdraw.route -> GureumAppBar(navController, "계정 탈퇴", true)
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (currentRoute != null && hideBottomBarRoutes.none { currentRoute.startsWith(it) })
                GureumBottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        Box(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(GureumTheme.colors.background)
        ) {
            CompositionLocalProvider(LocalAppBarUpClick provides timerAppbarUp) {
                GureumNavGraph(
                    navController = navController,
                    modifier = Modifier.fillMaxSize(),
                    snackbarHostState = snackbarHostState
                )
            }
        }
    }
    Log.d("APP", "GureumPageApp init - end")
}

@Composable
fun NetworkWarningBanner(
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(top = 40.dp)
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = GureumTheme.colors.systemRed
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = GureumTheme.colors.white
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Semi16Text(
                    "앗, 네트워크 연결이 끊어졌어요!",
                    color = GureumTheme.colors.white
                )
                Spacer(modifier = Modifier.height(4.dp))
                Medi14Text(
                    "데이터를 불러오거나 저장할 수 없어요.",
                    color = GureumTheme.colors.white
                )
                Medi14Text(
                    "다시 연결하면 바로 불러올 수 있어요!",
                    color = GureumTheme.colors.white
                )
            }

            IconButton(onClick = onDismiss) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    modifier = Modifier.size(16.dp),
                    contentDescription = null,
                    tint = GureumTheme.colors.white
                )
            }
        }
    }
}