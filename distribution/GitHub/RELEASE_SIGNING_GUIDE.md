# Release Signing, Verification & Checksum Guide

**Application Name:** DinKhon (`দিনক্ষণ • বাংলা ক্যালেন্ডার`)
**Application ID:** `dinkhon.sadmanwasi.app`
**Version:** `1.0.0` (`versionCode = 10`)

---

## 1. How Release Signing Is Configured in Gradle

[`app/build.gradle.kts`](../../app/build.gradle.kts) is configured to read release signing credentials safely from **either**:
1. A local, git-ignored `keystore.properties` file in the project root (see [`keystore.properties.example`](../../keystore.properties.example)), **OR**
2. Environment variables (`SIGNING_STORE_FILE`, `SIGNING_STORE_PASSWORD`, `SIGNING_KEY_ALIAS`, `SIGNING_KEY_PASSWORD`).

- **When credentials are provided:** `./gradlew assembleRelease` produces a signed, R8-optimized production release APK (`app/build/outputs/apk/release/app-release.apk`) with Android Signature Schemes **v1, v2, and v3** enabled.
- **When credentials are NOT provided (e.g., CI or F-Droid build servers):** `./gradlew assembleRelease` succeeds cleanly and outputs an unsigned R8-optimized release APK (`app/build/outputs/apk/release/app-release-unsigned.apk`).

---

## 2. Generating Your Private Release Keystore (Requires Your Private Password)

> **IMPORTANT SECURITY RULE:** Never share your `.jks` keystore file or passwords, and never commit them to Git. Back up your `.jks` file and password in a secure password manager (such as KeePassDX or Bitwarden); if you lose your signing key, you cannot publish seamless in-place updates for existing users.

Run the following command in PowerShell or terminal from the project root to generate a 4096-bit RSA release keystore valid for ~30 years:

```powershell
keytool -genkeypair -v `
  -keystore release-keystore.jks `
  -storetype JKS `
  -keyalg RSA `
  -keysize 4096 `
  -validity 10950 `
  -alias dinkhon-release-key `
  -dname "CN=SadmanWasi, O=SadmanWasi, C=BD"
```

Then create `keystore.properties` in the project root (automatically ignored by `.gitignore`):

```properties
storeFile=release-keystore.jks
storePassword=YOUR_CHOSEN_STORE_PASSWORD
keyAlias=dinkhon-release-key
keyPassword=YOUR_CHOSEN_KEY_PASSWORD
```

Now build your signed production release APK and Android App Bundle:

```powershell
.\gradlew.bat assembleRelease bundleRelease
```

---

## 3. Verifying the APK Signature & Generating SHA-256 Checksums

### Verify APK Signature (`apksigner`)
Using the Android SDK `build-tools` `apksigner`:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\build-tools\36.0.0\apksigner.bat" verify --verbose --print-certs app\build\outputs\apk\release\app-release.apk
```

### Generate SHA-256 Checksum
On Windows PowerShell:

```powershell
Get-FileHash -Algorithm SHA256 app\build\outputs\apk\release\app-release.apk
```

On Linux / macOS:

```bash
sha256sum app/build/outputs/apk/release/app-release.apk
```
