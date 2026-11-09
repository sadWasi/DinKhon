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

import com.banglacalendar.app.data.HolidayRepository
import com.banglacalendar.app.domain.model.BanglaMonth
import com.banglacalendar.app.domain.model.BanglaSeason
import com.banglacalendar.app.domain.model.BanglaYearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Clock
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.ZoneId

class BanglaCalendarEngineTest {

    @Test
    fun `verify official reference dates from specification`() {
        // 1. 16 December 2025 = ১ পৌষ ১৪৩২
        val dec16_2025 = LocalDate.of(2025, Month.DECEMBER, 16)
        val banglaDec16 = BanglaCalendarEngine.gregorianToBangla(dec16_2025)
        assertEquals(1432, banglaDec16.year)
        assertEquals(BanglaMonth.POUSH, banglaDec16.month)
        assertEquals(1, banglaDec16.dayOfMonth)
        assertEquals("১ পৌষ ১৪৩২", banglaDec16.formatBengali())
        assertEquals(dec16_2025, BanglaCalendarEngine.banglaToGregorian(banglaDec16))

        // 2. 21 February 2026 = ৮ ফাল্গুন ১৪৩২
        val feb21_2026 = LocalDate.of(2026, Month.FEBRUARY, 21)
        val banglaFeb21 = BanglaCalendarEngine.gregorianToBangla(feb21_2026)
        assertEquals(1432, banglaFeb21.year)
        assertEquals(BanglaMonth.FALGUN, banglaFeb21.month)
        assertEquals(8, banglaFeb21.dayOfMonth)
        assertEquals("৮ ফাল্গুন ১৪৩২", banglaFeb21.formatBengali())
        assertEquals(feb21_2026, BanglaCalendarEngine.banglaToGregorian(banglaFeb21))

        // 3. 26 March 2026 = ১২ চৈত্র ১৪৩২
        val mar26_2026 = LocalDate.of(2026, Month.MARCH, 26)
        val banglaMar26 = BanglaCalendarEngine.gregorianToBangla(mar26_2026)
        assertEquals(1432, banglaMar26.year)
        assertEquals(BanglaMonth.CHOITRO, banglaMar26.month)
        assertEquals(12, banglaMar26.dayOfMonth)
        assertEquals("১২ চৈত্র ১৪৩২", banglaMar26.formatBengali())
        assertEquals(mar26_2026, BanglaCalendarEngine.banglaToGregorian(banglaMar26))

        // 4. 14 April 2026 = ১ বৈশাখ ১৪৩৩
        val apr14_2026 = LocalDate.of(2026, Month.APRIL, 14)
        val banglaApr14 = BanglaCalendarEngine.gregorianToBangla(apr14_2026)
        assertEquals(1433, banglaApr14.year)
        assertEquals(BanglaMonth.BOISHAKH, banglaApr14.month)
        assertEquals(1, banglaApr14.dayOfMonth)
        assertEquals("১ বৈশাখ ১৪৩৩", banglaApr14.formatBengali())
        assertEquals(apr14_2026, BanglaCalendarEngine.banglaToGregorian(banglaApr14))

        // 5. 17 October 2026 = ১ কার্তিক ১৪৩৩
        val oct17_2026 = LocalDate.of(2026, Month.OCTOBER, 17)
        val banglaOct17 = BanglaCalendarEngine.gregorianToBangla(oct17_2026)
        assertEquals(1433, banglaOct17.year)
        assertEquals(BanglaMonth.KARTIK, banglaOct17.month)
        assertEquals(1, banglaOct17.dayOfMonth)
        assertEquals("১ কার্তিক ১৪৩৩", banglaOct17.formatBengali())
        assertEquals(oct17_2026, BanglaCalendarEngine.banglaToGregorian(banglaOct17))
    }

