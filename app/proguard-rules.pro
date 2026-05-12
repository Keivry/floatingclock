# 保留应用入口
-keep class com.example.floatingclock.MainActivity { *; }

# 保留前台 Service
-keep class com.example.floatingclock.FloatingWindowService { *; }

# 保留自定义视图
-keep class com.example.floatingclock.DraggableFrameLayout { *; }

# 保留 SharedPreferences 访问相关
-keepclassmembers class com.example.floatingclock.PreferencesManager {
    public *;
}

# Android 通用混淆规则
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Kotlin
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
