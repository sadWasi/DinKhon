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

package com.banglacalendar.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banglacalendar.app.domain.model.AppThemeMode
import com.banglacalendar.app.domain.model.BanglaMonth
import com.banglacalendar.app.domain.model.CalendarMode
import com.banglacalendar.app.domain.model.InterfaceLanguage
import com.banglacalendar.app.ui.components.DateJumpConverterSheet
import com.banglacalendar.app.ui.components.LiquidGlassBackground
import com.banglacalendar.app.ui.components.LiquidGlassIconButton
import com.banglacalendar.app.ui.components.MonthlyCalendarCard
import com.banglacalendar.app.ui.components.SegmentedCalendarModeSwitch
import com.banglacalendar.app.ui.components.SelectedDateDetailCard
import com.banglacalendar.app.ui.components.SettingsBottomSheet
import com.banglacalendar.app.ui.components.TodayHeroCard
import com.banglacalendar.app.ui.components.rememberIsReducedMotionEnabled
import com.banglacalendar.app.ui.theme.GlassTheme
import com.banglacalendar.app.ui.viewmodel.CalendarUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

private val IntroBezier = CubicBezierEasing(0.22f, 1.0f, 0.36f, 1.0f)

/**
 * Primary home screen of "DinKhon (দিনক্ষণ • বাংলা ক্যালেন্ডার)".
 * Features a short, simple, clearly visible startup intro animation and responsive layout.
 */
