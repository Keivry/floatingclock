package io.keivry.floatingclock

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color

/**
 * SharedPreferences utility for managing floating clock persistent preferences.
 * All getters return non-null default values on first access.
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

    var windowXLandscape: Int
        get() = prefs.getInt("window_x_landscape", 100)
        set(value) = prefs.edit().putInt("window_x_landscape", value).apply()

    var windowYLandscape: Int
        get() = prefs.getInt("window_y_landscape", 200)
        set(value) = prefs.edit().putInt("window_y_landscape", value).apply()

    var windowXPortrait: Int
        get() = prefs.getInt("window_x_portrait", 100)
        set(value) = prefs.edit().putInt("window_x_portrait", value).apply()

    var windowYPortrait: Int
        get() = prefs.getInt("window_y_portrait", 200)
        set(value) = prefs.edit().putInt("window_y_portrait", value).apply()

    fun saveWindowPosition(x: Int, y: Int, isLandscape: Boolean) {
        if (isLandscape) {
            windowXLandscape = x
            windowYLandscape = y
        } else {
            windowXPortrait = x
            windowYPortrait = y
        }
    }

    fun restoreWindowPosition(isLandscape: Boolean): Pair<Int, Int> {
        return if (isLandscape) {
            Pair(windowXLandscape, windowYLandscape)
        } else {
            Pair(windowXPortrait, windowYPortrait)
        }
    }
}
