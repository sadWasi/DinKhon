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
package com.banglacalendar.app.domain.engine

import com.banglacalendar.app.domain.model.BanglaDate
import com.banglacalendar.app.domain.model.BanglaMonth
import com.banglacalendar.app.domain.model.BanglaYearMonth
import com.banglacalendar.app.domain.model.CalendarDayCell
import com.banglacalendar.app.domain.model.CalendarMode
import com.banglacalendar.app.domain.model.HolidayEvent
import java.time.Clock
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * Pure, offline-first calculation engine implementing the official Bangladesh
 * Bangla Academy revised calendar (2019 revision).
 *
 * Official Rules:
 * - Boishakh to Ashwin (first 6 months): 31 days each
 * - Kartik to Magh (next 4 months): 30 days each
 * - Falgun (11th month): 29 days, or 30 days when the Gregorian year containing February is a leap year
 * - Choitro (12th month): 30 days
 * - Pohela Boishakh (1 Boishakh) always begins on April 14.
 */
object BanglaCalendarEngine {

    val DHAKA_ZONE: ZoneId = ZoneId.of("Asia/Dhaka")

    private const val BANGLA_ERA_OFFSET_FROM_APRIL_14 = 593
    private const val BANGLA_ERA_OFFSET_BEFORE_APRIL_14 = 594

    /**
     * Returns today's [LocalDate] in the `Asia/Dhaka` timezone.
     */
    fun today(clock: Clock = Clock.system(DHAKA_ZONE)): LocalDate =
        LocalDate.now(clock.withZone(DHAKA_ZONE))

    /**
     * Returns true if the given Gregorian year is a leap year according to ISO-8601 rules.
     */
    fun isGregorianLeapYear(gregorianYear: Int): Boolean =
        Year.isLeap(gregorianYear.toLong())

    /**
     * Returns true if [banglaYear] (বঙ্গাব্দ) has a 30-day Falgun.
     *
     * Because 1 Boishakh of [banglaYear] begins on April 14 of Gregorian year `(banglaYear + 593)`,
     * the month of Falgun (Feb 14 – Mar 14) falls in Gregorian year `(banglaYear + 594)`.
     * Therefore, Falgun has 30 days if and only if Gregorian year `(banglaYear + 594)` is a leap year.
     */
    fun isBanglaLeapYear(banglaYear: Int): Boolean =
        isGregorianLeapYear(banglaYear + BANGLA_ERA_OFFSET_BEFORE_APRIL_14)

    /**
     * Returns the number of days in [month] for the given [banglaYear] (বঙ্গাব্দ).
     */
    fun getDaysInBanglaMonth(banglaYear: Int, month: BanglaMonth): Int {
        return if (month == BanglaMonth.FALGUN && isBanglaLeapYear(banglaYear)) {
            30
        } else {
            month.baseDays
        }
    }

    /**
     * Returns the total number of days in [banglaYear] (365 in normal years, 366 in leap years).
     */
    fun getDaysInBanglaYear(banglaYear: Int): Int =
        if (isBanglaLeapYear(banglaYear)) 366 else 365

    /**
     * Converts a Gregorian [LocalDate] to its exact [BanglaDate] in the
     * 2019 revised Bangladesh Bangla Academy calendar.
     */
    fun gregorianToBangla(date: LocalDate): BanglaDate {
        val pohelaBoishakhInSameGregorianYear = LocalDate.of(date.year, Month.APRIL, 14)

        val banglaYear: Int
        val startOfBanglaYear: LocalDate

        if (!date.isBefore(pohelaBoishakhInSameGregorianYear)) {
            banglaYear = date.year - BANGLA_ERA_OFFSET_FROM_APRIL_14
            startOfBanglaYear = pohelaBoishakhInSameGregorianYear
        } else {
            banglaYear = date.year - BANGLA_ERA_OFFSET_BEFORE_APRIL_14
            startOfBanglaYear = LocalDate.of(date.year - 1, Month.APRIL, 14)
        }

        var remainingDays = ChronoUnit.DAYS.between(startOfBanglaYear, date).toInt()
        var resolvedMonth = BanglaMonth.BOISHAKH

        for (month in BanglaMonth.entries) {
            val monthDays = getDaysInBanglaMonth(banglaYear, month)
            if (remainingDays < monthDays) {
                resolvedMonth = month
                break
            }
            remainingDays -= monthDays
        }

        return BanglaDate(
            year = banglaYear,
            month = resolvedMonth,
            dayOfMonth = remainingDays + 1,
            dayOfWeek = date.dayOfWeek
        )
    }

