# Third-Party Notices & Licence Audit

**Project:** Bangla Calendar (`DinKhon` • `দিনক্ষণ • বাংলা ক্যালেন্ডার`)
**Application ID:** `dinkhon.sadmanwasi.app`
**Project Copyright:** Copyright 2026 SadmanWasi
**Project Licence:** Apache License, Version 2.0 (`SPDX-License-Identifier: Apache-2.0`)

This document records the complete licence audit of all third-party libraries, build plugins, fonts, and graphic assets used in **Bangla Calendar**. Third-party components remain under their respective original authors' copyrights and licences and are not relicensed under the project owner's copyright.

---

## 1. Runtime & Compile Dependencies

All Android runtime and compile dependencies are open-source software distributed via Maven Central and Google's Maven repository under **Apache-2.0** (100% compatible with both commercial redistribution and the [Official F-Droid Inclusion Policy](https://f-droid.org/docs/Inclusion_Policy/)).

| Component / Artifact | Version | Publisher / Author | Licence | Official Licence URL | Commercial Redistribution | F-Droid Compatible |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `org.jetbrains.kotlin:kotlin-stdlib` | `2.0.21` | JetBrains s.r.o. | Apache-2.0 | https://www.apache.org/licenses/LICENSE-2.0 | Yes | Yes |
| `org.jetbrains.kotlinx:kotlinx-coroutines-android` | `1.8.1` (via Lifecycle/Compose) | JetBrains s.r.o. | Apache-2.0 | https://www.apache.org/licenses/LICENSE-2.0 | Yes | Yes |
| `androidx.compose:compose-bom` | `2024.10.01` | The Android Open Source Project (Google) | Apache-2.0 | https://www.apache.org/licenses/LICENSE-2.0 | Yes | Yes |
| `androidx.core:core-ktx` | `1.15.0` | The Android Open Source Project | Apache-2.0 | https://www.apache.org/licenses/LICENSE-2.0 | Yes | Yes |
| `androidx.lifecycle:lifecycle-runtime-ktx` | `2.8.7` | The Android Open Source Project | Apache-2.0 | https://www.apache.org/licenses/LICENSE-2.0 | Yes | Yes |
| `androidx.lifecycle:lifecycle-runtime-compose` | `2.8.7` | The Android Open Source Project | Apache-2.0 | https://www.apache.org/licenses/LICENSE-2.0 | Yes | Yes |
| `androidx.lifecycle:lifecycle-viewmodel-compose` | `2.8.7` | The Android Open Source Project | Apache-2.0 | https://www.apache.org/licenses/LICENSE-2.0 | Yes | Yes |
| `androidx.activity:activity-compose` | `1.9.3` | The Android Open Source Project | Apache-2.0 | https://www.apache.org/licenses/LICENSE-2.0 | Yes | Yes |
| `androidx.compose.ui:ui` | `1.7.4` (via BOM) | The Android Open Source Project | Apache-2.0 | https://www.apache.org/licenses/LICENSE-2.0 | Yes | Yes |
| `androidx.compose.ui:ui-graphics` | `1.7.4` (via BOM) | The Android Open Source Project | Apache-2.0 | https://www.apache.org/licenses/LICENSE-2.0 | Yes | Yes |
| `androidx.compose.ui:ui-tooling-preview` | `1.7.4` (via BOM) | The Android Open Source Project | Apache-2.0 | https://www.apache.org/licenses/LICENSE-2.0 | Yes | Yes |
| `androidx.compose.foundation:foundation` | `1.7.4` (via BOM) | The Android Open Source Project | Apache-2.0 | https://www.apache.org/licenses/LICENSE-2.0 | Yes | Yes |
| `androidx.compose.animation:animation` | `1.7.4` (via BOM) | The Android Open Source Project | Apache-2.0 | https://www.apache.org/licenses/LICENSE-2.0 | Yes | Yes |
| `androidx.compose.material3:material3` | `1.3.0` (via BOM) | The Android Open Source Project | Apache-2.0 | https://www.apache.org/licenses/LICENSE-2.0 | Yes | Yes |
| `androidx.compose.material:material-icons-extended` | `1.7.4` (via BOM) | The Android Open Source Project | Apache-2.0 | https://www.apache.org/licenses/LICENSE-2.0 | Yes | Yes |

