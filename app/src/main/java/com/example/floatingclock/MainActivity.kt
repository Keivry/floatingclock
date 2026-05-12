package com.example.floatingclock

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.RadioGroup
import android.widget.Switch
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity() {

    private lateinit var preferencesManager: PreferencesManager
    private var notificationPermissionRequested = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // 权限请求结果回调 — 不做额外处理，下次 onResume 时会重新检查
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferencesManager = PreferencesManager(applicationContext)

        val switchFloating = findViewById<Switch>(R.id.switchFloating)
        val rgFont = findViewById<RadioGroup>(R.id.rgFont)
        val rgColor = findViewById<RadioGroup>(R.id.rgColor)

        switchFloating.isChecked = preferencesManager.isFloatingEnabled

        when (preferencesManager.fontFamily) {
            "monospace" -> rgFont.check(R.id.rbFontMonospace)
            "serif" -> rgFont.check(R.id.rbFontSerif)
            else -> rgFont.check(R.id.rbFontDefault)
        }

        when (preferencesManager.textColor) {
            Color.RED -> rgColor.check(R.id.rbColorRed)
            Color.GREEN -> rgColor.check(R.id.rbColorGreen)
            Color.BLUE -> rgColor.check(R.id.rbColorBlue)
            Color.YELLOW -> rgColor.check(R.id.rbColorYellow)
            Color.CYAN -> rgColor.check(R.id.rbColorCyan)
            Color.MAGENTA -> rgColor.check(R.id.rbColorMagenta)
            else -> rgColor.check(R.id.rbColorWhite)
        }

        switchFloating.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    requestOverlayPermission()
                } else {
                    preferencesManager.isFloatingEnabled = true
                    startService(Intent(this@MainActivity, FloatingWindowService::class.java))
                    checkBatteryOptimization()
                }
            } else {
                preferencesManager.isFloatingEnabled = false
                stopService(Intent(this@MainActivity, FloatingWindowService::class.java))
            }
        }

        rgFont.setOnCheckedChangeListener { _, checkedId ->
            val fontFamily = when (checkedId) {
                R.id.rbFontMonospace -> "monospace"
                R.id.rbFontSerif -> "serif"
                else -> "default"
            }
            preferencesManager.fontFamily = fontFamily
        }

        rgColor.setOnCheckedChangeListener { _, checkedId ->
            val color = when (checkedId) {
                R.id.rbColorRed -> Color.RED
                R.id.rbColorGreen -> Color.GREEN
                R.id.rbColorBlue -> Color.BLUE
                R.id.rbColorYellow -> Color.YELLOW
                R.id.rbColorCyan -> Color.CYAN
                R.id.rbColorMagenta -> Color.MAGENTA
                else -> Color.WHITE
            }
            preferencesManager.textColor = color
        }
    }

    override fun onResume() {
        super.onResume()

        val switchFloating = findViewById<Switch>(R.id.switchFloating)

        if (switchFloating.isChecked && !Settings.canDrawOverlays(this)) {
            switchFloating.isChecked = false
            preferencesManager.isFloatingEnabled = false
            stopService(Intent(this, FloatingWindowService::class.java))
        }

        if (!notificationPermissionRequested) {
            requestNotificationPermission()
        }
    }

    private fun requestOverlayPermission() {
        AlertDialog.Builder(this)
            .setTitle("权限提示")
            .setMessage("需要\"显示在其他应用上层\"权限才能使用悬浮窗功能")
            .setPositiveButton("去设置") { _, _ ->
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            }
            .setNegativeButton("取消") { _, _ ->
                val switchFloating = findViewById<Switch>(R.id.switchFloating)
                switchFloating.isChecked = false
            }
            .show()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                notificationPermissionRequested = true
                AlertDialog.Builder(this)
                    .setTitle("通知权限")
                    .setMessage("需要通知权限以保持悬浮时钟运行")
                    .setPositiveButton("允许") { _, _ ->
                        requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                    .setNegativeButton("取消") { _, _ -> }
                    .show()
            } else {
                notificationPermissionRequested = true
            }
        }
    }

    private fun checkBatteryOptimization() {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
            AlertDialog.Builder(this)
                .setTitle("电池优化")
                .setMessage("为防止悬浮时钟被系统关闭，建议关闭电池优化")
                .setPositiveButton("去设置") { _, _ ->
                    val intent = Intent(
                        Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                        Uri.parse("package:$packageName")
                    )
                    startActivity(intent)
                }
                .setNegativeButton("取消") { _, _ -> }
                .show()
        }
    }
}