    /**
     * Converts a Bangla date ([banglaYear], [month], [dayOfMonth]) to its exact Gregorian [LocalDate].
     *
     * @throws IllegalArgumentException if [dayOfMonth] is out of valid range for [month] in [banglaYear].
     */
    fun banglaToGregorian(banglaYear: Int, month: BanglaMonth, dayOfMonth: Int): LocalDate {
        val maxDays = getDaysInBanglaMonth(banglaYear, month)
        require(dayOfMonth in 1..maxDays) {
            "Invalid dayOfMonth $dayOfMonth for ${month.englishName} $banglaYear (valid: 1..$maxDays)"
        }

        val startGregorianYear = banglaYear + BANGLA_ERA_OFFSET_FROM_APRIL_14
        val startOfBanglaYear = LocalDate.of(startGregorianYear, Month.APRIL, 14)

        var daysFromYearStart = 0L
        for (m in BanglaMonth.entries) {
            if (m == month) break
            daysFromYearStart += getDaysInBanglaMonth(banglaYear, m)
        }
        daysFromYearStart += (dayOfMonth - 1)

        return startOfBanglaYear.plusDays(daysFromYearStart)
    }

    /**
     * Converts a [BanglaDate] to its exact Gregorian [LocalDate].
     */
    fun banglaToGregorian(banglaDate: BanglaDate): LocalDate =
        banglaToGregorian(banglaDate.year, banglaDate.month, banglaDate.dayOfMonth)

    /**
     * Returns the Gregorian [LocalDate] of the 1st day of [banglaYearMonth].
     */
    fun getFirstDayOfBanglaMonth(banglaYearMonth: BanglaYearMonth): LocalDate =
        banglaToGregorian(banglaYearMonth.year, banglaYearMonth.month, 1)

    /**
     * Returns the Gregorian [LocalDate] of the last day of [banglaYearMonth].
     */
    fun getLastDayOfBanglaMonth(banglaYearMonth: BanglaYearMonth): LocalDate {
        val daysInMonth = getDaysInBanglaMonth(banglaYearMonth.year, banglaYearMonth.month)
        return banglaToGregorian(banglaYearMonth.year, banglaYearMonth.month, daysInMonth)
    }

    /**
     * Computes the zero-based Sunday-first column index (0 = Sunday .. 6 = Saturday) for [dayOfWeek].
     */
    fun sundayFirstColumnIndex(dayOfWeek: DayOfWeek): Int = when (dayOfWeek) {
        DayOfWeek.SUNDAY -> 0
        DayOfWeek.MONDAY -> 1
        DayOfWeek.TUESDAY -> 2
        DayOfWeek.WEDNESDAY -> 3
        DayOfWeek.THURSDAY -> 4
        DayOfWeek.FRIDAY -> 5
        DayOfWeek.SATURDAY -> 6
    }

    /**
     * Generates a 42-cell (6 rows × 7 columns, Sunday through Saturday) calendar month grid
     * for a Bangla month [banglaYearMonth].
     */
    fun generateBanglaMonthGrid(
        banglaYearMonth: BanglaYearMonth,
        today: LocalDate = today(),
        holidayProvider: (LocalDate, BanglaDate) -> List<HolidayEvent> = { _, _ -> emptyList() }
    ): List<CalendarDayCell> {
        val firstDayGregorian = getFirstDayOfBanglaMonth(banglaYearMonth)
        val leadingDaysCount = sundayFirstColumnIndex(firstDayGregorian.dayOfWeek)
        val gridStartGregorian = firstDayGregorian.minusDays(leadingDaysCount.toLong())

        return (0 until 42).map { index ->
            val cellGregorian = gridStartGregorian.plusDays(index.toLong())
            val cellBangla = gregorianToBangla(cellGregorian)
            val isCurrentMonth =
                cellBangla.year == banglaYearMonth.year && cellBangla.month == banglaYearMonth.month
            CalendarDayCell(
                gregorianDate = cellGregorian,
                banglaDate = cellBangla,
                isCurrentMonth = isCurrentMonth,
                isToday = cellGregorian == today,
                isWeekend = BanglaNumeralFormatter.isBangladeshWeekend(cellGregorian.dayOfWeek),
                holidays = holidayProvider(cellGregorian, cellBangla)
            )
        }
    }