@Composable
fun BanglaCalendarHomeScreen(
    uiState: CalendarUiState,
    onModeChange: (CalendarMode) -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onJumpToToday: () -> Unit,
    onOpenSettings: () -> Unit,
    onCloseSettings: () -> Unit,
    onOpenDateJump: () -> Unit,
    onCloseDateJump: () -> Unit,
    onJumpToBanglaDate: (Int, BanglaMonth, Int) -> Unit,
    onJumpToGregorianDate: (Int, Int, Int) -> Unit,
    onThemeModeChange: (AppThemeMode) -> Unit,
    onDefaultCalendarModeChange: (CalendarMode) -> Unit,
    onInterfaceLanguageChange: (InterfaceLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GlassTheme.colors
    val reducedMotion = rememberIsReducedMotionEnabled()
    val density = LocalDensity.current

    var hasPlayedIntro by rememberSaveable { mutableStateOf(false) }
    val shouldAnimateIntro = !reducedMotion && !hasPlayedIntro
    var isIntroOverlayVisible by remember { mutableStateOf(shouldAnimateIntro) }

    val introOverlayAlpha = remember { Animatable(if (shouldAnimateIntro) 1f else 0f) }
    val introOverlayScale = remember { Animatable(if (shouldAnimateIntro) 0.90f else 1f) }
    val contentCascade = remember { Animatable(if (shouldAnimateIntro) 0f else 1f) }

    val slideDistancePx = remember(density) { with(density) { 36.dp.toPx() } }
    val formattedCopyString = remember(
        uiState.selectedGregorianDate,
        uiState.selectedBanglaDate,
        uiState.selectedDateHolidays
    ) {
        uiState.formatSelectedDateForCopy()
    }

    LaunchedEffect(shouldAnimateIntro) {
        if (shouldAnimateIntro) {
            // Wait for the initial composition & layout frame to settle before starting the animation clock
            withFrameNanos { }
            delay(80)
            withFrameNanos { }
            launch {
                introOverlayScale.animateTo(
                    targetValue = 1.04f,
                    animationSpec = tween(durationMillis = 380, easing = IntroBezier)
                )
            }
            launch {
                delay(180)
                introOverlayAlpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 280, easing = IntroBezier)
                )
                isIntroOverlayVisible = false
            }
            launch {
                delay(120)
                contentCascade.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 540, easing = IntroBezier)
                )
                hasPlayedIntro = true
            }
        } else {
            isIntroOverlayVisible = false
            introOverlayAlpha.snapTo(0f)
            contentCascade.snapTo(1f)
        }
    }

    LiquidGlassBackground(modifier = modifier) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .displayCutoutPadding()
        ) {
            val isWideLayout = maxWidth >= 700.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 1040.dp),
                    verticalArrangement = Arrangement.spacedBy(13.dp)
                ) {
                    // Top Header Bar: Title + Actions + Segmented বাংলা | English Switch
                    HomeTopBar(
                        selectedMode = uiState.calendarMode,
                        isViewingToday = uiState.isViewingToday,
                        onModeChange = onModeChange,
                        onJumpToToday = onJumpToToday,
                        onOpenDateJump = onOpenDateJump,
                        onOpenSettings = onOpenSettings,
                        modifier = Modifier.graphicsLayer {
                            val p = ((contentCascade.value - 0.0f) / 0.60f).coerceIn(0f, 1f)
                            alpha = p
                            translationY = (1f - p) * (-slideDistancePx * 0.6f)
                        }
                    )

                    if (isWideLayout) {
                        // Two-column responsive layout for tablets / foldables / landscape
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(
                                modifier = Modifier.weight(0.95f),
                                verticalArrangement = Arrangement.spacedBy(13.dp)
                            ) {
                                TodayHeroCard(
                                    todayGregorian = uiState.todayGregorian,
                                    todayBangla = uiState.todayBangla,
                                    calendarMode = uiState.calendarMode,
                                    todayHolidays = uiState.todayHolidays,
                                    isViewingToday = uiState.isViewingToday,
                                    onTodayClick = onJumpToToday,
                                    modifier = Modifier.graphicsLayer {
                                        val p = ((contentCascade.value - 0.10f) / 0.68f).coerceIn(0f, 1f)
                                        alpha = p
                                        val s = 0.92f + (0.08f * p)
                                        scaleX = s
                                        scaleY = s
                                        translationY = (1f - p) * (slideDistancePx * 0.75f)
                                    }
                                )

                                SelectedDateDetailCard(
                                    selectedGregorianDate = uiState.selectedGregorianDate,
                                    selectedBanglaDate = uiState.selectedBanglaDate,
                                    todayGregorianDate = uiState.todayGregorian,
                                    calendarMode = uiState.calendarMode,
                                    holidays = uiState.selectedDateHolidays,
                                    useBengaliChrome = uiState.useBengaliChrome,
                                    formattedCopyString = formattedCopyString,
                                    modifier = Modifier.graphicsLayer {
                                        val p = ((contentCascade.value - 0.26f) / 0.74f).coerceIn(0f, 1f)
                                        alpha = p
                                        val s = 0.94f + (0.06f * p)
                                        scaleX = s
                                        scaleY = s
                                        translationY = (1f - p) * slideDistancePx
                                    }
                                )
                            }

                            MonthlyCalendarCard(
                                primaryMonthHeading = uiState.primaryMonthHeading,
                                secondaryMonthSubtitle = uiState.secondaryMonthSubtitle,
                                calendarMode = uiState.calendarMode,
                                monthGridCells = uiState.monthGridCells,
                                selectedGregorianDate = uiState.selectedGregorianDate,
                                onSelectDate = onSelectDate,
                                onPreviousMonth = onPreviousMonth,
                                onNextMonth = onNextMonth,
                                onOpenDateJump = onOpenDateJump,
                                modifier = Modifier
                                    .weight(1.05f)
                                    .graphicsLayer {
                                        val p = ((contentCascade.value - 0.18f) / 0.72f).coerceIn(0f, 1f)
                                        alpha = p
                                        val s = 0.93f + (0.07f * p)
                                        scaleX = s
                                        scaleY = s
                                        translationY = (1f - p) * (slideDistancePx * 0.9f)
                                    }
                            )
                        }
                    } else {
                        // Standard portrait layout
                        TodayHeroCard(
                            todayGregorian = uiState.todayGregorian,
                            todayBangla = uiState.todayBangla,
                            calendarMode = uiState.calendarMode,
                            todayHolidays = uiState.todayHolidays,
                            isViewingToday = uiState.isViewingToday,
                            onTodayClick = onJumpToToday,
                            modifier = Modifier.graphicsLayer {
                                val p = ((contentCascade.value - 0.10f) / 0.68f).coerceIn(0f, 1f)
                                alpha = p
                                val s = 0.92f + (0.08f * p)
                                scaleX = s
                                scaleY = s
                                translationY = (1f - p) * (slideDistancePx * 0.75f)
                            }
                        )

                        MonthlyCalendarCard(
                            primaryMonthHeading = uiState.primaryMonthHeading,
                            secondaryMonthSubtitle = uiState.secondaryMonthSubtitle,
                            calendarMode = uiState.calendarMode,
                            monthGridCells = uiState.monthGridCells,
                            selectedGregorianDate = uiState.selectedGregorianDate,
                            onSelectDate = onSelectDate,
                            onPreviousMonth = onPreviousMonth,
                            onNextMonth = onNextMonth,
                            onOpenDateJump = onOpenDateJump,
                            modifier = Modifier.graphicsLayer {
                                val p = ((contentCascade.value - 0.18f) / 0.72f).coerceIn(0f, 1f)
                                alpha = p
                                val s = 0.93f + (0.07f * p)
                                scaleX = s
                                scaleY = s
                                translationY = (1f - p) * (slideDistancePx * 0.9f)
                            }
                        )

                        SelectedDateDetailCard(
                            selectedGregorianDate = uiState.selectedGregorianDate,
                            selectedBanglaDate = uiState.selectedBanglaDate,
                            todayGregorianDate = uiState.todayGregorian,
                            calendarMode = uiState.calendarMode,
                            holidays = uiState.selectedDateHolidays,
                            useBengaliChrome = uiState.useBengaliChrome,
                            formattedCopyString = formattedCopyString,
                            modifier = Modifier.graphicsLayer {
                                val p = ((contentCascade.value - 0.26f) / 0.74f).coerceIn(0f, 1f)
                                alpha = p
                                val s = 0.94f + (0.06f * p)
                                scaleX = s
                                scaleY = s
                                translationY = (1f - p) * slideDistancePx
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }

        // Short, Simple Startup Intro Emblem Overlay (animated strictly in graphicsLayer with zero recompositions)
        if (isIntroOverlayVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = introOverlayAlpha.value
                    },
                contentAlignment = Alignment.Center
            ) {
                val badgeShape = RoundedCornerShape(28.dp)
                Column(
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = introOverlayScale.value
                            scaleY = introOverlayScale.value
                        }
                        .clip(badgeShape)
                        .background(colors.glassElevatedTop, badgeShape)
                        .border(1.dp, colors.glassBorderHighlight, badgeShape)
                        .padding(horizontal = 28.dp, vertical = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.accentContainer, RoundedCornerShape(16.dp))
                            .border(1.dp, colors.accentPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CalendarMonth,
                            contentDescription = null,
                            tint = colors.accentPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Text(
                        text = "DinKhon",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = colors.textPrimary
                    )
                    Text(
                        text = "দিনক্ষণ • বাংলা ক্যালেন্ডার",
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.accentPrimary
                    )
                }
            }
        }

        // Full-Screen Animated Settings Overlay (synchronized via withFrameNanos + graphicsLayer)
        SmoothFullScreenOverlay(
            visible = uiState.isSettingsOpen,
            reducedMotion = reducedMotion
        ) {
            SettingsBottomSheet(
                preferences = uiState.preferences,
                useBengaliChrome = uiState.useBengaliChrome,
                onThemeModeChange = onThemeModeChange,
                onDefaultCalendarModeChange = onDefaultCalendarModeChange,
                onInterfaceLanguageChange = onInterfaceLanguageChange,
                onDismiss = onCloseSettings
            )
        }

        // Full-Screen Animated Date Converter & Quick Jump Overlay
        SmoothFullScreenOverlay(
            visible = uiState.isDateJumpOpen,
            reducedMotion = reducedMotion
        ) {
            DateJumpConverterSheet(
                initialGregorianDate = uiState.selectedGregorianDate,
                initialBanglaDate = uiState.selectedBanglaDate,
                initialMode = uiState.calendarMode,
                useBengaliChrome = uiState.useBengaliChrome,
                onJumpToBanglaDate = onJumpToBanglaDate,
                onJumpToGregorianDate = onJumpToGregorianDate,
                onDismiss = onCloseDateJump
            )
        }
    }
}