    @Test
    fun `verify all 12 Bangla month lengths in regular and leap years`() {
        // 1432 BS (Falgun is in Feb 2026, non-leap year)
        assertFalse(BanglaCalendarEngine.isBanglaLeapYear(1432))
        assertEquals(31, BanglaCalendarEngine.getDaysInBanglaMonth(1432, BanglaMonth.BOISHAKH))
        assertEquals(31, BanglaCalendarEngine.getDaysInBanglaMonth(1432, BanglaMonth.JYOISHTHO))
        assertEquals(31, BanglaCalendarEngine.getDaysInBanglaMonth(1432, BanglaMonth.ASHARH))
        assertEquals(31, BanglaCalendarEngine.getDaysInBanglaMonth(1432, BanglaMonth.SHRABON))
        assertEquals(31, BanglaCalendarEngine.getDaysInBanglaMonth(1432, BanglaMonth.BHADRO))
        assertEquals(31, BanglaCalendarEngine.getDaysInBanglaMonth(1432, BanglaMonth.ASHWIN))
        assertEquals(30, BanglaCalendarEngine.getDaysInBanglaMonth(1432, BanglaMonth.KARTIK))
        assertEquals(30, BanglaCalendarEngine.getDaysInBanglaMonth(1432, BanglaMonth.OGROHAYON))
        assertEquals(30, BanglaCalendarEngine.getDaysInBanglaMonth(1432, BanglaMonth.POUSH))
        assertEquals(30, BanglaCalendarEngine.getDaysInBanglaMonth(1432, BanglaMonth.MAGH))
        assertEquals(29, BanglaCalendarEngine.getDaysInBanglaMonth(1432, BanglaMonth.FALGUN))
        assertEquals(30, BanglaCalendarEngine.getDaysInBanglaMonth(1432, BanglaMonth.CHOITRO))
        assertEquals(365, BanglaCalendarEngine.getDaysInBanglaYear(1432))

        // 1430 BS (Falgun is in Feb 2024, leap year) and 1434 BS (Falgun is in Feb 2028, leap year)
        assertTrue(BanglaCalendarEngine.isBanglaLeapYear(1430))
        assertTrue(BanglaCalendarEngine.isBanglaLeapYear(1434))
        assertEquals(30, BanglaCalendarEngine.getDaysInBanglaMonth(1430, BanglaMonth.FALGUN))
        assertEquals(30, BanglaCalendarEngine.getDaysInBanglaMonth(1434, BanglaMonth.FALGUN))
        assertEquals(366, BanglaCalendarEngine.getDaysInBanglaYear(1430))
        assertEquals(366, BanglaCalendarEngine.getDaysInBanglaYear(1434))
    }

    @Test
    fun `verify leap year Falgun transitions and Feb 29 handling`() {
        // In 2024 (leap year), Feb 14 = 1 Falgun 1430, Feb 29 = 16 Falgun 1430, Mar 14 = 30 Falgun 1430, Mar 15 = 1 Choitro 1430
        val feb14_2024 = LocalDate.of(2024, Month.FEBRUARY, 14)
        val feb29_2024 = LocalDate.of(2024, Month.FEBRUARY, 29)
        val mar14_2024 = LocalDate.of(2024, Month.MARCH, 14)
        val mar15_2024 = LocalDate.of(2024, Month.MARCH, 15)

        assertEquals("১ ফাল্গুন ১৪৩০", BanglaCalendarEngine.gregorianToBangla(feb14_2024).formatBengali())
        assertEquals("১৬ ফাল্গুন ১৪৩০", BanglaCalendarEngine.gregorianToBangla(feb29_2024).formatBengali())
        assertEquals("৩০ ফাল্গুন ১৪৩০", BanglaCalendarEngine.gregorianToBangla(mar14_2024).formatBengali())
        assertEquals("১ চৈত্র ১৪৩০", BanglaCalendarEngine.gregorianToBangla(mar15_2024).formatBengali())

        // In 2026 (non-leap year), Mar 14 = 29 Falgun 1432, Mar 15 = 1 Choitro 1432
        val mar14_2026 = LocalDate.of(2026, Month.MARCH, 14)
        val mar15_2026 = LocalDate.of(2026, Month.MARCH, 15)
        assertEquals("২৯ ফাল্গুন ১৪৩২", BanglaCalendarEngine.gregorianToBangla(mar14_2026).formatBengali())
        assertEquals("১ চৈত্র ১৪৩২", BanglaCalendarEngine.gregorianToBangla(mar15_2026).formatBengali())
    }

