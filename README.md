# Calendar on da Watch — Wear OS app

Standalone Wear OS (Kotlin + Compose) app for a Galaxy Watch 6.

- Shows today's events on launch, read from Firestore doc
  (`calendar/main`)
- Every night at 23:59, reads **tomorrow's** events and arms an
  alarm for each event
- Re-arms after each boot

<img src="screenshots/schedule.png" width="300">

## Before building

1. **Firestore — set up.** `CalendarRepository.kt`
   matches data:
   ```
   calendar/main
     entries: {
       "2026-6-12": [                 <- NOT zero-padded (year-month-day)
         { id: "...", title: "Work", startTime: "06:30", endTime: "14:30", note: "" },
         ...
       ]
     }
   ```

2. **Firestore read access** The watch app reads only (same as an anonymous visitor ). 
   Check `firestore.rules` — if reads currently require
   authentication, either:
   - requires `request.auth != null` for reads.

3. **`google-services.json`.** In Firebase console for the project, add another app registration — 
   **Android**, package name `com.ledger.calendarwatch` (or whatever 
   `applicationId` is in `app/build.gradle.kts`). Download the
   `google-services.json` and drop it in `app/`.

4. **Package name / app name.** Change `com.ledger.calendarwatch`
   if you want a different package id, and change `app_name` in
   `res/values/strings.xml`.

## Build

Open the `projects_name/` folder in Android Studio (Koala or newer),
let Gradle sync, then:
- run on a Wear OS emulator (Watch6-class, API 34), or
- enable ADB + wireless debugging on watch ( pair via `adb pair <watch-ip>:5555`).

## Known limitations / notes

- **Not a literal entry in Samsung's Clock app.** Full-screen, sound+vibration notification 
  at each event's start time
- **Battery optimization.** "Put unused apps to sleep" / adaptive
  battery can kill background alarms. Exclude this app from battery
  optimization if the night job stops working.
- **Untimed entries** are shown in the list but skipped for alarms
  adjust in `DailyScheduleReceiver` if needed.