/**
 * Synchronizes full-screen overlay enter/exit transitions with `withFrameNanos`
 * and executes all alpha, scale, and vertical glide interpolations on the GPU RenderThread
 * inside `Modifier.graphicsLayer` so the initial composition pass never drops a frame.
 */
@Composable
private fun SmoothFullScreenOverlay(
    visible: Boolean,
    reducedMotion: Boolean,
    content: @Composable () -> Unit
) {
    var shouldCompose by remember { mutableStateOf(visible) }
    val progress = remember { Animatable(if (visible) 1f else 0f) }
    val density = LocalDensity.current
    val slideOffsetPx = remember(density) { with(density) { 28.dp.toPx() } }

    LaunchedEffect(visible, reducedMotion) {
        if (visible) {
            shouldCompose = true
            if (reducedMotion) {
                progress.snapTo(1f)
            } else {
                progress.snapTo(0f)
                // Wait for the initial composition & layout pass of the sheet before starting clock
                withFrameNanos { }
                progress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 220, easing = IntroBezier)
                )
            }
        } else {
            if (reducedMotion) {
                progress.snapTo(0f)
                shouldCompose = false
            } else {
                progress.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 175, easing = IntroBezier)
                )
                shouldCompose = false
            }
        }
    }

    if (shouldCompose) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val p = progress.value
                    alpha = p
                    val s = 0.965f + (0.035f * p)
                    scaleX = s
                    scaleY = s
                    translationY = (1f - p) * slideOffsetPx
                }
        ) {
            content()
        }
    }
}

