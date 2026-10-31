# Clear

**A calm, private companion for your mental well-being — for Android.**

Clear helps you notice how you are doing, find support in difficult moments, and collect the things that keep you grounded. Everything stays on your phone: no accounts, no cloud, no tracking, no internet connection required.

> **Clear is not a medical device and does not replace professional help.**
> It does not diagnose anything. If you are in crisis, please contact your local emergency number or a crisis line right away.

The app is available in **English** and **German**. It follows your phone's language, and you can change it any time under *Settings → Language*. The guided meditations are spoken in German only.

---

## Features

Clear is organized into five areas:

### Check In
- Quick self check-ins across eight areas: energy, clarity, recovery, joy, mood, thoughts, connection and structure
- History and charts of your ratings over time
- Validated questionnaires, available every 14 days: **PHQ-9**, **WHO-5** and **WSAS**
- **Personal early-warning signs:** Clear compares new ratings to *your own* usual range (not to fixed thresholds) and gently points out when an area has been lower than usual for a while. A single bad day never triggers a warning.
- Personal ideas per area, with a different suggestion for high, medium and low days
- Start an idea with a mood rating, end it with a second rating, or cancel it without one

### Meditate
- Guided meditations with a built-in player

### Acute help
- Exercises for difficult moments, chosen by emotion and situation and grouped into six kinds: body, thoughts, feelings, attention, action and self-support
- Thought records, inspired by cognitive behavioral therapy (CBT)
- An emergency card with your important contacts
- Calming music that keeps playing in the background

### My Anchor
- Your personal collection of things that help: images, quotes, music, lists and ideas
- Emergency contacts
- An evening reflection for small successes and good moments
- Optional, gentle reminders — at fixed or randomized times

### Settings
- Reminders, focus mode (Do Not Disturb while an idea is running), appearance, language
- **Delete everything:** removes all data, files, reminders and settings for good

## Privacy

- All data is stored locally in an on-device database.
- No accounts, no analytics, no ads, no network access.
- Android cloud backup and device-to-device transfer are disabled.
- **Watch companion:** if you use the Wear OS app, the watch receives only the titles of running ideas, the time of your last check-in and your language setting, through Google's Wearable Data Layer. This usually goes directly over Bluetooth; when the watch is connected only via Wi-Fi or LTE, Google relays it encrypted.

## Wear OS companion

A small companion app for Wear OS watches (for example the Galaxy Watch 7):

- **Running ideas:** see which ideas are running and end one right on your wrist, with the same "How do you feel now?" rating as on the phone, or cancel it without a rating. The result is saved on the phone.
- The watch shows its texts in the language set in Clear on your phone.
- **Tile:** swipe from the watch face to see how many ideas are running and whether you have checked in today. Tap it to open the running ideas.

The watch app has no data of its own and needs the phone app. Both must be signed with the same key.

## Installation

1. Download the latest APK from the [Releases](https://github.com/ItsDylan255/Clear/releases) page.
2. Open it on your Android phone and allow installing apps from this source when asked.

Requires **Android 8.0 (API 26)** or newer.

For the watch, install the Wear OS APK from the same release on the watch, for example with `adb install` over wireless debugging. It needs Wear OS 3 or newer.

## Building from source

Requirements: Android Studio (or the Android SDK) and JDK 17+.

```bash
git clone https://github.com/ItsDylan255/Clear.git
cd Clear
./gradlew assembleDebug
```

The APK is written to `app/build/outputs/apk/debug/`. The watch app is built with `./gradlew :wear:assembleDebug` and written to `wear/build/outputs/apk/debug/`.

## Tech stack

- Kotlin and Jetpack Compose (Material 3)
- Navigation Compose, ViewModel and StateFlow
- Room (local database, schema exported to `app/schemas/`)
- WorkManager for reminders
- Media3 / ExoPlayer for meditation and background music
- Coil for images
- Wear OS: Compose for Wear OS, Tiles and the Wearable Data Layer

## Project structure

```
app/src/main/java/com/example/clear/
├── data/        Room entities, DAOs, repositories, migrations
├── domain/      Pure logic: baseline and early-warning analysis, levels
├── model/       Static content: categories, exercises, questionnaires, meditations
├── navigation/  Routes and the app's navigation graph
├── playback/    Meditation and music playback
├── reminder/    Reminders, notifications, focus mode, foreground services
└── ui/          Compose screens for each area, shared components and theme
```

All texts live in the code in both languages as `tr("Deutsch", "English")`. `AppLanguage.kt` decides which one is shown.

```
wear/src/main/java/com/example/clear/wear/
├── MainActivity.kt         Running ideas and the "How do you feel now?" rating
├── ClearTileService.kt     The tile
├── PhoneLink.kt            Messages to the phone app
├── DataListenerService.kt  Receives updates from the phone
└── StatusStore.kt          Last known state, cached on the watch
```

On the phone, `watch/WatchSync.kt` sends the state and `watch/WatchMessageService.kt` handles messages from the watch.

## Status

Clear is an early version (1.1.0, MVP). Feedback and ideas are welcome via [Issues](https://github.com/ItsDylan255/Clear/issues).
