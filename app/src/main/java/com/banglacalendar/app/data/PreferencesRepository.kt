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
package com.banglacalendar.app.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Immutable
import com.banglacalendar.app.domain.model.AppThemeMode
import com.banglacalendar.app.domain.model.CalendarMode
import com.banglacalendar.app.domain.model.InterfaceLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Snapshot of persisted user preferences.
 */
@Immutable
data class UserPreferences(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val defaultCalendarMode: CalendarMode = CalendarMode.BANGLA,
    val interfaceLanguage: InterfaceLanguage = InterfaceLanguage.FOLLOW_CALENDAR
)

/**
 * Lightweight, synchronous-startup preferences store with reactive [StateFlow] updates.
 */
class PreferencesRepository(context: Context?) {

    private val prefs: SharedPreferences? =
        context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _preferences = MutableStateFlow(loadPreferences())
    val preferences: StateFlow<UserPreferences> = _preferences.asStateFlow()

    private fun loadPreferences(): UserPreferences {
        val p = prefs ?: return UserPreferences()
        val themeStr = p.getString(KEY_THEME_MODE, AppThemeMode.SYSTEM.name)
        val calModeStr = p.getString(KEY_DEFAULT_CALENDAR_MODE, CalendarMode.BANGLA.name)
        val langStr = p.getString(KEY_INTERFACE_LANGUAGE, InterfaceLanguage.FOLLOW_CALENDAR.name)

        return UserPreferences(
            themeMode = runCatching { AppThemeMode.valueOf(themeStr!!) }.getOrDefault(AppThemeMode.SYSTEM),
            defaultCalendarMode = runCatching { CalendarMode.valueOf(calModeStr!!) }.getOrDefault(CalendarMode.BANGLA),
            interfaceLanguage = runCatching { InterfaceLanguage.valueOf(langStr!!) }.getOrDefault(InterfaceLanguage.FOLLOW_CALENDAR)
        )
    }

    fun setThemeMode(themeMode: AppThemeMode) {
        prefs?.edit()?.putString(KEY_THEME_MODE, themeMode.name)?.apply()
        _preferences.update { it.copy(themeMode = themeMode) }
    }

    fun setDefaultCalendarMode(calendarMode: CalendarMode) {
        prefs?.edit()?.putString(KEY_DEFAULT_CALENDAR_MODE, calendarMode.name)?.apply()
        _preferences.update { it.copy(defaultCalendarMode = calendarMode) }
    }

    fun setInterfaceLanguage(language: InterfaceLanguage) {
        prefs?.edit()?.putString(KEY_INTERFACE_LANGUAGE, language.name)?.apply()
        _preferences.update { it.copy(interfaceLanguage = language) }
    }

    companion object {
        private const val PREFS_NAME = "bangla_calendar_prefs"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_DEFAULT_CALENDAR_MODE = "default_calendar_mode"
        private const val KEY_INTERFACE_LANGUAGE = "interface_language"
    }
}
