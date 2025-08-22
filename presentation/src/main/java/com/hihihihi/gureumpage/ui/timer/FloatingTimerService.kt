package com.hihihihi.gureumpage.ui.timer

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.hihihihi.gureumpage.MainActivity
import com.hihihihi.gureumpage.ui.mindmap.dpToPx
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FloatingTimerService : Service(), LifecycleOwner, SavedStateRegistryOwner {

    @Inject
    lateinit var timerRepository: TimerRepository

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: ComposeView
    private lateinit var layoutParams: WindowManager.LayoutParams

    private var isMinimized by mutableStateOf(false)
    private var isOnRightSide by mutableStateOf(false)

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        Log.d("FloatingTimer", "FloatingTimerService onCreate")

        initLifecycle()
        startForegroundNotification()
        initWindowManager()
        createFloatingView()
    }

    private fun initLifecycle() {
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    private fun initWindowManager() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 200
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createFloatingView() {
        floatingView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@FloatingTimerService)
            setViewTreeSavedStateRegistryOwner(this@FloatingTimerService)

            setContent {
                val timerState by timerRepository.timerState.collectAsState()

                FloatingTimer(
                    timerState = timerState,
                    isMinimized = isMinimized,
                    isOnRightSide = isOnRightSide,
                    onToggleTimer = {
                        lifecycleScope.launch {
                            timerRepository.sendFloatingAction(FloatingAction.ToggleTimer)
                        }
                    },
                    onOpenQuoteDialog = {
                        lifecycleScope.launch {
                            timerRepository.sendFloatingAction(FloatingAction.OpenMemoDialog)
                        }
                        returnToMainWithAction("open_memo_dialog")
                    },
                    onMinimize = {
                        isMinimized = true
                        moveToRightSide()
                    },
                    onExpand = {
                        isMinimized = false
                        moveToCenter()
                    }
                )
            }
        }

        floatingView.setOnTouchListener(
            FloatingTouchListener(
                layoutParams = layoutParams,
                windowManager = windowManager,
                onSwipeRight = {
                    isMinimized = true
                    isOnRightSide = true
                    moveToRightSide()
                },
                onExpand = {
                    isMinimized = false
                    isOnRightSide = false
                    moveToCenter()
                },
                onReturnToApp = {
                    lifecycleScope.launch {
                        timerRepository.sendFloatingAction(FloatingAction.ReturnToApp)
                    }
                    returnToMainWithAction(null)
                }
            )
        )

        windowManager.addView(floatingView, layoutParams)
    }

    private fun moveToRightSide() {
        layoutParams.apply {
            gravity = Gravity.TOP or Gravity.END
            x = 10
            y = 300
        }
        windowManager.updateViewLayout(floatingView, layoutParams)
    }

    private fun moveToCenter() {
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        layoutParams.apply {
            gravity = Gravity.TOP or Gravity.START
            x = (screenWidth - dpToPx(280)) / 2
            y = 200
        }
        windowManager.updateViewLayout(floatingView, layoutParams)
    }

    private fun returnToMainWithAction(action: String?) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            action?.let { putExtra("floating_action", it) }
            putExtra("return_to_timer", true)
            putExtra("user_book_id", timerRepository.timerState.value.userBookId)
        }
        startActivity(intent)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        return START_STICKY
    }

    override fun onDestroy() {
        if (::floatingView.isInitialized) {
            windowManager.removeView(floatingView)
        }

        savedStateRegistryController.performSave(Bundle())
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startForegroundNotification() {
        val channelId = "floating_timer_channel"
        val channelName = "독서 타이머"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "독서 타이머가 실행 중입니다"
                setShowBadge(false)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notification =
            Notification.Builder(this, channelId)
                .apply {
            setContentTitle("독서 타이머 실행 중")
            setContentText("탭하여 돌아가기")
            setSmallIcon(android.R.drawable.ic_media_play)
            setContentIntent(
                PendingIntent.getActivity(
                    this@FloatingTimerService,
                    0,
                    Intent(this@FloatingTimerService, MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        }.build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(1, notification)
        }
    }
}