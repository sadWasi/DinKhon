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

/**
 * Represents a Bangla year and month pair (e.g. কার্তিক ১৪৩৩) with month navigation across year boundaries.
 */
@Immutable
data class BanglaYearMonth(
    val year: Int,
    val month: BanglaMonth
) : Comparable<BanglaYearMonth> {

    /**
     * Returns a new [BanglaYearMonth] with [monthsToAdd] added (may be negative),
     * cleanly handling multi-year and single-year boundary transitions.
     */
    fun plusMonths(monthsToAdd: Int): BanglaYearMonth {
        if (monthsToAdd == 0) return this
        val totalMonths = year * 12L + (month.monthNumber - 1) + monthsToAdd
        val newYear = Math.floorDiv(totalMonths, 12L).toInt()
        val newMonthIndex = Math.floorMod(totalMonths, 12L).toInt()
        return BanglaYearMonth(
            year = newYear,
            month = BanglaMonth.entries[newMonthIndex]
        )
    }

    fun minusMonths(monthsToSubtract: Int): BanglaYearMonth = plusMonths(-monthsToSubtract)

    /**
     * Number of months from [this] to [other].
     */
    fun monthsUntil(other: BanglaYearMonth): Int {
        val thisTotal = year * 12 + (month.monthNumber - 1)
        val otherTotal = other.year * 12 + (other.month.monthNumber - 1)
        return otherTotal - thisTotal
    }

    fun formatBengali(includeBongabdoSuffix: Boolean = false): String {
        val base = "${month.bengaliName} ${BanglaNumeralFormatter.toBengaliDigits(year)}"
        return if (includeBongabdoSuffix) "$base বঙ্গাব্দ" else base
    }

    fun formatEnglish(includeBsSuffix: Boolean = false): String {
        val base = "${month.englishName} $year"
        return if (includeBsSuffix) "$base BS" else base
    }

    fun format(useBengali: Boolean, includeEraSuffix: Boolean = false): String =
        if (useBengali) formatBengali(includeEraSuffix) else formatEnglish(includeEraSuffix)

    override fun compareTo(other: BanglaYearMonth): Int {
        val yearCmp = year.compareTo(other.year)
        return if (yearCmp != 0) yearCmp else month.monthNumber.compareTo(other.month.monthNumber)
    }

    companion object {
        fun from(banglaDate: BanglaDate): BanglaYearMonth =
            BanglaYearMonth(year = banglaDate.year, month = banglaDate.month)
    }
}