    @Test
    fun `verify Bangla and Gregorian year boundary transitions`() {
        // April 13, 2026 is 30 Choitro 1432; April 14, 2026 is 1 Boishakh 1433
        val apr13_2026 = LocalDate.of(2026, Month.APRIL, 13)
        val apr14_2026 = LocalDate.of(2026, Month.APRIL, 14)

        val bApr13 = BanglaCalendarEngine.gregorianToBangla(apr13_2026)
        val bApr14 = BanglaCalendarEngine.gregorianToBangla(apr14_2026)

        assertEquals(1432, bApr13.year)
        assertEquals(BanglaMonth.CHOITRO, bApr13.month)
        assertEquals(30, bApr13.dayOfMonth)

        assertEquals(1433, bApr14.year)
        assertEquals(BanglaMonth.BOISHAKH, bApr14.month)
        assertEquals(1, bApr14.dayOfMonth)

        // Dec 31, 2025 is 16 Poush 1432; Jan 1, 2026 is 17 Poush 1432
        val dec31_2025 = LocalDate.of(2025, Month.DECEMBER, 31)
        val jan1_2026 = LocalDate.of(2026, Month.JANUARY, 1)

        assertEquals("১৬ পৌষ ১৪৩২", BanglaCalendarEngine.gregorianToBangla(dec31_2025).formatBengali())
        assertEquals("১৭ পৌষ ১৪৩২", BanglaCalendarEngine.gregorianToBangla(jan1_2026).formatBengali())
    }

    @Test
    fun `verify exhaustive round-trip conversion across multiple decades`() {
        var current = LocalDate.of(1990, 1, 1)
        val end = LocalDate.of(2040, 12, 31)

        while (!current.isAfter(end)) {
            val bangla = BanglaCalendarEngine.gregorianToBangla(current)
            val roundTrip = BanglaCalendarEngine.banglaToGregorian(bangla)
            assertEquals("Round-trip failed for $current ($bangla)", current, roundTrip)
            assertEquals(current.dayOfWeek, bangla.dayOfWeek)
            current = current.plusDays(1)
        }
    }

    @Test
    fun `verify month navigation across year boundaries`() {
        val choitro1432 = BanglaYearMonth(1432, BanglaMonth.CHOITRO)
        val boishakh1433 = choitro1432.plusMonths(1)
        assertEquals(BanglaYearMonth(1433, BanglaMonth.BOISHAKH), boishakh1433)
        assertEquals(choitro1432, boishakh1433.minusMonths(1))

        val jumpForward = choitro1432.plusMonths(15)
        assertEquals(BanglaYearMonth(1434, BanglaMonth.ASHARH), jumpForward)
        assertEquals(choitro1432, jumpForward.minusMonths(15))
    }

    @Test
    fun `verify month grid alignment and weekend flags`() {
        val kartik1433 = BanglaYearMonth(1433, BanglaMonth.KARTIK)
        val grid = BanglaCalendarEngine.generateBanglaMonthGrid(
            banglaYearMonth = kartik1433,
            today = LocalDate.of(2026, Month.NOVEMBER, 9)
        )

        assertEquals(42, grid.size)
        // First cell in grid must always be a Sunday
        assertEquals(DayOfWeek.SUNDAY, grid.first().gregorianDate.dayOfWeek)
        // Last cell in grid must always be a Saturday
        assertEquals(DayOfWeek.SATURDAY, grid.last().gregorianDate.dayOfWeek)

        // Count current-month cells for Kartik 1433 (30 days)
        val currentMonthCells = grid.filter { it.isCurrentMonth }
        assertEquals(30, currentMonthCells.size)
        assertEquals(1, currentMonthCells.first().banglaDate.dayOfMonth)
        assertEquals(30, currentMonthCells.last().banglaDate.dayOfMonth)

        // Verify Friday and Saturday are marked as Bangladesh weekends
        grid.forEach { cell ->
            val expectedWeekend =
                cell.gregorianDate.dayOfWeek == DayOfWeek.FRIDAY ||
                    cell.gregorianDate.dayOfWeek == DayOfWeek.SATURDAY
            assertEquals(expectedWeekend, cell.isWeekend)
        }

        // Verify Gregorian month grid as well
        val nov2026 = YearMonth.of(2026, Month.NOVEMBER)
        val gregGrid = BanglaCalendarEngine.generateGregorianMonthGrid(
            gregorianYearMonth = nov2026,
            today = LocalDate.of(2026, Month.NOVEMBER, 9)
        )
        assertEquals(42, gregGrid.size)
        assertEquals(DayOfWeek.SUNDAY, gregGrid.first().gregorianDate.dayOfWeek)
        assertEquals(30, gregGrid.count { it.isCurrentMonth })
    }

