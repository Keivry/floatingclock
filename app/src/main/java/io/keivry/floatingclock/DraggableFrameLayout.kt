package io.keivry.floatingclock

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
    private var lastAppliedX = 0
    private var lastAppliedY = 0

    private val handler = Handler(Looper.getMainLooper())
    private val dragRunnable = Runnable {
        isDragging = true
        lastAppliedX = layoutParams.x
        lastAppliedY = layoutParams.y
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
                    val newX = (event.rawX - initialTouchX).toInt()
                    val newY = (event.rawY - initialTouchY).toInt()
                    if (Math.abs(newX - lastAppliedX) >= MOVE_THRESHOLD ||
                        Math.abs(newY - lastAppliedY) >= MOVE_THRESHOLD) {
                        layoutParams.x = newX
                        layoutParams.y = newY
                        windowManager.updateViewLayout(this, layoutParams)
                        lastAppliedX = newX
                        lastAppliedY = newY
                    }
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

    companion object {
        private const val MOVE_THRESHOLD = 4
    }
}
