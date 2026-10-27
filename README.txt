YAAD RAKHO - Native Android source project (v1 prototype)

HOW TO BUILD:
1. Open this folder in Android Studio (with Android SDK 35 installed).
2. Allow Gradle sync (internet needed for Gradle plugin).
3. Choose Build > Build APK(s).
4. Install app/build/outputs/apk/debug/app-debug.apk on Android.
5. Allow Notifications, Alarms & reminders, and full-screen notifications where available.

Features: offline reminder storage, date/time, once/daily/weekly/monthly/yearly repeats, voice dictation through Android recognizer, exact AlarmClock scheduling, full-screen alarm notification, bell -> TTS -> bell loop, snooze, done, boot restore, delete.

LIMITATIONS:
- This ZIP is Android Studio source, NOT a ready-to-install APK.
- Android OS may restrict automatic full-screen presentation and voice recognition may need network depending on installed speech service.
- Urdu text-to-speech requires an installed Urdu-capable voice; fallback may pronounce poorly.
- The prototype has not been compiled or device-tested in this environment.
- Snooze on a recurring reminder temporarily shifts the scheduled time; a production release should preserve original recurrence separately.
- No cloud sync or backup/export in this prototype.
