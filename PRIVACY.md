# Privacy Policy — Bangla Calendar (`DinKhon`)

**Effective Date:** November 9, 2026
**Application Name:** Bangla Calendar (`DinKhon` • `দিনক্ষণ • বাংলা ক্যালেন্ডার`)
**Application ID:** `dinkhon.sadmanwasi.app`
**Developer:** SadmanWasi

---

## 1. Summary

**Bangla Calendar (`DinKhon`)** is a 100% offline, open-source Android calendar application. It does **not** request any Android system permissions (including no `android.permission.INTERNET`), does **not** collect personal data, does **not** include any analytics, telemetry, crash-reporting, or advertising SDKs, and does **not** transmit any information over the network.

---

## 2. Information Collected

**None.** The application does not collect, access, or process any personal information, device identifiers, location data, contacts, accounts, or usage analytics.

---

## 3. Information Stored Locally on Your Device

To remember your visual and language settings across app restarts, the application stores three non-personal configuration values locally inside your device's private application storage (`SharedPreferences` file `bangla_calendar_prefs` in [`PreferencesRepository.kt`](app/src/main/java/com/banglacalendar/app/data/PreferencesRepository.kt)):

1. **`theme_mode`**: Your selected appearance theme (`SYSTEM`, `LIGHT` / সাদা, or `DARK` / কালো).
2. **`default_calendar_mode`**: Your preferred startup calendar view (`BANGLA` or `ENGLISH`).
3. **`interface_language`**: Your preferred UI label language (`FOLLOW_CALENDAR`, `BANGLA`, or `ENGLISH`).

This data never leaves your device and is permanently deleted when you clear the app's storage or uninstall the application.

---

## 4. Network & External Transmission

- **Zero Network Access:** The app's [`AndroidManifest.xml`](app/src/main/AndroidManifest.xml) requests **zero permissions**—it does not declare `android.permission.INTERNET` or `android.permission.ACCESS_NETWORK_STATE`. The application is technically incapable of making background or foreground network connections.
- **100% Offline Operation:** All Bangla Academy (2019 revision) and Gregorian date calculations, leap-year rules, and holiday lookups run entirely offline on your device.
- **Explicit User-Initiated External Links:** In the **Settings & Information** screen, tapping the **SupportKori** button (`https://www.supportkori.com/sadmanwasi`) or **Facebook** button (`https://www.facebook.com/s4dmanwasi`) launches an explicit Android `ACTION_VIEW` intent handled by your default web browser. No link is ever opened unless you explicitly tap those buttons.
- **Explicit Clipboard Action:** Tapping the **Copy / কপি** button on the Selected Date card copies the formatted Bangla and English date text to your Android system clipboard via `LocalClipboardManager` solely at your request.

---

## 5. Third-Party Services, Analytics, and Advertising

- **No Analytics or Telemetry:** No Firebase, Google Analytics, Sentry, Crashlytics, or other tracking libraries are included.
- **No Proprietary Services:** No Google Play Services (`com.google.android.gms`) dependencies are used.
- **No Advertisements:** The application contains zero advertising frameworks.

---

## 6. Open-Source Verification

Because **Bangla Calendar (`DinKhon`)** is open-source software licensed under the [Apache License, Version 2.0](LICENSE), anyone can inspect the complete source code and verify every claim in this Privacy Policy directly.
