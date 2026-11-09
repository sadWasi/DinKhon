# Complete Technical, Security, Privacy, and F-Droid Release Audit Report

**Project Name:** Bangla Calendar (`DinKhon` • `দিনক্ষণ • বাংলা ক্যালেন্ডার`)
**Application ID (`package`):** `dinkhon.sadmanwasi.app`
**Internal Kotlin Namespace:** `com.banglacalendar.app`
**Version:** `1.0.0` (`versionCode = 10`)
**Copyright Holder:** `SadmanWasi` (Copyright 2026)
**Software Licence:** Apache License, Version 2.0 (`SPDX-License-Identifier: Apache-2.0`)

---

## Phase 1 — Complete Project Audit Findings

| Audit Category | Inspected Value / State | Status |
| :--- | :--- | :--- |
| **Application Name** | `DinKhon` (`দিনক্ষণ • বাংলা ক্যালেন্ডার` / Bangla Calendar) | Verified |
| **Application ID** | `dinkhon.sadmanwasi.app` | Verified |
| **Language & UI Framework** | Kotlin `2.0.21` (`JVM 17`) + Compose Compiler Plugin `2.0.21` + Jetpack Compose (`BOM 2024.10.01`) + Material 3 | Upgraded & Verified |
| **SDK Versions** | `minSdk = 26` (Android 8.0), `targetSdk = 36` (Android 16), `compileSdk = 36` (Android 16) | Upgraded & Verified |
| **Gradle & AGP** | Gradle `8.11.1` (`gradle-8.11.1-bin.zip`) + Android Gradle Plugin `8.9.1` | Upgraded & Verified |
| **Version Name & Code** | `versionName = "1.0.0"`, `versionCode = 10` | Updated & Verified |
| **Bundled Fonts & Assets** | `res/font/noto_sans_bengali.ttf` (`OFL-1.1`), Adaptive + Monochrome XML icons & `mdpi–xxxhdpi` PNGs (`Apache-2.0`) | Verified |
| **Android Permissions** | **Zero permissions** in `AndroidManifest.xml` (no `INTERNET` permission) | Verified |
| **Analytics / Ads / Play Services** | **None** (100% pure AndroidX + Kotlin Stdlib) | Verified |
| **F-Droid `dependenciesInfo` Block** | Was previously default (`true`); updated to `includeInApk = false` and `includeInBundle = false` | Fixed |
| **Release Signing Setup** | Was previously hardcoded to `debug` signing key; updated to read from git-ignored `keystore.properties` or `SIGNING_*` env vars while allowing clean unsigned source builds | Fixed |

---

## Phase 2 — Application Quality & UI/UX Polish Verification

1. **2019 Revised Bangladesh Bangla Academy Calendar Engine (`BanglaCalendarEngine.kt`)**:
   - Verified all 12 month lengths (`31 × 6`, `30 × 5`, and `29/30` in `FALGUN` based on `IsoChronology.INSTANCE.isLeapYear(banglaYear + 594)`).
   - Verified reference dates (`16 Dec 2025 = ১ পৌষ ১৪৩২`, `21 Feb 2026 = ৮ ফাল্গুন ১৪৩২`, `26 Mar 2026 = ১২ চৈত্র ১৪৩২`, `14 Apr 2026 = ১ বৈশাখ ১৪৩৩`, `17 Oct 2026 = ১ কার্তিক ১৪৩৩`).
   - Verified `Asia/Dhaka` (`UTC+6`) timezone handling across UTC midnight boundaries.
   - Verified exhaustive daily round-trip conversions from `1990-01-01` through `2040-12-31` (`18,628` consecutive days).
2. **Final UI & Animation Smoothness Polish**:
   - Removed the placeholder `Icons.Rounded.Person` avatar box in the Settings Creator card (`SettingsAndJumpSheets.kt`) so only `"Sadman Wasi"` is shown on the left alongside the **SupportKori** (`https://www.supportkori.com/sadmanwasi`) and **Facebook** (`https://www.facebook.com/s4dmanwasi`) brand logo buttons on the right.
   - Eliminated 90+ redundant `animateColorAsState` allocations in `SettingsOptionChip` when opening the Date Converter sheet.
   - Added interactive touch-follow drag resistance + `withFrameNanos`-synchronized GPU `graphicsLayer` transitions in `MonthlyCalendarCard.kt`.
   - Added `SmoothFullScreenOverlay` in `BanglaCalendarHomeScreen.kt` so Settings and Date Converter sheets complete their initial composition/layout frame before starting their enter animation clock.

---

## Phase 3 — Security & Privacy Audit

- **Manifest Permissions:** Inspected `app/src/main/AndroidManifest.xml` — zero `<uses-permission>` elements exist.
- **Network Access:** No `INTERNET` permission, no HTTP clients (`OkHttp`, `Retrofit`, `HttpURLConnection`, `WebView`) exist anywhere in the codebase or dependency tree.
- **Local Storage:** Only 3 enum strings (`theme_mode`, `default_calendar_mode`, `interface_language`) are saved in private `SharedPreferences` (`bangla_calendar_prefs`).
- **Secrets Scan:** Scanned all project files for hardcoded passwords, tokens, or API keys — none exist. `local.properties`, `keystore.properties`, and `*.jks`/`*.keystore` are strictly excluded in `.gitignore`.

---

## Phase 4 & 10 — Licensing & F-Droid Compliance Summary

- **Project Licence:** Official unmodified [Apache License, Version 2.0](../LICENSE) applied at the repository root.
- **Source File Headers:** Applied `Copyright 2026 SadmanWasi` and `SPDX-License-Identifier: Apache-2.0` headers across all 25 original `.kt` source/test files, 3 Gradle files, and 8 XML files.
- **Third-Party Audit:** Documented in [`NOTICE`](../NOTICE) and [`THIRD_PARTY_NOTICES.md`](../THIRD_PARTY_NOTICES.md). Every dependency is `Apache-2.0` (or `EPL-1.0` for JUnit 4 test-only), and `noto_sans_bengali.ttf` is `OFL-1.1`.
