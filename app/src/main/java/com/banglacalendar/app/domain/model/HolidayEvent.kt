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

import androidx.compose.runtime.Immutable

/**
 * Category of a Bangladeshi holiday or cultural observance.
 */
enum class HolidayCategory(
    val bengaliLabel: String,
    val englishLabel: String
) {
    NATIONAL_HOLIDAY(
        bengaliLabel = "জাতীয় ও সরকারি ছুটি",
        englishLabel = "National Public Holiday"
    ),
    PUBLIC_HOLIDAY(
        bengaliLabel = "সরকারি ছুটি",
        englishLabel = "Public Holiday"
    ),
    CULTURAL_OBSERVANCE(
        bengaliLabel = "সাংস্কৃতিক ও বিশেষ দিবস",
        englishLabel = "Cultural & Special Day"
    ),
    SUBJECT_TO_MOON_SIGHTING(
        bengaliLabel = "চাঁদ দেখার ওপর নির্ভরশীল ছুটি",
        englishLabel = "Holiday (Subject to Moon Sighting)"
    );

    fun label(useBengali: Boolean): String =
        if (useBengali) bengaliLabel else englishLabel
}

/**
 * Represents a verified holiday or special cultural observance in Bangladesh.
 */
@Immutable
data class HolidayEvent(
    val id: String,
    val titleBengali: String,
    val titleEnglish: String,
    val descriptionBengali: String,
    val descriptionEnglish: String,
    val category: HolidayCategory,
    val isPublicHoliday: Boolean,
    val isMoonSightingDependent: Boolean = false
) {
    fun title(useBengali: Boolean): String =
        if (useBengali) titleBengali else titleEnglish

    fun description(useBengali: Boolean): String =
        if (useBengali) descriptionBengali else descriptionEnglish
}