---

## 2. Test & Build-Tool Dependencies

| Component / Artifact | Version | Publisher / Author | Licence | Official Licence URL | Commercial Redistribution | F-Droid Compatible |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `junit:junit` | `4.13.2` | JUnit Team | EPL-1.0 | https://www.eclipse.org/legal/epl-v10.html | Yes | Yes |
| `androidx.test.ext:junit` | `1.2.1` | The Android Open Source Project | Apache-2.0 | https://www.apache.org/licenses/LICENSE-2.0 | Yes | Yes |
| `androidx.test.espresso:espresso-core` | `3.6.1` | The Android Open Source Project | Apache-2.0 | https://www.apache.org/licenses/LICENSE-2.0 | Yes | Yes |
| `androidx.compose.ui:ui-test-junit4` | `1.7.4` (via BOM) | The Android Open Source Project | Apache-2.0 | https://www.apache.org/licenses/LICENSE-2.0 | Yes | Yes |
| `com.android.application` (Android Gradle Plugin) | `8.9.1` | The Android Open Source Project | Apache-2.0 | https://www.apache.org/licenses/LICENSE-2.0 | Yes | Yes |
| `org.jetbrains.kotlin.android` (Kotlin Gradle Plugin) | `2.0.21` | JetBrains s.r.o. | Apache-2.0 | https://www.apache.org/licenses/LICENSE-2.0 | Yes | Yes |
| `org.jetbrains.kotlin.plugin.compose` (Compose Compiler Gradle Plugin) | `2.0.21` | JetBrains s.r.o. | Apache-2.0 | https://www.apache.org/licenses/LICENSE-2.0 | Yes | Yes |
| Gradle Wrapper (`gradle-8.11.1-bin.zip`) | `8.11.1` | Gradle, Inc. | Apache-2.0 | https://www.apache.org/licenses/LICENSE-2.0 | Yes | Yes |

---

## 3. Bundled Fonts, Icons, and Graphic Assets

| Asset File | Type | Author / Source | Licence | Official Licence URL | Commercial & F-Droid Redistribution |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `app/src/main/res/font/noto_sans_bengali.ttf` | Variable TrueType Font (`Noto Sans Bengali`) | The Noto Project Authors (Google) | SIL Open Font License 1.1 (`OFL-1.1`) | https://openfontlicense.org | Permitted when bundled with software |
| `app/src/main/res/drawable/ic_launcher_background.xml` | Android Vector Drawable | Original work by SadmanWasi (2026) | Apache-2.0 | https://www.apache.org/licenses/LICENSE-2.0 | Yes |
| `app/src/main/res/drawable/ic_launcher_foreground.xml` | Android Vector Drawable (uses glyph outline from `Noto Sans Bengali` under `OFL-1.1`) | Original composition by SadmanWasi (2026) | Apache-2.0 / OFL-1.1 | https://www.apache.org/licenses/LICENSE-2.0 | Yes |
| `app/src/main/res/drawable/ic_launcher_monochrome.xml` | Android Vector Drawable | Original composition by SadmanWasi (2026) | Apache-2.0 / OFL-1.1 | https://www.apache.org/licenses/LICENSE-2.0 | Yes |
| `app/src/main/res/mipmap-*/ic_launcher*.png` | Pre-rendered Launcher Icons | Original work by SadmanWasi (2026) | Apache-2.0 | https://www.apache.org/licenses/LICENSE-2.0 | Yes |

---

## 4. Branding & Visual Identity Notice

Per Section 6 ("Trademarks") of the [Apache License, Version 2.0](LICENSE):

> This License does not grant permission to use the trade names, trademarks, service marks, or product names of the Licensor, except as required for reasonable and customary use in describing the origin of the Work and reproducing the content of the NOTICE file.

Specifically:
- **Application Name:** `Bangla Calendar` / `DinKhon` (`দিনক্ষণ • বাংলা ক্যালেন্ডার`)
- **Application Package ID:** `dinkhon.sadmanwasi.app`
- **Original Application Logo & Brand Artwork:** Located in `app/src/main/res/drawable/ic_launcher*.xml` and `app/src/main/res/mipmap-*/ic_launcher*.png`

