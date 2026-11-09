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

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.banglacalendar.app.data.HolidayRepository
import com.banglacalendar.app.data.PreferencesRepository
import com.banglacalendar.app.data.UserPreferences
import com.banglacalendar.app.domain.engine.BanglaCalendarEngine
import com.banglacalendar.app.domain.engine.BanglaNumeralFormatter
import com.banglacalendar.app.domain.model.AppThemeMode
import com.banglacalendar.app.domain.model.BanglaDate
import com.banglacalendar.app.domain.model.BanglaMonth
import com.banglacalendar.app.domain.model.BanglaYearMonth
import com.banglacalendar.app.domain.model.CalendarDayCell
import com.banglacalendar.app.domain.model.CalendarMode
import com.banglacalendar.app.domain.model.HolidayEvent
import com.banglacalendar.app.domain.model.InterfaceLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.Clock
import java.time.LocalDate
import java.time.YearMonth

/**
 * Immutable UI state for the DinKhon home screen.
 */
@Immutable
data class CalendarUiState(
    val todayGregorian: LocalDate,
    val todayBangla: BanglaDate,
    val selectedGregorianDate: LocalDate,
    val selectedBanglaDate: BanglaDate,
    val calendarMode: CalendarMode,
    val displayedBanglaYearMonth: BanglaYearMonth,
    val displayedGregorianYearMonth: YearMonth,
    val monthGridCells: List<CalendarDayCell>,
    val selectedDateHolidays: List<HolidayEvent>,
    val todayHolidays: List<HolidayEvent>,
    val preferences: UserPreferences,
    val isSettingsOpen: Boolean = false,
    val isDateJumpOpen: Boolean = false,
    val monthTransitionDirection: Int = 0
) {
    /**
     * Whether general UI chrome (button labels, section headers) should render in Bengali.
     */
    val useBengaliChrome: Boolean = when (preferences.interfaceLanguage) {
        InterfaceLanguage.FOLLOW_CALENDAR -> calendarMode == CalendarMode.BANGLA
        InterfaceLanguage.BANGLA -> true
        InterfaceLanguage.ENGLISH -> false
    }

    /**
     * Whether the currently displayed month is the month containing [todayGregorian]
     * and [selectedGregorianDate] is already [todayGregorian].
     */
    val isViewingToday: Boolean = run {
        val isOnTodayDate = selectedGregorianDate == todayGregorian
        val isOnTodayMonth = when (calendarMode) {
            CalendarMode.BANGLA ->
                displayedBanglaYearMonth == BanglaYearMonth.from(todayBangla)
            CalendarMode.ENGLISH ->
                displayedGregorianYearMonth == YearMonth.from(todayGregorian)
        }
        isOnTodayDate && isOnTodayMonth
    }

    /**
     * Primary heading for the monthly calendar section.
     */
    val primaryMonthHeading: String = when (calendarMode) {
        CalendarMode.BANGLA -> displayedBanglaYearMonth.formatBengali(includeBongabdoSuffix = true)
        CalendarMode.ENGLISH -> BanglaNumeralFormatter.formatGregorianYearMonth(
            yearMonth = displayedGregorianYearMonth,
            useBengali = false
        )
    }

    /**
     * Secondary subtitle for the monthly calendar section showing the corresponding span in the alternate calendar.
     */
    val secondaryMonthSubtitle: String = when (calendarMode) {
        CalendarMode.BANGLA -> BanglaCalendarEngine.formatCorrespondingGregorianSpan(
            banglaYearMonth = displayedBanglaYearMonth,
            useBengali = false
        )
        CalendarMode.ENGLISH -> BanglaCalendarEngine.formatCorrespondingBanglaSpan(
            yearMonth = displayedGregorianYearMonth,
            useBengali = true
        )
    }

    /**
     * Formatted string suitable for copying the currently selected date to the clipboard.
     */
    fun formatSelectedDateForCopy(): String {
        val banglaFull = "${selectedBanglaDate.formatBengali(includeBongabdoSuffix = true)} (${selectedBanglaDate.weekdayBengali})"
        val englishFull = "${BanglaNumeralFormatter.formatGregorianDate(selectedGregorianDate, useBengali = false)} (${selectedBanglaDate.weekdayEnglish})"
        val holidayLine = if (selectedDateHolidays.isNotEmpty()) {
            "\n" + selectedDateHolidays.joinToString(" • ") { "${it.titleBengali} / ${it.titleEnglish}" }
        } else {
            ""
        }
        return "$banglaFull\n$englishFull$holidayLine"
    }
}

