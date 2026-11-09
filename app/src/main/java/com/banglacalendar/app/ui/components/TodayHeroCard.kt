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
package com.banglacalendar.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banglacalendar.app.domain.engine.BanglaNumeralFormatter
import com.banglacalendar.app.domain.model.BanglaDate
import com.banglacalendar.app.domain.model.CalendarMode
import com.banglacalendar.app.domain.model.HolidayEvent
import com.banglacalendar.app.ui.theme.GlassTheme
import java.time.LocalDate

private val HeroSmoothOut = CubicBezierEasing(0.22f, 1.0f, 0.36f, 1.0f)

/**
 * Visually impressive translucent Liquid Glass hero card displaying Today's Date
 * in both Bangla and English calendar systems, using a single persistent layout tree
 * and RenderThread `graphicsLayer` transitions for zero-jank mode switching.
 */
@Composable
fun TodayHeroCard(
    todayGregorian: LocalDate,
    todayBangla: BanglaDate,
    calendarMode: CalendarMode,
    todayHolidays: List<HolidayEvent>,
    isViewingToday: Boolean,
    onTodayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GlassTheme.colors
    val reducedMotion = rememberIsReducedMotionEnabled()
    val isBanglaMode = calendarMode == CalendarMode.BANGLA
    val pillShape = remember { RoundedCornerShape(50) }
    val boxShape = remember { RoundedCornerShape(18.dp) }
    val bannerShape = remember { RoundedCornerShape(14.dp) }

    val modeTransitionProgress = remember { Animatable(1f) }
    var lastMode by remember { mutableStateOf(calendarMode) }

    LaunchedEffect(calendarMode) {
        if (calendarMode != lastMode) {
            lastMode = calendarMode
            if (!reducedMotion) {
                modeTransitionProgress.snapTo(0.20f)
                withFrameNanos { }
                modeTransitionProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 190, easing = HeroSmoothOut)
                )
            }
        }
    }

    val centeredBadgeTextStyle = MaterialTheme.typography.labelMedium.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.5.sp,
        lineHeight = 14.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    )

    val primaryDayText = when (calendarMode) {
        CalendarMode.BANGLA -> todayBangla.dayBengali
        CalendarMode.ENGLISH -> todayGregorian.dayOfMonth.toString()
    }
    val primaryTitleText = when (calendarMode) {
        CalendarMode.BANGLA -> "${todayBangla.month.bengaliName} ${todayBangla.yearBengali} বঙ্গাব্দ"
        CalendarMode.ENGLISH -> "${BanglaNumeralFormatter.getGregorianMonthEnglish(todayGregorian.month)} ${todayGregorian.year}"
    }
    val secondarySubtitleText = when (calendarMode) {
        CalendarMode.BANGLA -> "${todayBangla.weekdayBengali} • ${
            BanglaNumeralFormatter.formatGregorianDate(todayGregorian, useBengali = false)
        }"
        CalendarMode.ENGLISH -> "${todayBangla.weekdayEnglish} • ${todayBangla.formatBengali(includeBongabdoSuffix = false)}"
    }

    LiquidGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("today_hero_card"),
        cornerRadius = 26.dp,
        isElevated = true,
        accentTint = colors.todayGlow.copy(alpha = if (colors.isDark) 0.12f else 0.20f),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
        onClick = onTodayClick
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top badge row: fixed 30.dp height so the TODAY bubble, dot, season pill, and Return button never shift or misalign
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    // Glowing TODAY Indicator Pill (perfectly centered dot + single-line label)
                    Row(
                        modifier = Modifier
                            .height(28.dp)
                            .clip(pillShape)
                            .background(colors.accentContainer, pillShape)
                            .border(
                                width = 1.dp,
                                color = colors.accentPrimary.copy(alpha = 0.38f),
                                shape = pillShape
                            )
                            .padding(horizontal = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.5.dp)
                                .clip(CircleShape)
                                .background(colors.accentPrimary, CircleShape)
                        )
                        Text(
                            text = if (isBanglaMode) "আজ • TODAY" else "TODAY",
                            style = centeredBadgeTextStyle.copy(fontWeight = FontWeight.Bold),
                            color = colors.accentPrimary,
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    // Bangla Season (ষড়ঋতু) Pill — concise single-line label
                    Row(
                        modifier = Modifier
                            .height(28.dp)
                            .clip(pillShape)
                            .background(colors.pillTrackBackground, pillShape)
                            .border(
                                width = 1.dp,
                                color = colors.glassBorderHighlight,
                                shape = pillShape
                            )
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = colors.accentSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = todayBangla.season.compactDisplayName(useBengali = isBanglaMode),
                            style = centeredBadgeTextStyle,
                            color = colors.textSecondary,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Smooth fade-only Return to Today pill that matches the exact 28.dp height of the row
                AnimatedVisibility(
                    visible = !isViewingToday,
                    enter = fadeIn(tween(if (reducedMotion) 0 else 160)),
                    exit = fadeOut(tween(if (reducedMotion) 0 else 120))
                ) {
                    LiquidGlassPillButton(
                        text = if (isBanglaMode) "আজকে ফিরুন" else "Reset",
                        icon = Icons.Rounded.Today,
                        onClick = onTodayClick,
                        isHighlighted = true,
                        modifier = Modifier
                            .height(28.dp)
                            .testTag("return_to_today_button")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Hero Content with locked height & RenderThread mode transition
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(66.dp)
                    .graphicsLayer {
                        val p = modeTransitionProgress.value
                        alpha = p
                        val s = 0.97f + (0.03f * p)
                        scaleX = s
                        scaleY = s
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Fixed-size Sculptural Day Numeral Box (identical 72x64dp in both modes)
                Box(
                    modifier = Modifier
                        .width(72.dp)
                        .height(64.dp)
                        .clip(boxShape)
                        .background(colors.pillTrackBackground, boxShape)
                        .border(
                            width = 1.dp,
                            color = colors.glassBorderHighlight,
                            shape = boxShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = primaryDayText,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            lineHeight = 36.sp
                        ),
                        color = colors.accentPrimary,
                        maxLines = 1,
                        modifier = Modifier.testTag("today_primary_day_numeral")
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = primaryTitleText,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            lineHeight = 25.sp
                        ),
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = secondarySubtitleText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            lineHeight = 19.sp
                        ),
                        color = colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // If today itself is a special day or holiday, show a subtle single-line glass banner
            if (todayHolidays.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                val primaryHoliday = todayHolidays.first()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(bannerShape)
                        .background(colors.weekendSurfaceTint, bannerShape)
                        .border(
                            width = 1.dp,
                            color = colors.holidayNationalDot.copy(alpha = 0.28f),
                            shape = bannerShape
                        )
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(
                                color = if (primaryHoliday.isPublicHoliday) colors.holidayNationalDot
                                else colors.holidayCulturalDot,
                                shape = CircleShape
                            )
                    )
                    Text(
                        text = primaryHoliday.title(useBengali = isBanglaMode),
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
