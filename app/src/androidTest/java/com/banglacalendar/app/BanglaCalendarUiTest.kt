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
package com.banglacalendar.app

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.banglacalendar.app.data.PreferencesRepository
import com.banglacalendar.app.ui.screens.BanglaCalendarHomeScreen
import com.banglacalendar.app.ui.theme.BanglaCalendarTheme
import com.banglacalendar.app.ui.viewmodel.CalendarViewModel
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class BanglaCalendarUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fixedClock = Clock.fixed(
        Instant.parse("2026-02-21T06:00:00Z"),
        ZoneId.of("Asia/Dhaka")
    )

    @Test
    fun homeScreen_togglesModeAndPreservesSelectedDateAndNavigatesMonths() {
        val viewModel = CalendarViewModel(
            preferencesRepository = PreferencesRepository(context = null),
            clock = fixedClock
        )

        composeTestRule.setContent {
            val uiState by viewModel.uiState.collectAsState()
            BanglaCalendarTheme(themeMode = uiState.preferences.themeMode) {
                BanglaCalendarHomeScreen(
                    uiState = uiState,
                    onModeChange = viewModel::setCalendarMode,
                    onSelectDate = viewModel::selectDate,
                    onPreviousMonth = viewModel::previousMonth,
                    onNextMonth = viewModel::nextMonth,
                    onJumpToToday = viewModel::jumpToToday,
                    onOpenSettings = { viewModel.setSettingsOpen(true) },
                    onCloseSettings = { viewModel.setSettingsOpen(false) },
                    onOpenDateJump = { viewModel.setDateJumpOpen(true) },
                    onCloseDateJump = { viewModel.setDateJumpOpen(false) },
                    onJumpToBanglaDate = viewModel::jumpToBanglaDate,
                    onJumpToGregorianDate = viewModel::jumpToGregorianDate,
                    onThemeModeChange = viewModel::updateThemeMode,
                    onDefaultCalendarModeChange = viewModel::updateDefaultCalendarMode,
                    onInterfaceLanguageChange = viewModel::updateInterfaceLanguage
                )
            }
        }

        // Verify title and hero numeral in Bangla mode (21 Feb 2026 = ৮ ফাল্গুন ১৪৩২)
        composeTestRule.onNodeWithTag("app_title_text").assertTextContains("DinKhon")
        composeTestRule.onNodeWithTag("today_primary_day_numeral").assertTextContains("৮")
        composeTestRule.onNodeWithTag("detail_bangla_date_text").assertTextContains("৮ ফাল্গুন ১৪৩২")
        composeTestRule.onNodeWithTag("detail_english_date_text").assertTextContains("21 February 2026")
        composeTestRule.onNodeWithTag("detail_english_weekday_season_text").assertTextContains("Saturday • Spring")

        // Switch to English mode and verify same date is preserved while primary hero numeral becomes "21"
        composeTestRule.onNodeWithTag("toggle_english_button").performClick()
        composeTestRule.onNodeWithTag("today_primary_day_numeral").assertTextContains("21")
        composeTestRule.onNodeWithTag("detail_bangla_date_text").assertTextContains("৮ ফাল্গুন ১৪৩২")
        composeTestRule.onNodeWithTag("detail_english_date_text").assertTextContains("21 February 2026")

        // Switch back to Bangla mode
        composeTestRule.onNodeWithTag("toggle_bangla_button").performClick()
        composeTestRule.onNodeWithTag("today_primary_day_numeral").assertTextContains("৮")

        // Navigate to next month (Choitro 1432) and select 26 March 2026 (১২ চৈত্র ১৪৩২)
        composeTestRule.onNodeWithTag("next_month_button").performClick()
        composeTestRule.onNodeWithTag("date_cell_2026-03-26").performClick()
        composeTestRule.onNodeWithTag("detail_bangla_date_text").assertTextContains("১২ চৈত্র ১৪৩২")
        composeTestRule.onNodeWithText("মহান স্বাধীনতা ও জাতীয় দিবস").assertIsDisplayed()
    }
}
