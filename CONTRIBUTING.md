# Contributing to Bangla Calendar (`DinKhon`)

Thank you for your interest in contributing to **Bangla Calendar (`DinKhon` • `দিনক্ষণ • বাংলা ক্যালেন্ডার`)**!

---

## 1. Core Project Principles

Every contribution must uphold the core principles of the project:

1. **100% Offline & Privacy-First**:
   - Never add `android.permission.INTERNET` or any other unnecessary Android permission.
   - Never add analytics, telemetry, crash reporting, advertising, or proprietary SDKs (such as Google Play Services or Firebase).
   - Maintain 100% compliance with the [Official F-Droid Inclusion Policy](https://f-droid.org/docs/Inclusion_Policy/).
2. **Official 2019 Revised Bangladesh Bangla Academy Calendar Accuracy**:
   - Months **বৈশাখ through আশ্বিন** (first 6 months): **31 days** each.
   - Months **কার্তিক, অগ্রহায়ণ, পৌষ, মাঘ, and চৈত্র**: **30 days** each.
   - Month **ফাল্গুন (Falgun)**: **29 days** in a standard year, and **30 days** when the Gregorian year containing February (`BanglaYear + 594`) is a leap year.
   - **পহেলা বৈশাখ (1 Boishakh)** always begins on **14 April**.
   - Default timezone for today's date detection must remain `Asia/Dhaka` (`UTC+6`).
3. **Performance & Liquid Glass Design**:
   - Keep animations inside `Modifier.graphicsLayer` synchronized with `withFrameNanos` so 60Hz/120Hz frame rates are preserved even on entry-level Android devices.
   - Maintain `@Immutable` annotations and `app/compose_stability.conf` so Jetpack Compose composables remain skippable.

---

## 2. Development Setup & Verification

### Prerequisites
- **JDK 17**
- **Android SDK Platform 34** (`compileSdk = 34`, `buildToolsVersion = 34.0.0`)

### Running Unit Tests
Before opening a pull request, run the full unit test suite:

```bash
./gradlew testDebugUnitTest
```

### Verifying UI Test Compilation & Lint
```bash
./gradlew compileDebugAndroidTestKotlin lintDebug
```

### Building Debug & Unsigned Release APKs
```bash
./gradlew assembleDebug assembleRelease
```

---

## 3. Licensing of Contributions

By submitting a pull request or contribution to this repository, you agree that your contribution will be licensed under the project's [Apache License, Version 2.0](LICENSE) (`SPDX-License-Identifier: Apache-2.0`).

All new original source files must include the standard Apache 2.0 header at the top of the file.
