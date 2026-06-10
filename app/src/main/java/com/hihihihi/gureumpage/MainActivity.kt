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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.hihihihi.domain.model.GureumThemeType
import com.hihihihi.domain.repository.NetworkMonitor
import com.hihihihi.domain.usecase.user.GetOnboardingCompleteUseCase
import com.hihihihi.domain.usecase.user.GetThemeFlowUseCase
import com.hihihihi.domain.usecase.user.UpdateLastVisitUseCase
import com.hihihihi.gureumpage.service.FloatingTimerService
import com.hihihihi.presentation.designsystem.components.GureumAppBar
import com.hihihihi.presentation.designsystem.theme.GureumPageTheme
import com.hihihihi.presentation.designsystem.theme.GureumTheme
import com.hihihihi.presentation.navigation.BookDetail
import com.hihihihi.presentation.navigation.BottomNavItem
import com.hihihihi.presentation.navigation.GureumBottomNavBar
import com.hihihihi.presentation.navigation.GureumNavGraph
import com.hihihihi.presentation.navigation.Home
import com.hihihihi.presentation.navigation.Library
import com.hihihihi.presentation.navigation.Login
import com.hihihihi.presentation.navigation.MindMap
import com.hihihihi.presentation.navigation.MyPage
import com.hihihihi.presentation.navigation.OnBoarding
import com.hihihihi.presentation.navigation.Quotes
import com.hihihihi.presentation.navigation.Splash
import com.hihihihi.presentation.navigation.StatisticsMonthly
import com.hihihihi.presentation.navigation.StatisticsWeekly
import com.hihihihi.presentation.navigation.StatisticsYearly
import com.hihihihi.presentation.navigation.Timer
import com.hihihihi.presentation.navigation.Withdraw
import com.hihihihi.presentation.notification.common.Channels
import com.hihihihi.presentation.ui.nonetwork.NoNetworkScreen
import com.hihihihi.presentation.ui.timer.LocalAppBarUpClick
import com.hihihihi.presentation.ui.timer.TimerRepository
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
        private val networkManager: NetworkMonitor,
        private val getOnboardingCompleteUseCase: GetOnboardingCompleteUseCase,
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

    private var _widgetRoute: Any? = null

    @SuppressLint("ContextCastToActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isWidgetDeepLink(intent)) {
            _widgetRoute = extractWidgetRoute(intent)
        }

        setTheme(com.hihihihi.presentation.R.style.Theme_GureumPage)
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
                        navController.navigate(Login) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        if (viewModel.getOnboardingComplete(user.uid).first()) {
                            navController.navigate(Home) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        } else {
                            navController.navigate(OnBoarding) {
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
                if (_widgetRoute != null) {
                    return@LaunchedEffect
                }

                if (routeIfNotificationDeepLink(initIntent)) return@LaunchedEffect

                pendingDeepLink?.let { pending ->
                    if (isWidgetDeepLink(pending)) {
                        _widgetRoute = extractWidgetRoute(pending)
                    } else {
                        routeIfNotificationDeepLink(pending)
                    }
                    pendingDeepLink = null
                }
            }

            GureumPageTheme(darkTheme = isDark) {
                Surface(modifier = Modifier.fillMaxSize(), color = GureumTheme.colors.background) {
                    if (!isConnected) {
                        NoNetworkScreen(
                            onRefresh = { viewModel.recheckNetwork() },
                            onExit = { finish() },
                        )
                    } else {
                        GureumPageApp(
                            navController,
                            initIntent,
                            isTimerRunning,
                            timerRepository,
                            pendingWidgetRoute = _widgetRoute,
                            onWidgetRouteConsumed = { _widgetRoute = null },
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

        if (isWidgetDeepLink(intent)) {
            _widgetRoute = extractWidgetRoute(intent)
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
            pendingDeepLink = intent
        }
    }

    private fun routeIfNotificationDeepLink(intent: Intent): Boolean {
        val uri = intent.data ?: return false
        if (uri.scheme == "gureumpage" && uri.host == "app") return false

        return routeNotificationUri(uri).also { handled ->
            if (handled) {
                intent.data = null
                setIntent(intent)
            }
        }
    }

    private fun extractWidgetRoute(intent: Intent): Any? {
        val uri = intent.data ?: return null

        return when {
            uri.toString().matches(Regex("gureumpage://app/book/missedRecord/[^/?]+.*")) -> {
                val bookId = uri.pathSegments.lastOrNull()
                bookId?.let { BookDetail(bookId = it, showAddManualRecord = true) }
            }

            uri.toString().matches(Regex("gureumpage://app/book/timer/[^/?]+.*")) -> {
                val bookId = uri.pathSegments.lastOrNull()
                bookId?.let { Timer(userBookId = it) }
            }

            uri.toString().matches(Regex("gureumpage://app/book/addQuote/[^/?]+.*")) -> {
                val bookId = uri.pathSegments.lastOrNull()
                bookId?.let { BookDetail(bookId = it, showAddQuote = true) }
            }

            uri.toString().matches(Regex("gureumpage://app/book/[^/?]+.*")) -> {
                val bookId = uri.pathSegments.lastOrNull()
                bookId?.let { BookDetail(bookId = it) }
            }

            else -> null
        }
    }

    private fun isWidgetDeepLink(intent: Intent): Boolean {
        val uri = intent.data ?: return false
        return uri.scheme == "gureumpage" && uri.host == "app"
    }

    private fun routeNotificationUri(uri: Uri): Boolean {
        val nc = _navController ?: return false
        val targetRoute: Any = when {
            uri.host == "home" || uri.pathSegments.firstOrNull() == "home" ->
                Home

            uri.host == "bookdetail" || uri.pathSegments.firstOrNull() == "bookdetail" ->
                uri.lastPathSegment?.let { BookDetail(bookId = it) } ?: return false

            uri.host in setOf("statistics", "stats") ||
                    uri.pathSegments.firstOrNull() in setOf("statistics", "stats") ->
                when (uri.pathSegments.getOrNull(1)) {
                    "monthly" -> StatisticsMonthly
                    "yearly" -> StatisticsYearly
                    else -> StatisticsWeekly
                }

            else -> return false
        }

        if (targetRoute is Home) {
            nc.navigate(Home) {
                popUpTo<Splash> { inclusive = true }
                launchSingleTop = true
                restoreState = true
            }
            return true
        }

        nc.navigate(Home) {
            popUpTo<Splash> { inclusive = true }
            launchSingleTop = true
            restoreState = true
        }
        nc.navigate(targetRoute) {
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
    pendingWidgetRoute: Any? = null,
    onWidgetRouteConsumed: () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    var lastBackMillis by remember { mutableLongStateOf(0L) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isAuthRoute = currentDestination?.hierarchy?.any {
        it.hasRoute(Login::class) || it.hasRoute(OnBoarding::class) || it.hasRoute(Splash::class)
    } == true
    val isBottomNavRoute = currentDestination?.let { dest ->
        BottomNavItem.items.any { item -> dest.hierarchy.any { it.hasRoute(item.routeClass) } }
    } ?: false
    val isHomeRoute = currentDestination?.hasRoute(Home::class) == true
    val isTimerRoute = currentDestination?.hasRoute(Timer::class) == true

    LaunchedEffect(currentDestination, isTimerRunning) {
        val isExcludedFromTimer = currentDestination?.hierarchy?.any {
            it.hasRoute(Timer::class) || it.hasRoute(Splash::class) ||
                    it.hasRoute(Login::class) || it.hasRoute(OnBoarding::class)
        } == true
        if (isTimerRunning && currentDestination != null && !isExcludedFromTimer) {
            val userBookId = timerRepository.getTimerBookId()
            navController.navigate(Timer(userBookId = userBookId)) {
                popUpTo<Home> {
                    inclusive = false
                    saveState = false
                }
                launchSingleTop = true
            }
        }
    }

    var initialHandle by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(initialHandle) {
        if (!initialHandle) {
            initialHandle = true
        }
    }

    var timerAppbarUp by remember { mutableStateOf(0L) }

    LaunchedEffect(currentDestination) {
        if (!isTimerRoute) timerAppbarUp = 0L
    }

    BackHandler(enabled = !isAuthRoute) {
        when {
            isBottomNavRoute && !isHomeRoute -> {
                navController.navigate(Home) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }

            isHomeRoute -> {
                val now = System.currentTimeMillis()
                if (now - lastBackMillis < 2000L) (context as? Activity)?.finish()
                else {
                    lastBackMillis = now
                    Toast.makeText(context, "한 번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show()
                }
            }

            else -> {
                val popped = navController.popBackStack()
                if (!popped) {
                    if (!isHomeRoute) {
                        navController.navigate(Home) {
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

    val showBottomBar = isBottomNavRoute

    Scaffold(
        containerColor = GureumTheme.colors.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            when {
                currentDestination?.hasRoute(Library::class) == true ->
                    GureumAppBar(title = "서재")

                currentDestination?.hasRoute(Quotes::class) == true ->
                    GureumAppBar(title = "필사 목록")

                currentDestination?.hasRoute(StatisticsWeekly::class) == true ||
                        currentDestination?.hasRoute(StatisticsMonthly::class) == true ||
                        currentDestination?.hasRoute(StatisticsYearly::class) == true ->
                    GureumAppBar(title = "통계")

                currentDestination?.hasRoute(MyPage::class) == true ->
                    GureumAppBar(title = "마이페이지")

                currentDestination?.hasRoute(Timer::class) == true ->
                    GureumAppBar(
                        navController = navController,
                        title = "독서 스톱워치",
                        showUpButton = true,
                        onUpClick = { timerAppbarUp = System.currentTimeMillis() },
                    )

                currentDestination?.hasRoute(BookDetail::class) == true ->
                    GureumAppBar(navController, "", true)

                currentDestination?.hasRoute(Withdraw::class) == true ->
                    GureumAppBar(navController, "계정 탈퇴", true)
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (showBottomBar) GureumBottomNavBar(navController = navController)
        },
    ) { innerPadding ->
        Box(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(GureumTheme.colors.background),
        ) {
            CompositionLocalProvider(LocalAppBarUpClick provides timerAppbarUp) {
                GureumNavGraph(
                    navController = navController,
                    modifier = Modifier.fillMaxSize(),
                    snackbarHostState = snackbarHostState,
                    pendingWidgetRoute = pendingWidgetRoute,
                )
            }
        }
    }
}
