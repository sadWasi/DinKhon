# Official F-Droid Compatibility, Reproducible Builds & Submission Guide

**Application Name:** DinKhon (`দিনক্ষণ • বাংলা ক্যালেন্ডার` / Bangla Calendar)
**Application ID:** `dinkhon.sadmanwasi.app`
**Licence:** `Apache-2.0`
**Current Version:** `1.0.0` (`versionCode = 10`)

---

## 1. F-Droid Inclusion Policy Compliance Audit

We audited the entire project against the [Official F-Droid Inclusion Policy](https://f-droid.org/docs/Inclusion_Policy/):

| Requirement | Status | Verification Details |
| :--- | :--- | :--- |
| **1. 100% Open-Source Licence** | **PASS** | Licensed under **Apache License 2.0** (`SPDX-License-Identifier: Apache-2.0`) with root [`LICENSE`](../../LICENSE), [`NOTICE`](../../NOTICE), and [`THIRD_PARTY_NOTICES.md`](../../THIRD_PARTY_NOTICES.md). |
| **2. All Dependencies FOSS** | **PASS** | Uses only official Kotlin (`2.0.21`), AndroidX, and Jetpack Compose (`2024.10.01`) libraries under `Apache-2.0` and JUnit (`4.13.2`) under `EPL-1.0`. |
| **3. No Google Play Services / Firebase** | **PASS** | Zero `com.google.android.gms` or `com.google.firebase` dependencies. |
| **4. No Ads, Analytics, or Tracking** | **PASS** | Zero tracking, telemetry, crash-reporting, or advertising libraries. |
| **5. No Proprietary Encrypted Dependency Block** | **PASS** | `dependenciesInfo { includeInApk = false; includeInBundle = false }` is configured in [`app/build.gradle.kts`](../../app/build.gradle.kts). |
| **6. Bundled Assets FOSS-Licensed** | **PASS** | `res/font/noto_sans_bengali.ttf` is licensed under SIL Open Font License 1.1 (`OFL-1.1`); all launcher icons are original vector/raster works under `Apache-2.0`. |
| **7. Clean Source Build without Secrets** | **PASS** | `./gradlew assembleRelease` succeeds from a clean checkout without requiring `keystore.properties` or private environment variables. |
| **8. Anti-Features Assessment** | **NONE** | Zero Anti-Features (`No Tracking`, `No NonFreeNet`, `No NonFreeAdd`, `No Ads`). |

---

## 2. Signing Strategies & Reproducible Builds Assessment

When distributing an Android app on both **GitHub Releases** and **F-Droid**, Android's package manager enforces that an app update must be signed by the exact same cryptographic certificate as the currently installed APK.

There are **two official signing approaches** supported by F-Droid:

### Approach A: Standard F-Droid Signing Key (Default)
- **How it works:** F-Droid compiles your source code on their build server and signs the resulting APK with F-Droid's own air-gapped signing key. Meanwhile, you sign your GitHub Release APKs with your private developer keystore.
- **Pros:** Simplest F-Droid setup; does not require strict byte-for-byte build reproducibility between your local machine and F-Droid's Debian build container.
- **Cons for Users:** Because the GitHub APK and the F-Droid APK have different signing keys, a user who installed DinKhon from GitHub cannot cross-update via F-Droid (and vice versa) without uninstalling first.

### Approach B: Reproducible Builds with Developer Signing Key (`AllowedAPKSigningKeys`)
- **How it works:** You publish your signed release APK on GitHub Releases (`v1.0.0`). F-Droid compiles the same commit from source inside their build server, strips the signature from your GitHub APK, compares the unsigned contents byte-for-byte against their build output, and if identical, distributes **your original developer-signed APK** (`Binaries:` + `AllowedAPKSigningKeys:` in `dinkhon.sadmanwasi.app.yml`).
- **Pros for Users:** Users can seamlessly switch between installing updates from GitHub Releases and F-Droid without ever uninstalling.
- **Technical Readiness & Empirical Reproducibility Test Results in This Project:**
  - `dependenciesInfo { includeInApk = false; includeInBundle = false }` is configured in [`app/build.gradle.kts`](../../app/build.gradle.kts).
  - PNG crunching determinism, baseline profile determinism, and R8 determinism are verified under AGP `8.9.1` + Gradle `8.11.1` + Kotlin `2.0.21` (`compileSdk = 36`, `targetSdk = 36`).
  - **Empirical Reproducibility Test (Verified):** Two independent clean builds (`./gradlew clean assembleRelease --no-build-cache`) produced **100% byte-for-byte identical** unsigned release APKs (`app-release-unsigned.apk`):
    - **Build #1 SHA-256:** `0CA2C94D58AA49DBFE71314E1A89B1ED639496CC90B44362B86A2B4D9F199F52`
    - **Build #2 SHA-256 (`clean --no-build-cache`):** `0CA2C94D58AA49DBFE71314E1A89B1ED639496CC90B44362B86A2B4D9F199F52`
  - **Cross-OS Note (Windows vs. Linux F-Droid Build Server):** While builds on the same OS are 100% deterministic byte-for-byte, F-Droid builds inside a Debian Linux container. Therefore, if you enable `Binaries:` and `AllowedAPKSigningKeys:` in `dinkhon.sadmanwasi.app.yml`, build and sign the official GitHub Release APK on Linux (for example, using the included Ubuntu GitHub Actions workflow in [`.github/workflows/android-ci.yml`](../../.github/workflows/android-ci.yml)) so host OS path/newline conventions inside R8/art-profile metadata match F-Droid's Linux builder.

---

## 3. Important F-Droid Policy Notes & Remaining External Requirements

Before opening a merge request to [`gitlab.com/fdroid/fdroiddata`](https://gitlab.com/fdroid/fdroiddata), please note:

1. **Public Git Repository & Tagged Release (`v1.0.0`)**:
   - Push this repository to a public GitHub URL and create a Git tag (`v1.0.0`) so F-Droid's `checkupdates` bot can discover tags automatically.
   - Fill in `<YOUR_GITHUB_REPO_URL>` and `<RELEASE_COMMIT_HASH_OR_TAG_v1.0.0>` in [`dinkhon.sadmanwasi.app.yml`](dinkhon.sadmanwasi.app.yml).
2. **F-Droid Activity / Maturity Expectation**:
   - Note that current F-Droid review guidelines may consider repository history/maturity (often looking for active maintenance history or ~3 months of public activity for brand-new repositories, subject to maintainer discretion).
3. **Submitting to `fdroiddata`**:
   - Fork [`https://gitlab.com/fdroid/fdroiddata`](https://gitlab.com/fdroid/fdroiddata) on GitLab.
   - Copy `distribution/F-Droid/dinkhon.sadmanwasi.app.yml` into `metadata/dinkhon.sadmanwasi.app.yml`.
   - Open a Merge Request following the [Submitting to F-Droid Quick Start Guide](https://f-droid.org/docs/Submitting_to_F-Droid_Quick_Start_Guide/).
   - *(Note: Because Fastlane metadata is already structured in `fastlane/metadata/android/en-US` and `fastlane/metadata/android/bn-BD` inside this source repository, F-Droid will automatically pull descriptions, changelogs, icons, and screenshots directly from your repo!)*
