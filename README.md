# FloatingClock

A lightweight Android floating clock app that displays the current system time (with seconds) as a transparent overlay on top of other apps.

## Features

- **Floating clock overlay** — displays HH:mm:ss in real-time above other apps
- **Transparent background** — only the time text is visible, minimizes visual obstruction
- **Drag to reposition** — long-press (~500ms) and drag to move the clock anywhere on screen
- **Tap-through** — outside touch events pass through to apps underneath (text area tap-through limited by Android platform)
- **Font selection** — choose from Default, Monospace, or Serif fonts
- **Color customization** — pick from 7 preset colors (White, Red, Green, Blue, Yellow, Cyan, Magenta)
- **Persistent settings** — all preferences survive app restarts
- **Material Design UI** — clean, modern settings interface with light/dark theme support
- **Bilingual** — supports English and Chinese (Simplified)

## Screenshots

*(Add screenshots here)*

## Requirements

- Android 8.0 (API 26) or later

## Permissions

| Permission | Purpose |
|---|---|
| **Display over other apps** | Required to show the floating clock overlay |
| **Notifications** | Required for the foreground service notification (Android 13+) |
| **Foreground service** | Keeps the clock running reliably in the background |

## Installation

Download the latest APK from the [Releases](https://github.com/Keivry/floatingclock/releases) page.

## Building from Source

```bash
# Clone the repository
git clone https://github.com/Keivry/floatingclock.git
cd floatingclock

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

The project uses:
- **Gradle 8.11.1** with Kotlin DSL
- **AGP 8.7.3** (Android Gradle Plugin)
- **Kotlin 2.0.21**
- **Min SDK**: 26 | **Target SDK**: 34 | **Compile SDK**: 35

## Architecture

- **FloatingWindowService** — foreground `Service` that manages the floating window lifecycle via `WindowManager`
- **DraggableFrameLayout** — custom `FrameLayout` subclass handling long-press detection and drag positioning
- **MainActivity** — settings UI with toggle switch, font picker, and color picker
- **PreferencesManager** — `SharedPreferences` wrapper for persisting user settings

## License

MIT
