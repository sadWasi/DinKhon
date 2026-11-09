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
package com.banglacalendar.app.ui.viewmodel

import com.banglacalendar.app.data.PreferencesRepository
import com.banglacalendar.app.domain.model.BanglaMonth
import com.banglacalendar.app.domain.model.CalendarMode
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.ZoneId

class CalendarViewModelTest {

    private val fixedClock = Clock.fixed(
        Instant.parse("2026-02-21T06:00:00Z"),
        ZoneId.of("Asia/Dhaka")
    )

    @Test
    fun `switching between Bangla and English modes preserves exact real-world selected date`() {
        val vm = CalendarViewModel(
            preferencesRepository = PreferencesRepository(context = null),
            clock = fixedClock
        )

        // Initial date is 21 February 2026 = 8 Falgun 1432
        val feb21 = LocalDate.of(2026, Month.FEBRUARY, 21)
        assertEquals(feb21, vm.uiState.value.selectedGregorianDate)
        assertEquals(1432, vm.uiState.value.selectedBanglaDate.year)
        assertEquals(BanglaMonth.FALGUN, vm.uiState.value.selectedBanglaDate.month)
        assertEquals(8, vm.uiState.value.selectedBanglaDate.dayOfMonth)
        assertEquals(CalendarMode.BANGLA, vm.uiState.value.calendarMode)

        // Select another date: 16 December 2025 (1 Poush 1432)
        val dec16 = LocalDate.of(2025, Month.DECEMBER, 16)
        vm.selectDate(dec16)
        assertEquals(dec16, vm.uiState.value.selectedGregorianDate)
        assertEquals("১ পৌষ ১৪৩২", vm.uiState.value.selectedBanglaDate.formatBengali())

        // Switch to ENGLISH mode -> must preserve 16 December 2025 and display December 2025
        vm.setCalendarMode(CalendarMode.ENGLISH)
        assertEquals(CalendarMode.ENGLISH, vm.uiState.value.calendarMode)
        assertEquals(dec16, vm.uiState.value.selectedGregorianDate)
        assertEquals("১ পৌষ ১৪৩২", vm.uiState.value.selectedBanglaDate.formatBengali())
        assertEquals(YearMonth.of(2025, Month.DECEMBER), vm.uiState.value.displayedGregorianYearMonth)

        // Switch back to BANGLA mode -> must still preserve 16 December 2025 and display Poush 1432
        vm.setCalendarMode(CalendarMode.BANGLA)
        assertEquals(CalendarMode.BANGLA, vm.uiState.value.calendarMode)
        assertEquals(dec16, vm.uiState.value.selectedGregorianDate)
        assertEquals(BanglaMonth.POUSH, vm.uiState.value.displayedBanglaYearMonth.month)
        assertEquals(1432, vm.uiState.value.displayedBanglaYearMonth.year)
    }

    @Test
    fun `jumpToToday restores today date and month`() {
        val vm = CalendarViewModel(
            preferencesRepository = PreferencesRepository(context = null),
            clock = fixedClock
        )

        vm.navigateMonth(5)
        vm.selectDate(LocalDate.of(2026, Month.OCTOBER, 17))
        assertEquals(LocalDate.of(2026, Month.OCTOBER, 17), vm.uiState.value.selectedGregorianDate)

        vm.jumpToToday()
        assertEquals(LocalDate.of(2026, Month.FEBRUARY, 21), vm.uiState.value.selectedGregorianDate)
        assertEquals(BanglaMonth.FALGUN, vm.uiState.value.displayedBanglaYearMonth.month)
    }
}
