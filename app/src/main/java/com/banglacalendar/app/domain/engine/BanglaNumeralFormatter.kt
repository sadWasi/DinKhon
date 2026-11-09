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

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

/**
 * Pure formatting utility for Bengali and English numerals, weekdays, and month labels.
 *
 * Bengali digits: ০ ১ ২ ৩ ৪ ৫ ৬ ৭ ৮ ৯
 */
object BanglaNumeralFormatter {

    private val BENGALI_DIGITS = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

    /**
     * Converts any ASCII digits (0-9) in [input] to Bengali numerals (০-৯).
     */
    fun toBengaliDigits(input: String): String {
        val sb = StringBuilder(input.length)
        for (ch in input) {
            if (ch in '0'..'9') {
                sb.append(BENGALI_DIGITS[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * Converts an integer [number] to a string of Bengali numerals (০-৯).
     */
    fun toBengaliDigits(number: Int): String = toBengaliDigits(number.toString())

    /**
     * Converts any Bengali numerals (০-৯) in [input] back to ASCII digits (0-9).
     */
    fun toEnglishDigits(input: String): String {
        val sb = StringBuilder(input.length)
        for (ch in input) {
            if (ch in '০'..'৯') {
                sb.append(('0'.code + (ch - '০')).toChar())
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * Formats an integer according to [useBengali].
     */
    fun formatNumber(number: Int, useBengali: Boolean): String =
        if (useBengali) toBengaliDigits(number) else number.toString()

    /**
     * Full Bengali weekday names as required:
     * রবিবার, সোমবার, মঙ্গলবার, বুধবার, বৃহস্পতিবার, শুক্রবার, শনিবার
     */
    fun getBengaliWeekdayFull(dayOfWeek: DayOfWeek): String = when (dayOfWeek) {
        DayOfWeek.SUNDAY -> "রবিবার"
        DayOfWeek.MONDAY -> "সোমবার"
        DayOfWeek.TUESDAY -> "মঙ্গলবার"
        DayOfWeek.WEDNESDAY -> "বুধবার"
        DayOfWeek.THURSDAY -> "বৃহস্পতিবার"
        DayOfWeek.FRIDAY -> "শুক্রবার"
        DayOfWeek.SATURDAY -> "শনিবার"
    }

    /**
     * Short Bengali weekday headers for the 7-column calendar grid.
     */
    fun getBengaliWeekdayShort(dayOfWeek: DayOfWeek): String = when (dayOfWeek) {
        DayOfWeek.SUNDAY -> "রবি"
        DayOfWeek.MONDAY -> "সোম"
        DayOfWeek.TUESDAY -> "মঙ্গল"
        DayOfWeek.WEDNESDAY -> "বুধ"
        DayOfWeek.THURSDAY -> "বৃহঃ"
        DayOfWeek.FRIDAY -> "শুক্র"
        DayOfWeek.SATURDAY -> "শনি"
    }

    /**
     * Full English weekday names.
     */
    fun getEnglishWeekdayFull(dayOfWeek: DayOfWeek): String = when (dayOfWeek) {
        DayOfWeek.SUNDAY -> "Sunday"
        DayOfWeek.MONDAY -> "Monday"
        DayOfWeek.TUESDAY -> "Tuesday"
        DayOfWeek.WEDNESDAY -> "Wednesday"
        DayOfWeek.THURSDAY -> "Thursday"
        DayOfWeek.FRIDAY -> "Friday"
        DayOfWeek.SATURDAY -> "Saturday"
    }

    /**
     * Short English weekday headers for the 7-column calendar grid.
     */
    fun getEnglishWeekdayShort(dayOfWeek: DayOfWeek): String = when (dayOfWeek) {
        DayOfWeek.SUNDAY -> "Sun"
        DayOfWeek.MONDAY -> "Mon"
        DayOfWeek.TUESDAY -> "Tue"
        DayOfWeek.WEDNESDAY -> "Wed"
        DayOfWeek.THURSDAY -> "Thu"
        DayOfWeek.FRIDAY -> "Fri"
        DayOfWeek.SATURDAY -> "Sat"
    }

    fun getWeekdayFull(dayOfWeek: DayOfWeek, useBengali: Boolean): String =
        if (useBengali) getBengaliWeekdayFull(dayOfWeek) else getEnglishWeekdayFull(dayOfWeek)

    fun getWeekdayShort(dayOfWeek: DayOfWeek, useBengali: Boolean): String =
        if (useBengali) getBengaliWeekdayShort(dayOfWeek) else getEnglishWeekdayShort(dayOfWeek)

    /**
     * Standard 7-day week order starting on Sunday (রবিবার to শনিবার).
     */
    val WEEK_DAYS_SUNDAY_START: List<DayOfWeek> = listOf(
        DayOfWeek.SUNDAY,
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY
    )

    /**
     * Returns true if [dayOfWeek] is a weekend in Bangladesh (Friday or Saturday).
     */
    fun isBangladeshWeekend(dayOfWeek: DayOfWeek): Boolean =
        dayOfWeek == DayOfWeek.FRIDAY || dayOfWeek == DayOfWeek.SATURDAY

    /**
     * Gregorian month names in Bengali.
     */
    fun getGregorianMonthBengali(month: Month): String = when (month) {
        Month.JANUARY -> "জানুয়ারি"
        Month.FEBRUARY -> "ফেব্রুয়ারি"
        Month.MARCH -> "মার্চ"
        Month.APRIL -> "এপ্রিল"
        Month.MAY -> "মে"
        Month.JUNE -> "জুন"
        Month.JULY -> "জুলাই"
        Month.AUGUST -> "আগস্ট"
        Month.SEPTEMBER -> "সেপ্টেম্বর"
        Month.OCTOBER -> "অক্টোবর"
        Month.NOVEMBER -> "নভেম্বর"
        Month.DECEMBER -> "ডিসেম্বর"
    }

    /**
     * Short Gregorian month names in Bengali.
     */
    fun getGregorianMonthShortBengali(month: Month): String = when (month) {
        Month.JANUARY -> "জানু"
        Month.FEBRUARY -> "ফেব্রু"
        Month.MARCH -> "মার্চ"
        Month.APRIL -> "এপ্রিল"
        Month.MAY -> "মে"
        Month.JUNE -> "জুন"
        Month.JULY -> "জুলাই"
        Month.AUGUST -> "আগস্ট"
        Month.SEPTEMBER -> "সেপ্টে"
        Month.OCTOBER -> "অক্টো"
        Month.NOVEMBER -> "নভে"
        Month.DECEMBER -> "ডিসে"
    }

    /**
     * Full Gregorian month names in English.
     */
    fun getGregorianMonthEnglish(month: Month): String = when (month) {
        Month.JANUARY -> "January"
        Month.FEBRUARY -> "February"
        Month.MARCH -> "March"
        Month.APRIL -> "April"
        Month.MAY -> "May"
        Month.JUNE -> "June"
        Month.JULY -> "July"
        Month.AUGUST -> "August"
        Month.SEPTEMBER -> "September"
        Month.OCTOBER -> "October"
        Month.NOVEMBER -> "November"
        Month.DECEMBER -> "December"
    }

    /**
     * Short Gregorian month names in English.
     */
    fun getGregorianMonthShortEnglish(month: Month): String = when (month) {
        Month.JANUARY -> "Jan"
        Month.FEBRUARY -> "Feb"
        Month.MARCH -> "Mar"
        Month.APRIL -> "Apr"
        Month.MAY -> "May"
        Month.JUNE -> "Jun"
        Month.JULY -> "Jul"
        Month.AUGUST -> "Aug"
        Month.SEPTEMBER -> "Sep"
        Month.OCTOBER -> "Oct"
        Month.NOVEMBER -> "Nov"
        Month.DECEMBER -> "Dec"
    }

    /**
     * Formats a Gregorian [LocalDate] in full (e.g., "১৬ ডিসেম্বর ২০২৫" or "16 December 2025").
     */
    fun formatGregorianDate(date: LocalDate, useBengali: Boolean): String {
        return if (useBengali) {
            "${toBengaliDigits(date.dayOfMonth)} ${getGregorianMonthBengali(date.month)} ${toBengaliDigits(date.year)}"
        } else {
            "${date.dayOfMonth} ${getGregorianMonthEnglish(date.month)} ${date.year}"
        }
    }

    /**
     * Formats a Gregorian [YearMonth] heading.
     */
    fun formatGregorianYearMonth(yearMonth: YearMonth, useBengali: Boolean): String {
        return if (useBengali) {
            "${getGregorianMonthBengali(yearMonth.month)} ${toBengaliDigits(yearMonth.year)}"
        } else {
            "${getGregorianMonthEnglish(yearMonth.month)} ${yearMonth.year}"
        }
    }
}