While the source code and covered assets are freely reusable under Apache-2.0 (including redistribution by official FOSS repositories such as F-Droid that build the unmodified upstream source), modified third-party forks distributed publicly as separate products should use a distinct name and application ID so as not to misrepresent themselves as the official release by **SadmanWasi**.

---

## 5. SIL Open Font License 1.1 (For `noto_sans_bengali.ttf`)

```text
Copyright 2012-2023 The Noto Project Authors (https://github.com/notofonts/bengali)

This Font Software is licensed under the SIL Open Font License, Version 1.1.
This license is copied below, and is also available with a FAQ at:
https://openfontlicense.org

-----------------------------------------------------------
SIL OPEN FONT LICENSE Version 1.1 - 26 February 2007
-----------------------------------------------------------

PREAMBLE
The goals of the Open Font License (OFL) are to stimulate worldwide
development of collaborative font projects, to support the font creation
efforts of academic and linguistic communities, and to provide a free and
open framework in which fonts may be shared and improved in partnership
with others.

The OFL allows the licensed fonts to be used, studied, modified and
redistributed freely as long as they are not sold by themselves. The
fonts, including any derivative works, can be bundled, embedded,
redistributed and/or sold with any software provided that any reserved
names are not used by derivative works. The fonts and derivatives,
however, cannot be released under any other type of license. The
requirement for fonts to remain under this license does not apply
to any document created using the fonts or their derivatives.

DEFINITIONS
"Font Software" refers to the set of files released by the Copyright
Holder(s) under this license and clearly marked as such. This may
include source files, build scripts and documentation.

"Reserved Font Name" refers to any names specified as such after the
copyright statement(s).

"Original Version" refers to the collection of Font Software components as
distributed by the Copyright Holder(s).

"Modified Version" refers to any derivative made by adding to, deleting,
or substituting -- in part or in whole -- any of the components of the
Original Version, by changing formats or by porting the Font Software to a
new environment.

"Author" refers to any designer, engineer, programmer, technical
writer or other person who contributed to the Font Software.

PERMISSION & CONDITIONS
Permission is hereby granted, free of charge, to any person obtaining
a copy of the Font Software, to use, study, copy, merge, embed, modify,
redistribute, and sell modified and unmodified copies of the Font
Software, subject to the following conditions:

1) Neither the Font Software nor any of its individual components,
in Original or Modified Versions, may be sold by itself.

2) Original or Modified Versions of the Font Software may be bundled,
redistributed and/or sold with any software, provided that each copy
contains the above copyright notice and this license. These can be
included either as stand-alone text files, human-readable headers or
in the appropriate machine-readable metadata fields within text or
binary files as long as those fields can be easily viewed by the user.

3) No Modified Version of the Font Software may use the Reserved Font
Name(s) unless explicit written permission is granted by the corresponding
Copyright Holder. This restriction only applies to the primary font name as
presented to the users.

4) The name(s) of the Copyright Holder(s) or the Author(s) of the Font
Software shall not be used to promote, endorse or advertise any
Modified Version, except to acknowledge the contribution(s) of the
Copyright Holder(s) and the Author(s) or with their explicit written
permission.

5) The Font Software, modified or unmodified, in part or in whole,
must be distributed entirely under this license, and must not be
distributed under any other license. The requirement for fonts to
remain under this license does not apply to any document created
using the Font Software.

TERMINATION
This license becomes null and void if any of the above conditions are
not met.

DISCLAIMER
THE FONT SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO ANY WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT
OF COPYRIGHT, PATENT, TRADEMARK, OR OTHER RIGHT. IN NO EVENT SHALL THE
COPYRIGHT HOLDER BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
INCLUDING ANY GENERAL, SPECIAL, INDIRECT, INCIDENTAL, OR CONSEQUENTIAL
DAMAGES, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF THE USE OR IN CONNECTION WITH THE FONT SOFTWARE OR THE
USE OR OTHER DEALINGS IN THE FONT SOFTWARE.
```
