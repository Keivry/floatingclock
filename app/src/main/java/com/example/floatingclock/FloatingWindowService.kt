package com.example.floatingclock

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FloatingWindowService : Service() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var windowManager: WindowManager
    private lateinit var layoutParams: WindowManager.LayoutParams
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var floatingView: DraggableFrameLayout
    private lateinit var tvTime: TextView

    private val handler = Handler(Looper.getMainLooper())
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    private var isViewAdded = false

    private val timeUpdateRunnable = object : Runnable {
        override fun run() {
            tvTime.text = dateFormat.format(Date())
            applyStyleFromPreferences()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        preferencesManager = PreferencesManager(this)

        layoutParams = WindowManager.LayoutParams().apply {
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
            format = PixelFormat.TRANSLUCENT
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.TOP or Gravity.START
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            layoutParams.flags =
                layoutParams.flags or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        }

        floatingView = DraggableFrameLayout(this, windowManager, layoutParams, preferencesManager)
        LayoutInflater.from(this).inflate(R.layout.floating_window, floatingView, true)
        tvTime = floatingView.findViewById(R.id.tvTime)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildNotification()
        startForegroundWithType(notification)

        if (!isViewAdded) {
            validateCoordinates()
            windowManager.addView(floatingView, layoutParams)
            isViewAdded = true
        }

        handler.removeCallbacks(timeUpdateRunnable)
        handler.post(timeUpdateRunnable)

        return START_STICKY
    }

    override fun onDestroy() {
        handler.removeCallbacks(timeUpdateRunnable)
        if (isViewAdded) {
            windowManager.removeView(floatingView)
            isViewAdded = false
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun buildNotification(): android.app.Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_content))
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun startForegroundWithType(notification: android.app.Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun validateCoordinates() {
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        val estimatedWindowWidth = 200
        val estimatedWindowHeight = 60

        preferencesManager.windowX = preferencesManager.windowX
            .coerceIn(0, (screenWidth - estimatedWindowWidth).coerceAtLeast(0))
        preferencesManager.windowY = preferencesManager.windowY
            .coerceIn(0, (screenHeight - estimatedWindowHeight).coerceAtLeast(0))

        layoutParams.x = preferencesManager.windowX
        layoutParams.y = preferencesManager.windowY
    }

    private fun applyStyleFromPreferences() {
        tvTime.typeface = when (preferencesManager.fontFamily) {
            "monospace" -> Typeface.MONOSPACE
            "serif" -> Typeface.SERIF
            else -> Typeface.DEFAULT
        }
        tvTime.setTextColor(preferencesManager.textColor)
    }

    companion object {
        private const val CHANNEL_ID = "floating_clock_channel"
        private const val NOTIFICATION_ID = 1
    }
}
