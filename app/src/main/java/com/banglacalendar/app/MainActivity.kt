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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.banglacalendar.app.domain.model.AppThemeMode
import com.banglacalendar.app.ui.screens.BanglaCalendarHomeScreen
import com.banglacalendar.app.ui.theme.BanglaCalendarTheme
import com.banglacalendar.app.ui.viewmodel.CalendarViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: CalendarViewModel = viewModel(
                factory = CalendarViewModel.Factory(applicationContext)
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            val isDark = resolveDarkTheme(uiState.preferences.themeMode)
            LaunchedEffect(isDark) {
                enableEdgeToEdge(
                    statusBarStyle = if (isDark) {
                        SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(
                            android.graphics.Color.TRANSPARENT,
                            android.graphics.Color.TRANSPARENT
                        )
                    },
                    navigationBarStyle = if (isDark) {
                        SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(
                            android.graphics.Color.TRANSPARENT,
                            android.graphics.Color.TRANSPARENT
                        )
                    }
                )
            }

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
    }
}

@Composable
private fun resolveDarkTheme(themeMode: AppThemeMode): Boolean = when (themeMode) {
    AppThemeMode.SYSTEM -> isSystemInDarkTheme()
    AppThemeMode.LIGHT -> false
    AppThemeMode.DARK -> true
}