/**
 * ViewModel managing calendar state, two-way date conversion, month navigation, and preferences.
 * Includes an in-memory month-grid cache and pre-warmed adjacent months for zero-latency swipes.
 */
class CalendarViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val holidayRepository: HolidayRepository = HolidayRepository(),
    private val clock: Clock = Clock.system(BanglaCalendarEngine.DHAKA_ZONE)
) : ViewModel() {

    private val banglaGridCache = HashMap<Pair<BanglaYearMonth, LocalDate>, List<CalendarDayCell>>(24)
    private val gregorianGridCache = HashMap<Pair<YearMonth, LocalDate>, List<CalendarDayCell>>(24)

    private val initialToday: LocalDate = BanglaCalendarEngine.today(clock)
    private val initialTodayBangla: BanglaDate = BanglaCalendarEngine.gregorianToBangla(initialToday)
    private val initialPrefs: UserPreferences = preferencesRepository.preferences.value

    private val _uiState: MutableStateFlow<CalendarUiState> = MutableStateFlow(
        buildInitialState(
            today = initialToday,
            todayBangla = initialTodayBangla,
            prefs = initialPrefs
        )
    )
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    private fun buildInitialState(
        today: LocalDate,
        todayBangla: BanglaDate,
        prefs: UserPreferences
    ): CalendarUiState {
        val mode = prefs.defaultCalendarMode
        val banglaYm = BanglaYearMonth.from(todayBangla)
        val gregYm = YearMonth.from(today)
        val grid = computeGrid(mode, banglaYm, gregYm, today)
        preWarmAdjacentMonths(banglaYm, gregYm, today)
        val todayEvents = holidayRepository.getHolidaysForDate(today, todayBangla)

        return CalendarUiState(
            todayGregorian = today,
            todayBangla = todayBangla,
            selectedGregorianDate = today,
            selectedBanglaDate = todayBangla,
            calendarMode = mode,
            displayedBanglaYearMonth = banglaYm,
            displayedGregorianYearMonth = gregYm,
            monthGridCells = grid,
            selectedDateHolidays = todayEvents,
            todayHolidays = todayEvents,
            preferences = prefs
        )
    }

    private fun computeGrid(
        mode: CalendarMode,
        banglaYearMonth: BanglaYearMonth,
        gregorianYearMonth: YearMonth,
        today: LocalDate
    ): List<CalendarDayCell> {
        return when (mode) {
            CalendarMode.BANGLA -> banglaGridCache.getOrPut(banglaYearMonth to today) {
                BanglaCalendarEngine.generateBanglaMonthGrid(
                    banglaYearMonth = banglaYearMonth,
                    today = today,
                    holidayProvider = holidayRepository::getHolidaysForDate
                )
            }
            CalendarMode.ENGLISH -> gregorianGridCache.getOrPut(gregorianYearMonth to today) {
                BanglaCalendarEngine.generateGregorianMonthGrid(
                    gregorianYearMonth = gregorianYearMonth,
                    today = today,
                    holidayProvider = holidayRepository::getHolidaysForDate
                )
            }
        }
    }

    private fun preWarmAdjacentMonths(
        banglaYm: BanglaYearMonth,
        gregYm: YearMonth,
        today: LocalDate
    ) {
        if (banglaGridCache.size > 48) banglaGridCache.clear()
        if (gregorianGridCache.size > 48) gregorianGridCache.clear()
        computeGrid(CalendarMode.BANGLA, banglaYm.minusMonths(1), gregYm, today)
        computeGrid(CalendarMode.BANGLA, banglaYm.plusMonths(1), gregYm, today)
        computeGrid(CalendarMode.ENGLISH, banglaYm, gregYm.minusMonths(1), today)
        computeGrid(CalendarMode.ENGLISH, banglaYm, gregYm.plusMonths(1), today)
    }

    /**
     * Switches between [CalendarMode.BANGLA] and [CalendarMode.ENGLISH] while preserving
     * the exact same selected real-world date ([CalendarUiState.selectedGregorianDate]).
     */
    fun setCalendarMode(newMode: CalendarMode) {
        _uiState.update { current ->
            if (current.calendarMode == newMode) return@update current
            val syncedBanglaYm = BanglaYearMonth.from(current.selectedBanglaDate)
            val syncedGregYm = YearMonth.from(current.selectedGregorianDate)
            val newGrid = computeGrid(
                mode = newMode,
                banglaYearMonth = syncedBanglaYm,
                gregorianYearMonth = syncedGregYm,
                today = current.todayGregorian
            )
            preWarmAdjacentMonths(syncedBanglaYm, syncedGregYm, current.todayGregorian)
            current.copy(
                calendarMode = newMode,
                displayedBanglaYearMonth = syncedBanglaYm,
                displayedGregorianYearMonth = syncedGregYm,
                monthGridCells = newGrid,
                monthTransitionDirection = 0
            )
        }
    }

    /**
     * Selects a specific real-world date. If the date belongs to a different month than
     * currently displayed, smoothly navigates the calendar grid to that month.
     */
    fun selectDate(gregorianDate: LocalDate) {
        _uiState.update { current ->
            if (current.selectedGregorianDate == gregorianDate) return@update current
            val banglaDate = BanglaCalendarEngine.gregorianToBangla(gregorianDate)
            val targetBanglaYm = BanglaYearMonth.from(banglaDate)
            val targetGregYm = YearMonth.from(gregorianDate)

            val needsGridRefresh = when (current.calendarMode) {
                CalendarMode.BANGLA -> targetBanglaYm != current.displayedBanglaYearMonth
                CalendarMode.ENGLISH -> targetGregYm != current.displayedGregorianYearMonth
            }

            val direction = when (current.calendarMode) {
                CalendarMode.BANGLA -> targetBanglaYm.compareTo(current.displayedBanglaYearMonth).coerceIn(-1, 1)
                CalendarMode.ENGLISH -> targetGregYm.compareTo(current.displayedGregorianYearMonth).coerceIn(-1, 1)
            }

            val updatedGrid = if (needsGridRefresh) {
                val g = computeGrid(
                    mode = current.calendarMode,
                    banglaYearMonth = targetBanglaYm,
                    gregorianYearMonth = targetGregYm,
                    today = current.todayGregorian
                )
                preWarmAdjacentMonths(targetBanglaYm, targetGregYm, current.todayGregorian)
                g
            } else {
                current.monthGridCells
            }

            current.copy(
                selectedGregorianDate = gregorianDate,
                selectedBanglaDate = banglaDate,
                displayedBanglaYearMonth = targetBanglaYm,
                displayedGregorianYearMonth = targetGregYm,
                monthGridCells = updatedGrid,
                selectedDateHolidays = holidayRepository.getHolidaysForDate(gregorianDate, banglaDate),
                monthTransitionDirection = direction
            )
        }
    }

    /**
     * Navigates forward (+1) or backward (-1) by one month in the active calendar system.
     */
    fun navigateMonth(monthDelta: Int) {
        if (monthDelta == 0) return
        _uiState.update { current ->
            val newBanglaYm: BanglaYearMonth
            val newGregYm: YearMonth

            when (current.calendarMode) {
                CalendarMode.BANGLA -> {
                    newBanglaYm = current.displayedBanglaYearMonth.plusMonths(monthDelta)
                    val firstDayGreg = BanglaCalendarEngine.getFirstDayOfBanglaMonth(newBanglaYm)
                    newGregYm = YearMonth.from(firstDayGreg)
                }
                CalendarMode.ENGLISH -> {
                    newGregYm = current.displayedGregorianYearMonth.plusMonths(monthDelta.toLong())
                    val firstDayBangla = BanglaCalendarEngine.gregorianToBangla(newGregYm.atDay(1))
                    newBanglaYm = BanglaYearMonth.from(firstDayBangla)
                }
            }

            val newGrid = computeGrid(
                mode = current.calendarMode,
                banglaYearMonth = newBanglaYm,
                gregorianYearMonth = newGregYm,
                today = current.todayGregorian
            )
            preWarmAdjacentMonths(newBanglaYm, newGregYm, current.todayGregorian)

            current.copy(
                displayedBanglaYearMonth = newBanglaYm,
                displayedGregorianYearMonth = newGregYm,
                monthGridCells = newGrid,
                monthTransitionDirection = if (monthDelta > 0) 1 else -1
            )
        }
    }

    fun previousMonth() = navigateMonth(-1)

    fun nextMonth() = navigateMonth(1)

    /**
     * Immediately returns the calendar view and selection to today's date in `Asia/Dhaka`.
     */
    fun jumpToToday() {
        val refreshedToday = BanglaCalendarEngine.today(clock)
        val refreshedTodayBangla = BanglaCalendarEngine.gregorianToBangla(refreshedToday)

        _uiState.update { current ->
            val banglaYm = BanglaYearMonth.from(refreshedTodayBangla)
            val gregYm = YearMonth.from(refreshedToday)
            val grid = computeGrid(
                mode = current.calendarMode,
                banglaYearMonth = banglaYm,
                gregorianYearMonth = gregYm,
                today = refreshedToday
            )
            preWarmAdjacentMonths(banglaYm, gregYm, refreshedToday)
            val todayEvents = holidayRepository.getHolidaysForDate(refreshedToday, refreshedTodayBangla)

            current.copy(
                todayGregorian = refreshedToday,
                todayBangla = refreshedTodayBangla,
                selectedGregorianDate = refreshedToday,
                selectedBanglaDate = refreshedTodayBangla,
                displayedBanglaYearMonth = banglaYm,
                displayedGregorianYearMonth = gregYm,
                monthGridCells = grid,
                selectedDateHolidays = todayEvents,
                todayHolidays = todayEvents,
                monthTransitionDirection = 0
            )
        }
    }

    /**
     * Jumps directly to a specific Bangla date and selects it.
     */
    fun jumpToBanglaDate(year: Int, month: BanglaMonth, day: Int) {
        val maxDay = BanglaCalendarEngine.getDaysInBanglaMonth(year, month)
        val safeDay = day.coerceIn(1, maxDay)
        val gregorianDate = BanglaCalendarEngine.banglaToGregorian(year, month, safeDay)
        selectDate(gregorianDate)
        setDateJumpOpen(false)
    }

    /**
     * Jumps directly to a specific Gregorian date and selects it.
     */
    fun jumpToGregorianDate(year: Int, month: Int, day: Int) {
        val ym = YearMonth.of(year, month.coerceIn(1, 12))
        val safeDay = day.coerceIn(1, ym.lengthOfMonth())
        selectDate(ym.atDay(safeDay))
        setDateJumpOpen(false)
    }

    fun setSettingsOpen(open: Boolean) {
        _uiState.update { it.copy(isSettingsOpen = open) }
    }

    fun setDateJumpOpen(open: Boolean) {
        _uiState.update { it.copy(isDateJumpOpen = open) }
    }

    fun updateThemeMode(themeMode: AppThemeMode) {
        preferencesRepository.setThemeMode(themeMode)
        _uiState.update { it.copy(preferences = preferencesRepository.preferences.value) }
    }

    fun updateDefaultCalendarMode(calendarMode: CalendarMode) {
        preferencesRepository.setDefaultCalendarMode(calendarMode)
        _uiState.update { it.copy(preferences = preferencesRepository.preferences.value) }
        setCalendarMode(calendarMode)
    }

    fun updateInterfaceLanguage(language: InterfaceLanguage) {
        preferencesRepository.setInterfaceLanguage(language)
        _uiState.update { it.copy(preferences = preferencesRepository.preferences.value) }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val prefsRepo = PreferencesRepository(context.applicationContext)
            return CalendarViewModel(preferencesRepository = prefsRepo) as T
        }
    }
}
