/*
 * Copyright 2026 SadmanWasi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.banglacalendar.app.domain.model

/**
 * Primary calendar representation mode toggled from the home screen header.
 */
enum class CalendarMode {
    BANGLA,
    ENGLISH
}

/**
 * Theme preference setting.
 */
enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

/**
 * Interface language preference for UI labels.
 */
enum class InterfaceLanguage {
    FOLLOW_CALENDAR,
    BANGLA,
    ENGLISH
}
