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
import java.time.LocalDate

/**
 * Represents a single day cell inside the 7-column monthly calendar grid.
 * Pre-computes display strings upon creation so UI rendering performs zero string allocations.
 */
@Immutable
data class CalendarDayCell(
    val gregorianDate: LocalDate,
    val banglaDate: BanglaDate,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isWeekend: Boolean,
    val holidays: List<HolidayEvent> = emptyList()
) {
    val hasHoliday: Boolean = holidays.isNotEmpty()

    val hasPublicHoliday: Boolean = holidays.any { it.isPublicHoliday }

    private val primaryBangla: String = banglaDate.dayBengali
    private val primaryEnglish: String = gregorianDate.dayOfMonth.toString()

    private val secondaryBanglaMode: String = if (gregorianDate.dayOfMonth == 1) {
        "${BanglaNumeralFormatter.getGregorianMonthShortEnglish(gregorianDate.month)} 1"
    } else {
        primaryEnglish
    }

    private val secondaryEnglishMode: String = if (banglaDate.dayOfMonth == 1) {
        "১ ${banglaDate.month.shortBengaliName}"
    } else {
        primaryBangla
    }

    /**
     * Primary large numeral displayed in the cell.
     */
    fun primaryDayText(mode: CalendarMode): String = when (mode) {
        CalendarMode.BANGLA -> primaryBangla
        CalendarMode.ENGLISH -> primaryEnglish
    }

    /**
     * Secondary smaller date label from the alternate calendar system.
     */
    fun secondaryDateText(mode: CalendarMode): String = when (mode) {
        CalendarMode.BANGLA -> secondaryBanglaMode
        CalendarMode.ENGLISH -> secondaryEnglishMode
    }
}
