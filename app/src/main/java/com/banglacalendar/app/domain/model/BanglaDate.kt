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
import com.banglacalendar.app.domain.engine.BanglaNumeralFormatter
import java.time.DayOfWeek

/**
 * Immutable representation of a date in the official Bangladesh Bangla Academy calendar (2019 revision).
 */
@Immutable
data class BanglaDate(
    val year: Int,
    val month: BanglaMonth,
    val dayOfMonth: Int,
    val dayOfWeek: DayOfWeek
) {
    val season: BanglaSeason
        get() = month.season

    val dayBengali: String
        get() = BanglaNumeralFormatter.toBengaliDigits(dayOfMonth)

    val yearBengali: String
        get() = BanglaNumeralFormatter.toBengaliDigits(year)

    val weekdayBengali: String
        get() = BanglaNumeralFormatter.getBengaliWeekdayFull(dayOfWeek)

    val weekdayEnglish: String
        get() = BanglaNumeralFormatter.getEnglishWeekdayFull(dayOfWeek)

    /**
     * Formats the date in canonical Bengali notation, e.g. "১ পৌষ ১৪৩২".
     */
    fun formatBengali(includeBongabdoSuffix: Boolean = false): String {
        val base = "$dayBengali ${month.bengaliName} $yearBengali"
        return if (includeBongabdoSuffix) "$base বঙ্গাব্দ" else base
    }

    /**
     * Formats the date in English transliteration, e.g. "1 Poush 1432".
     */
    fun formatEnglish(includeBsSuffix: Boolean = false): String {
        val base = "$dayOfMonth ${month.englishName} $year"
        return if (includeBsSuffix) "$base BS" else base
    }

    fun format(useBengali: Boolean, includeEraSuffix: Boolean = false): String =
        if (useBengali) formatBengali(includeEraSuffix) else formatEnglish(includeEraSuffix)
}
