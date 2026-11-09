# Changelog

All notable changes to **Bangla Calendar (`DinKhon` • `দিনক্ষণ • বাংলা ক্যালেন্ডার`)** are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.0.0] (`versionCode = 10`) — 2026-11-09

### Added
- **First Stable Public Release (`1.0.0`)** prepared for GitHub Releases and the official **F-Droid** repository under **Apache License 2.0** (`SPDX-License-Identifier: Apache-2.0`).
- **Interactive Finger-Follow Month Swiping**: Added responsive touch-follow physics coupled with `withFrameNanos`-synchronized GPU `graphicsLayer` transitions in `MonthlyCalendarCard`.
- **Frame-Synchronized Full-Screen Overlays**: Added `SmoothFullScreenOverlay` in `BanglaCalendarHomeScreen` so opening and closing the Settings and Date Converter sheets completes initial layout before starting the animation clock.
- **F-Droid & Reproducible Build Readiness**: Disabled `dependenciesInfo` (`includeInApk = false`, `includeInBundle = false`) in `app/build.gradle.kts`, added Fastlane metadata (`en-US` and `bn-BD`), and added open-source licensing and privacy documentation (`LICENSE`, `NOTICE`, `PRIVACY.md`, `THIRD_PARTY_NOTICES.md`, `SECURITY.md`, `CONTRIBUTING.md`).
- **Expanded Unit Test Suite**: Added comprehensive unit tests for all 12 Bangla month start dates, century leap-year rules (`2000` vs. `2100`), and `Asia/Dhaka` (`UTC+6`) midnight boundary detection.

### Changed
- **Refined Creator Section in Settings**: Removed the placeholder person avatar box beside **Sadman Wasi** so only the creator name is shown on the left alongside the **SupportKori** (`https://www.supportkori.com/sadmanwasi`) and **Facebook** (`https://www.facebook.com/s4dmanwasi`) brand logo buttons on the right.
- **Optimized Option Chip Composition**: Removed redundant per-chip `animateColorAsState` allocations in `SettingsOptionChip`, eliminating 90+ `Animatable` instances when opening the Date Converter sheet.

---

## [0.6 Alpha] (`versionCode = 6`) — 2026-11-09

### Added
- Updated application package ID (`applicationId`) to `dinkhon.sadmanwasi.app`.
- Added **SupportKori** creator support button (`https://www.supportkori.com/sadmanwasi`) with custom vector SupportKori logo beside the Facebook button.
- Redesigned universal adaptive launcher icon (`ic_launcher_foreground.xml`, `ic_launcher_background.xml`) with Android 13+ Material You `<monochrome>` themed icon support (`ic_launcher_monochrome.xml`) and multi-density PNGs (`mdpi` through `xxxhdpi`).

---

## [0.2 Alpha] (`versionCode = 2`) — 2026-11-09

### Added
- Added short, zero-recomposition startup emblem intro animation.
- Added pre-warmed adjacent month grid cache in `CalendarViewModel` and Compose compiler stability configuration (`compose_stability.conf`).
- Unified Settings and Date Converter into full-screen edge-to-edge Liquid Glass overlays with identical geometry in both Light (`সাদা`) and Dark (`কালো`) themes.

---

## [0.1 Alpha] (`versionCode = 1`) — 2026-11-09

### Added
- Initial implementation of **DinKhon (`দিনক্ষণ • বাংলা ক্যালেন্ডার`)** featuring the 2019 Revised Bangladesh Bangla Academy Calendar engine (`BanglaCalendarEngine`), instant two-way **বাংলা | English** mode toggle, Apple-inspired Liquid Glass UI, `Asia/Dhaka` timezone handling, and offline Bangladesh holiday repository.
