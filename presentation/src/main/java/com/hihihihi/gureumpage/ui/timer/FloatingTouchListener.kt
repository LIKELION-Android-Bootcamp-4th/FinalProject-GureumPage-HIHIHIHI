package com.hihihihi.gureumpage.ui.timer

import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import kotlin.math.abs
import kotlin.math.sqrt

class FloatingTouchListener(
    private val layoutParams: WindowManager.LayoutParams,
    private val windowManager: WindowManager,
    private val onSwipeRight: () -> Unit,
    private val onExpand: () -> Unit,
    private val onReturnToApp: () -> Unit
) : View.OnTouchListener {
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var isDragging = false

    private var startTime = 0L
    private var startX = 0f
    private var startY = 0f

    private var lastClickTime = 0L

    companion object {
        private const val CLICK_TIME_THRESHOLD = 300L
        private const val DOUBLE_CLICK_TIME_THRESHOLD = 400L
        private const val CLICK_DISTANCE_THRESHOLD = 30f
        private const val SWIPE_DISTANCE_THRESHOLD = 200f
        private const val SWIPE_VELOCITY_THRESHOLD = 800f
        private const val SWIPE_MAX_VERTICAL_DISTANCE = 50f
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = layoutParams.x
                initialY = layoutParams.y
                initialTouchX = event.rawX
                initialTouchY = event.rawY

                startTime = System.currentTimeMillis()
                startX = event.rawX
                startY = event.rawY
                isDragging = false

                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val deltaX = event.rawX - initialTouchX
                val deltaY = event.rawY - initialTouchY

                if (!isDragging && (abs(deltaX) > CLICK_DISTANCE_THRESHOLD || abs(deltaY) > CLICK_DISTANCE_THRESHOLD)) {
                    isDragging = true
                }

                if (isDragging) {
                    // 수정된 부분: x 좌표 계산 방향 수정
                    layoutParams.x = (initialX + deltaX).toInt()
                    layoutParams.y = (initialY + deltaY).toInt()
                    windowManager.updateViewLayout(v, layoutParams)
                }

                return true
            }

            MotionEvent.ACTION_UP -> {
                val endTime = System.currentTimeMillis()
                val deltaTime = endTime - startTime
                val endX = event.rawX
                val endY = event.rawY
                val deltaX = endX - startX
                val deltaY = endY - startY
                val distance = sqrt((deltaX * deltaX + deltaY * deltaY).toDouble()).toFloat()

                if (!isDragging && deltaTime < CLICK_TIME_THRESHOLD && distance < CLICK_DISTANCE_THRESHOLD) {
                    val currentTime = System.currentTimeMillis()

                    if (currentTime - lastClickTime < DOUBLE_CLICK_TIME_THRESHOLD) {
                        onReturnToApp()
                    } else {
                        onExpand()
                    }

                    lastClickTime = currentTime
                } else if (isDragging) {
                    val velocity = if (deltaTime > 0) distance / (deltaTime / 1000f) else 0f
                    val realDeltaX = event.rawX - initialTouchX // 실제 스와이프 거리
                    val realDeltaY = event.rawY - initialTouchY

                    when {
                        realDeltaX > SWIPE_DISTANCE_THRESHOLD
                                && abs(realDeltaY) < SWIPE_DISTANCE_THRESHOLD
                                && velocity > SWIPE_VELOCITY_THRESHOLD
                                && deltaTime < 800L -> {
                            onSwipeRight()
                        }
                        else -> {
                            snapToEdge(v)
                        }
                    }
                }

                isDragging = false
                return true
            }
        }
        return false
    }

    private fun snapToEdge(view: View) {
        val displayMetrics = windowManager.defaultDisplay.let { display ->
            val metrics = android.util.DisplayMetrics()
            display.getMetrics(metrics)
            metrics
        }

        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        layoutParams.apply {
            x = if (x < screenWidth / 3) {
                20
            } else {
                screenWidth - view.width - 20
            }
            y = y.coerceIn(50, screenHeight - 200)
        }
        windowManager.updateViewLayout(view, layoutParams)
    }
}