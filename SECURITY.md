# Security Policy

## Supported Versions

The following versions of **Bangla Calendar (`DinKhon` — `dinkhon.sadmanwasi.app`)** receive security and maintenance updates:

| Version | Supported |
| :--- | :--- |
| `1.0.x` (`versionCode >= 10`) | :white_check_mark: Yes |
| `< 1.0.0` (`0.1 Alpha` – `0.6 Alpha`) | :x: No (upgrade to `1.0.0+`) |

---

## Security & Attack-Surface Architecture

**Bangla Calendar (`DinKhon`)** is designed with a minimal attack surface:
- **Zero Android Permissions**: The app requests no permissions in `AndroidManifest.xml` (no `INTERNET`, no file storage access, no contacts/calendar/location access).
- **No Network Sockets or WebViews**: All date conversions and UI rendering happen locally in memory.
- **No Exported Internal Components**: Only `MainActivity` (`android.intent.action.MAIN` / `CATEGORY_LAUNCHER`) is exported.
- **Safe External Intent Handling**: External links in the About section (`SupportKori` and `Facebook`) are wrapped in `runCatching` and use standard `LocalUriHandler.openUri(...)` browser intents only when explicitly tapped by the user.

---

## Reporting a Vulnerability

If you discover a security issue or vulnerability:

1. **Please do not open a public issue** if the vulnerability could put users at risk before a fix is released.
2. Report the issue privately to the maintainer (**Sadman Wasi**) via GitHub Security Advisories ("Report a vulnerability" in the repository's **Security** tab) or via the maintainer's contact links in [README.md](README.md).
3. Include steps to reproduce, affected Android versions, and potential impact.
4. We aim to acknowledge reports within **72 hours** and publish a patched release promptly.
