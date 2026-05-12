package io.keivry.floatingclock

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color

/**
 * SharedPreferences 工具类，管理悬浮时钟的持久化偏好设置。
 * 所有 getter 均返回非空默认值。
 */
class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("floating_clock_prefs", Context.MODE_PRIVATE)

    var isFloatingEnabled: Boolean
        get() = prefs.getBoolean("is_floating_enabled", false)
        set(value) = prefs.edit().putBoolean("is_floating_enabled", value).apply()

    var fontFamily: String
        get() = prefs.getString("font_family", "default") ?: "default"
        set(value) = prefs.edit().putString("font_family", value).apply()

    var textColor: Int
        get() = prefs.getInt("text_color", Color.WHITE)
        set(value) = prefs.edit().putInt("text_color", value).apply()

    var windowX: Int
        get() = prefs.getInt("window_x", 100)
        set(value) = prefs.edit().putInt("window_x", value).apply()

    var windowY: Int
        get() = prefs.getInt("window_y", 200)
        set(value) = prefs.edit().putInt("window_y", value).apply()
}