@Composable
private fun HomeTopBar(
    selectedMode: CalendarMode,
    isViewingToday: Boolean,
    onModeChange: (CalendarMode) -> Unit,
    onJumpToToday: () -> Unit,
    onOpenDateJump: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GlassTheme.colors
    val reducedMotion = rememberIsReducedMotionEnabled()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = "DinKhon",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        lineHeight = 30.sp,
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    ),
                    color = colors.textPrimary,
                    maxLines = 1,
                    modifier = Modifier.testTag("app_title_text")
                )
                Text(
                    text = "দিনক্ষণ • বাংলা ক্যালেন্ডার",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 11.5.sp,
                        lineHeight = 15.sp,
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    ),
                    color = colors.textSecondary,
                    maxLines = 1
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnimatedVisibility(
                    visible = !isViewingToday,
                    enter = fadeIn(tween(if (reducedMotion) 0 else 180)) + scaleIn(tween(if (reducedMotion) 0 else 180)),
                    exit = fadeOut(tween(if (reducedMotion) 0 else 140)) + scaleOut(tween(if (reducedMotion) 0 else 140))
                ) {
                    LiquidGlassIconButton(
                        icon = Icons.Rounded.Today,
                        contentDescription = "আজকের তারিখে ফিরুন / Jump to Today",
                        onClick = onJumpToToday,
                        tint = colors.accentPrimary,
                        modifier = Modifier.testTag("top_today_button")
                    )
                }

                LiquidGlassIconButton(
                    icon = Icons.Rounded.SwapHoriz,
                    contentDescription = "তারিখ রূপান্তর / Date Converter",
                    onClick = onOpenDateJump,
                    modifier = Modifier.testTag("open_converter_button")
                )

                LiquidGlassIconButton(
                    icon = Icons.Rounded.Settings,
                    contentDescription = "সেটিংস / Settings",
                    onClick = onOpenSettings,
                    modifier = Modifier.testTag("open_settings_button")
                )
            }
        }

        // Prominent Segmented বাংলা | English Switch
        SegmentedCalendarModeSwitch(
            selectedMode = selectedMode,
            onModeChange = onModeChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
