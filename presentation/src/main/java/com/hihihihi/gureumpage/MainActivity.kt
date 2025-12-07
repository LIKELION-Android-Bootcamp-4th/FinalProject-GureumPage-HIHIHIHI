package com.hihihihi.gureumpage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.collection.isNotEmpty
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.hihihihi.domain.model.GureumThemeType
import com.hihihihi.domain.usecase.user.GetOnboardingCompleteUseCase
import com.hihihihi.domain.usecase.user.GetThemeFlowUseCase
import com.hihihihi.domain.usecase.user.UpdateLastVisitUseCase
import com.hihihihi.gureumpage.common.utils.NetworkManager
import com.hihihihi.gureumpage.designsystem.components.GureumAppBar
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.navigation.BottomNavItem
import com.hihihihi.gureumpage.navigation.GureumBottomNavBar
import com.hihihihi.gureumpage.navigation.GureumNavGraph
import com.hihihihi.gureumpage.navigation.NavigationRoute
import com.hihihihi.gureumpage.notification.common.Channels
import com.hihihihi.gureumpage.ui.nonetwork.NoNetworkScreen
import com.hihihihi.gureumpage.ui.timer.FloatingTimerService
import com.hihihihi.gureumpage.ui.timer.LocalAppBarUpClick
import com.hihihihi.gureumpage.ui.timer.TimerRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @HiltViewModel
    class GureumThemeViewModel @Inject constructor(
        getTheme: GetThemeFlowUseCase,
        private val networkManager: NetworkManager,
        private val getOnboardingCompleteUseCase: GetOnboardingCompleteUseCase
    ) : ViewModel() {
        val theme = getTheme().stateIn(viewModelScope, SharingStarted.Lazily, GureumThemeType.DARK)
        val isConnected = networkManager.networkState

        fun recheckNetwork() {
            networkManager.checkCurrentNetwork()
        }

        val showNetworkWarning = networkManager.showNetworkWarning

        fun dismissNetworkWarning() {
            networkManager.dismissNetworkWarning()
        }

        fun getOnboardingComplete(userId: String) = getOnboardingCompleteUseCase(userId)
    }

    @Inject
    lateinit var timerRepository: TimerRepository

    @Inject
    lateinit var updateLastVisitUseCase: UpdateLastVisitUseCase

    private var _navController: NavHostController? = null
    private var pendingDeepLink: Intent? = null

    // 위젯 라우트를 저장할 변수 추가
    private var _widgetRoute: String? = null

    @SuppressLint("ContextCastToActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // onCreate에서 인텐트 처리
        if (isWidgetDeepLink(intent)) {
            _widgetRoute = extractWidgetRoute(intent)
        }

        setTheme(R.style.Theme_GureumPage)
        Channels.ensureAll(this)
        enableEdgeToEdge()
        setContent {
            val viewModel = hiltViewModel<GureumThemeViewModel>()
            val initIntent = remember { intent }

            val showNetworkWarning by viewModel.showNetworkWarning.collectAsState()
            val currentTheme by viewModel.theme.collectAsState()

            val isTimerRunning by timerRepository.isTimerRunning.collectAsState()

            val isDark = currentTheme != GureumThemeType.LIGHT
            val window = (LocalContext.current as Activity).window

            val isConnected by viewModel.isConnected.collectAsState()

            val navController = rememberNavController()
            LaunchedEffect(navController) { _navController = navController }

            var hasShownNoNetwork by remember { mutableStateOf(false) }

            LaunchedEffect(isConnected) {
                if (!isConnected) {
                    hasShownNoNetwork = true
                } else if (hasShownNoNetwork) {
                    val user = FirebaseAuth.getInstance().currentUser

                    if (user == null) {
                        navController.navigate(NavigationRoute.Login.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        if (viewModel.getOnboardingComplete(user.uid).first()) {
                            navController.navigate(NavigationRoute.Home.route) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        } else {
                            navController.navigate(NavigationRoute.OnBoarding.route) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }

                    hasShownNoNetwork = false
                }
            }

            DisposableEffect(isDark) {
                WindowCompat.setDecorFitsSystemWindows(window, false)
                WindowInsetsControllerCompat(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !isDark
                    isAppearanceLightNavigationBars = !isDark
                }
                onDispose { }
            }

            LaunchedEffect(Unit) {
                // onCreate에서 처리한 위젯 라우트가 있다면 사용
                if (_widgetRoute != null) {
                    return@LaunchedEffect
                }

                if (routeIfNotificationDeepLink(initIntent)) return@LaunchedEffect

                // 보류분 처리도 동일 정책
                pendingDeepLink?.let { pending ->
                    if (isWidgetDeepLink(pending)) {
                        _widgetRoute = extractWidgetRoute(pending)
                    } else {
                        routeIfNotificationDeepLink(pending)
                    }
                    pendingDeepLink = null
                }
            }

            // 모드 상태에 따라 GureumPageTheme 에 반영
            GureumPageTheme(darkTheme = isDark) {
                Surface(modifier = Modifier.fillMaxSize(), color = GureumTheme.colors.background) {
                    if (!isConnected) {
                        NoNetworkScreen(
                            onRefresh = { viewModel.recheckNetwork() },
                            onExit = { finish() }
                        )
                    } else {
                        GureumPageApp(
                            navController,
                            initIntent,
                            isTimerRunning,
                            timerRepository,
                            pendingWidgetRoute = _widgetRoute, // 저장된 위젯 라우트 전달
                            onWidgetRouteConsumed = { _widgetRoute = null }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this, FloatingTimerService::class.java)
        stopService(intent)

        lifecycleScope.launch {
            updateLastVisitUseCase()
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        // 위젯 딥링크인 경우 처리
        if (isWidgetDeepLink(intent)) {
            _widgetRoute = extractWidgetRoute(intent)
            // NavController가 준비되었다면 바로 네비게이션
            _navController?.let { navController ->
                _widgetRoute?.let { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                    _widgetRoute = null
                }
            }
            return
        }

        if (_navController != null && _navController!!.graph.nodes.isNotEmpty()) {
            if (routeIfNotificationDeepLink(intent)) return
        } else {
            // 그래프 준비 전이면 보류
            pendingDeepLink = intent
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

    private fun extractWidgetRoute(intent: Intent): String? {
        val uri = intent.data ?: return null

        return when {
            uri.toString().matches(Regex("gureumpage://app/book/missedRecord/[^/?]+.*")) -> {
                val bookId = uri.pathSegments.lastOrNull()
                bookId?.let {
                    NavigationRoute.BookDetail.createRoute(
                        bookId = it,
                        showAddManualRecord = true
                    )
                }
            }

            uri.toString().matches(Regex("gureumpage://app/book/timer/[^/?]+.*")) -> {
                val bookId = uri.pathSegments.lastOrNull()
                bookId?.let { NavigationRoute.Timer.createRoute(userBookId = it) }
            }

            uri.toString().matches(Regex("gureumpage://app/book/addQuote/[^/?]+.*")) -> {
                val bookId = uri.pathSegments.lastOrNull()
                bookId?.let {
                    NavigationRoute.BookDetail.createRoute(
                        bookId = it,
                        showAddQuote = true
                    )
                }
            }

            uri.toString().matches(Regex("gureumpage://app/book/[^/?]+.*")) -> {
                val bookId = uri.pathSegments.lastOrNull()
                bookId?.let { NavigationRoute.BookDetail.createRoute(bookId = it) }
            }

            else -> {
                null
            }
        }.also { route ->
        }
    }

    private fun isWidgetDeepLink(intent: Intent): Boolean {
        val uri = intent.data ?: return false
        val isWidget = uri.scheme == "gureumpage" && uri.host == "app"
        return isWidget
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

    override fun onDestroy() {
        _navController = null
        _widgetRoute = null
        super.onDestroy()
    }
}

@Composable
fun GureumPageApp(
    navController: NavHostController,
    initIntent: Intent,
    isTimerRunning: Boolean,
    timerRepository: TimerRepository,
    pendingWidgetRoute: String? = null,
    onWidgetRouteConsumed: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    var lastBackMillis by remember { mutableLongStateOf(0L) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(currentRoute, isTimerRunning) {
        if (isTimerRunning &&
            currentRoute != null &&
            !currentRoute.startsWith(NavigationRoute.Timer.route) &&
            currentRoute != NavigationRoute.Splash.route &&
            currentRoute != NavigationRoute.Login.route &&
            currentRoute != NavigationRoute.OnBoarding.route
        ) {
            val userBookId = timerRepository.getTimerBookId()

            navController.navigate(NavigationRoute.Timer.createRoute(userBookId)) {
                popUpTo(NavigationRoute.Home.route) {
                    inclusive = false
                    saveState = false
                }
                launchSingleTop = true
            }
        }
    }

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
    val authRoutes =
        remember { setOf(NavigationRoute.Login.route, NavigationRoute.OnBoarding.route) }

    var initialHandle by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(initialHandle) {
        if (!initialHandle) {
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
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
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
            if (currentRoute != null && BottomNavItem.items.any { currentRoute.startsWith(it.route) })
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
                    snackbarHostState = snackbarHostState,
                    pendingWidgetRoute = pendingWidgetRoute
                )
            }
        }
    }
}