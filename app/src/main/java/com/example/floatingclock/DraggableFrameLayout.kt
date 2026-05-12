package com.example.floatingclock

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.FrameLayout

class DraggableFrameLayout(
    context: Context,
    private val windowManager: WindowManager,
    private val layoutParams: WindowManager.LayoutParams,
    private val preferencesManager: PreferencesManager
) : FrameLayout(context) {

    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var isDragging = false

    private val handler = Handler(Looper.getMainLooper())
    private val dragRunnable = Runnable {
        isDragging = true
        layoutParams.flags =
            layoutParams.flags and WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH.inv()
        windowManager.updateViewLayout(this, layoutParams)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialTouchX = event.x
                initialTouchY = event.y
                handler.postDelayed(dragRunnable, 500)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (isDragging) {
                    layoutParams.x = (event.rawX - initialTouchX).toInt()
                    layoutParams.y = (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(this, layoutParams)
                }
                return true
            }

            MotionEvent.ACTION_UP -> {
                if (isDragging) {
                    isDragging = false
                    preferencesManager.windowX = layoutParams.x
                    preferencesManager.windowY = layoutParams.y
                    layoutParams.flags =
                        layoutParams.flags or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    windowManager.updateViewLayout(this, layoutParams)
                } else {
                    handler.removeCallbacks(dragRunnable)
                }
                return true
            }

            MotionEvent.ACTION_CANCEL -> {
                handler.removeCallbacks(dragRunnable)
                if (isDragging) {
                    isDragging = false
                    preferencesManager.windowX = layoutParams.x
                    preferencesManager.windowY = layoutParams.y
                    layoutParams.flags =
                        layoutParams.flags or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    windowManager.updateViewLayout(this, layoutParams)
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}