    @Test
    fun `verify Bengali numerals seasons and holidays`() {
        assertEquals("১৪৩৩", BanglaNumeralFormatter.toBengaliDigits(1433))
        assertEquals("০১২৩৪৫৬৭৮৯", BanglaNumeralFormatter.toBengaliDigits("0123456789"))
        assertEquals("0123456789", BanglaNumeralFormatter.toEnglishDigits("০১২৩৪৫৬৭৮৯"))

        assertEquals(BanglaSeason.GRISHMO, BanglaMonth.BOISHAKH.season)
        assertEquals(BanglaSeason.BORSHA, BanglaMonth.ASHARH.season)
        assertEquals(BanglaSeason.SHOROT, BanglaMonth.ASHWIN.season)
        assertEquals(BanglaSeason.HEMONTO, BanglaMonth.KARTIK.season)
        assertEquals(BanglaSeason.SHEET, BanglaMonth.POUSH.season)
        assertEquals(BanglaSeason.BOSHONTO, BanglaMonth.FALGUN.season)

        val repo = HolidayRepository()
        val pohelaBoishakh = LocalDate.of(2026, Month.APRIL, 14)
        val pbHolidays = repo.getHolidaysForDate(
            pohelaBoishakh,
            BanglaCalendarEngine.gregorianToBangla(pohelaBoishakh)
        )
        assertTrue(pbHolidays.any { it.id == "pohela_boishakh" })
    }

    @Test
    fun `verify all 12 Bangla month start dates in Gregorian calendar`() {
        val expectedMonthStarts1433 = listOf(
            BanglaMonth.BOISHAKH to LocalDate.of(2026, Month.APRIL, 14),
            BanglaMonth.JYOISHTHO to LocalDate.of(2026, Month.MAY, 15),
            BanglaMonth.ASHARH to LocalDate.of(2026, Month.JUNE, 15),
            BanglaMonth.SHRABON to LocalDate.of(2026, Month.JULY, 16),
            BanglaMonth.BHADRO to LocalDate.of(2026, Month.AUGUST, 16),
            BanglaMonth.ASHWIN to LocalDate.of(2026, Month.SEPTEMBER, 16),
            BanglaMonth.KARTIK to LocalDate.of(2026, Month.OCTOBER, 17),
            BanglaMonth.OGROHAYON to LocalDate.of(2026, Month.NOVEMBER, 16),
            BanglaMonth.POUSH to LocalDate.of(2026, Month.DECEMBER, 16),
            BanglaMonth.MAGH to LocalDate.of(2027, Month.JANUARY, 15),
            BanglaMonth.FALGUN to LocalDate.of(2027, Month.FEBRUARY, 14),
            BanglaMonth.CHOITRO to LocalDate.of(2027, Month.MARCH, 15)
        )

        for ((banglaMonth, expectedGregorianStart) in expectedMonthStarts1433) {
            val converted = BanglaCalendarEngine.gregorianToBangla(expectedGregorianStart)
            assertEquals(1433, converted.year)
            assertEquals(banglaMonth, converted.month)
            assertEquals(1, converted.dayOfMonth)
            assertEquals(
                expectedGregorianStart,
                BanglaCalendarEngine.banglaToGregorian(1433, banglaMonth, 1)
            )
        }
    }

    @Test
    fun `verify century leap year rules for 2000 and 2100`() {
        // Year 2000 is divisible by 400 -> leap year (Bangla year 1406 has 30 days in Falgun)
        assertTrue(BanglaCalendarEngine.isBanglaLeapYear(1406))
        assertEquals(30, BanglaCalendarEngine.getDaysInBanglaMonth(1406, BanglaMonth.FALGUN))

        // Year 2100 is divisible by 100 but not 400 -> NOT a leap year (Bangla year 1506 has 29 days in Falgun)
        assertFalse(BanglaCalendarEngine.isBanglaLeapYear(1506))
        assertEquals(29, BanglaCalendarEngine.getDaysInBanglaMonth(1506, BanglaMonth.FALGUN))
    }

    @Test
    fun `verify Asia Dhaka timezone today detection across UTC boundary`() {
        // 2026-04-13T18:30:00Z is 2026-04-14T00:30:00+06:00 in Asia/Dhaka (Pohela Boishakh 1433!)
        val dhakaMidnightClock = Clock.fixed(
            Instant.parse("2026-04-13T18:30:00Z"),
            ZoneId.of("Asia/Dhaka")
        )
        val todayGregorian = BanglaCalendarEngine.today(dhakaMidnightClock)
        val todayBangla = BanglaCalendarEngine.gregorianToBangla(todayGregorian)

        assertEquals(LocalDate.of(2026, Month.APRIL, 14), todayGregorian)
        assertEquals(1433, todayBangla.year)
        assertEquals(BanglaMonth.BOISHAKH, todayBangla.month)
        assertEquals(1, todayBangla.dayOfMonth)
    }
}