    /**
     * Generates a 42-cell (6 rows × 7 columns, Sunday through Saturday) calendar month grid
     * for a Gregorian month [gregorianYearMonth].
     */
    fun generateGregorianMonthGrid(
        gregorianYearMonth: YearMonth,
        today: LocalDate = today(),
        holidayProvider: (LocalDate, BanglaDate) -> List<HolidayEvent> = { _, _ -> emptyList() }
    ): List<CalendarDayCell> {
        val firstDayGregorian = gregorianYearMonth.atDay(1)
        val leadingDaysCount = sundayFirstColumnIndex(firstDayGregorian.dayOfWeek)
        val gridStartGregorian = firstDayGregorian.minusDays(leadingDaysCount.toLong())

        return (0 until 42).map { index ->
            val cellGregorian = gridStartGregorian.plusDays(index.toLong())
            val cellBangla = gregorianToBangla(cellGregorian)
            val isCurrentMonth = YearMonth.from(cellGregorian) == gregorianYearMonth
            CalendarDayCell(
                gregorianDate = cellGregorian,
                banglaDate = cellBangla,
                isCurrentMonth = isCurrentMonth,
                isToday = cellGregorian == today,
                isWeekend = BanglaNumeralFormatter.isBangladeshWeekend(cellGregorian.dayOfWeek),
                holidays = holidayProvider(cellGregorian, cellBangla)
            )
        }
    }

    /**
     * Returns a formatted subtitle showing the corresponding Gregorian month/year span
     * for a given [banglaYearMonth] (e.g. "Oct – Nov 2026" or "অক্টোবর – নভেম্বর ২০২৬").
     */
    fun formatCorrespondingGregorianSpan(
        banglaYearMonth: BanglaYearMonth,
        useBengali: Boolean
    ): String {
        val start = getFirstDayOfBanglaMonth(banglaYearMonth)
        val end = getLastDayOfBanglaMonth(banglaYearMonth)

        return if (start.year == end.year) {
            if (start.month == end.month) {
                if (useBengali) {
                    "${BanglaNumeralFormatter.getGregorianMonthBengali(start.month)} ${BanglaNumeralFormatter.toBengaliDigits(start.year)}"
                } else {
                    "${BanglaNumeralFormatter.getGregorianMonthEnglish(start.month)} ${start.year}"
                }
            } else {
                if (useBengali) {
                    "${BanglaNumeralFormatter.getGregorianMonthBengali(start.month)} – ${BanglaNumeralFormatter.getGregorianMonthBengali(end.month)} ${BanglaNumeralFormatter.toBengaliDigits(end.year)}"
                } else {
                    "${BanglaNumeralFormatter.getGregorianMonthShortEnglish(start.month)} – ${BanglaNumeralFormatter.getGregorianMonthShortEnglish(end.month)} ${end.year}"
                }
            }
        } else {
            if (useBengali) {
                "${BanglaNumeralFormatter.getGregorianMonthShortBengali(start.month)} ${BanglaNumeralFormatter.toBengaliDigits(start.year)} – ${BanglaNumeralFormatter.getGregorianMonthShortBengali(end.month)} ${BanglaNumeralFormatter.toBengaliDigits(end.year)}"
            } else {
                "${BanglaNumeralFormatter.getGregorianMonthShortEnglish(start.month)} ${start.year} – ${BanglaNumeralFormatter.getGregorianMonthShortEnglish(end.month)} ${end.year}"
            }
        }
    }

    /**
     * Returns a formatted subtitle showing the corresponding Bangla month/year span
     * for a given Gregorian [yearMonth] (e.g. "কার্তিক – অগ্রহায়ণ ১৪৩৩").
     */
    fun formatCorrespondingBanglaSpan(
        yearMonth: YearMonth,
        useBengali: Boolean
    ): String {
        val startBangla = gregorianToBangla(yearMonth.atDay(1))
        val endBangla = gregorianToBangla(yearMonth.atEndOfMonth())

        return if (startBangla.year == endBangla.year) {
            if (startBangla.month == endBangla.month) {
                if (useBengali) {
                    "${startBangla.month.bengaliName} ${startBangla.yearBengali}"
                } else {
                    "${startBangla.month.englishName} ${startBangla.year} BS"
                }
            } else {
                if (useBengali) {
                    "${startBangla.month.bengaliName} – ${endBangla.month.bengaliName} ${endBangla.yearBengali} বঙ্গাব্দ"
                } else {
                    "${startBangla.month.englishName} – ${endBangla.month.englishName} ${endBangla.year} BS"
                }
            }
        } else {
            if (useBengali) {
                "${startBangla.month.bengaliName} ${startBangla.yearBengali} – ${endBangla.month.bengaliName} ${endBangla.yearBengali} বঙ্গাব্দ"
            } else {
                "${startBangla.month.englishName} ${startBangla.year} – ${endBangla.month.englishName} ${endBangla.year} BS"
            }
        }
    }
}
