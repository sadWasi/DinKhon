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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.EventAvailable
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
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
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private val DetailBezier = CubicBezierEasing(0.22f, 1.0f, 0.36f, 1.0f)
private val PillShape = RoundedCornerShape(50)
private val BoxShape = RoundedCornerShape(18.dp)
private val HolidayShape = RoundedCornerShape(16.dp)

/**
 * Smoothly animated Liquid Glass detail panel displaying the selected date's
 * full Bangla date, full English date, weekday and season in both languages,
 * holiday/special-day details, and a one-tap Copy Date action.
 *
 * Uses a single persistent layout tree with a GPU-accelerated `graphicsLayer`
 * transition synchronized via `withFrameNanos` so selecting a date is silky-smooth
 * with zero layout thrashing.
 */
@Composable
fun SelectedDateDetailCard(
    selectedGregorianDate: LocalDate,
    selectedBanglaDate: BanglaDate,
    todayGregorianDate: LocalDate,
    calendarMode: CalendarMode,
    holidays: List<HolidayEvent>,
    useBengaliChrome: Boolean,
    formattedCopyString: String,
    modifier: Modifier = Modifier
) {
    val colors = GlassTheme.colors
    val reducedMotion = rememberIsReducedMotionEnabled()
    val clipboardManager = LocalClipboardManager.current
    var isCopied by remember(selectedGregorianDate) { mutableStateOf(false) }

    val detailTransition = remember { Animatable(1f) }
    var lastSelectedDate by remember { mutableStateOf(selectedGregorianDate) }

    LaunchedEffect(selectedGregorianDate) {
        if (lastSelectedDate != selectedGregorianDate) {
            lastSelectedDate = selectedGregorianDate
            if (!reducedMotion) {
                detailTransition.snapTo(0.25f)
                withFrameNanos { }
                detailTransition.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 200, easing = DetailBezier)
                )
            } else {
                detailTransition.snapTo(1f)
            }
        }
    }

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(1800)
            isCopied = false
        }
    }

    val daysOffset = remember(todayGregorianDate, selectedGregorianDate) {
        ChronoUnit.DAYS.between(todayGregorianDate, selectedGregorianDate).toInt()
    }
    val useBengaliLabel = calendarMode == CalendarMode.BANGLA || useBengaliChrome
    val relativeLabel = remember(daysOffset, useBengaliLabel) {
        formatRelativeDayLabel(daysOffset = daysOffset, useBengali = useBengaliLabel)
    }
    val isWeekend = remember(selectedGregorianDate) {
        BanglaNumeralFormatter.isBangladeshWeekend(selectedGregorianDate.dayOfWeek)
    }
    val formattedBanglaDate = remember(selectedBanglaDate) {
        selectedBanglaDate.formatBengali(includeBongabdoSuffix = false)
    }
    val formattedEnglishDate = remember(selectedGregorianDate) {
        BanglaNumeralFormatter.formatGregorianDate(date = selectedGregorianDate, useBengali = false)
    }

    LiquidGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("selected_date_detail_card"),
        cornerRadius = 26.dp,
        isElevated = false,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 15.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    val p = detailTransition.value
                    alpha = p
                    translationY = (1f - p) * 10f
                }
        ) {
            // Header row: fixed 30.dp height so toggling modes never shifts height
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.EventAvailable,
                        contentDescription = null,
                        tint = colors.accentPrimary,
                        modifier = Modifier.size(17.dp)
                    )
                    Text(
                        text = if (useBengaliChrome) "নির্বাচিত তারিখ" else "Selected Date",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            lineHeight = 16.sp,
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        ),
                        color = colors.accentPrimary,
                        maxLines = 1
                    )

                    Box(
                        modifier = Modifier
                            .height(24.dp)
                            .clip(PillShape)
                            .background(colors.pillTrackBackground, PillShape)
                            .border(
                                width = 1.dp,
                                color = colors.glassBorderSubtle,
                                shape = PillShape
                            )
                            .padding(horizontal = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = relativeLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                lineHeight = 13.sp,
                                platformStyle = PlatformTextStyle(includeFontPadding = false)
                            ),
                            color = colors.textSecondary,
                            maxLines = 1
                        )
                    }
                }

                LiquidGlassPillButton(
                    text = when {
                        isCopied && useBengaliChrome -> "কপি হয়েছে"
                        isCopied -> "Copied"
                        useBengaliChrome -> "কপি"
                        else -> "Copy"
                    },
                    icon = if (isCopied) Icons.Rounded.Check else Icons.Rounded.ContentCopy,
                    isHighlighted = isCopied,
                    onClick = {
                        clipboardManager.setText(AnnotatedString(formattedCopyString))
                        isCopied = true
                    },
                    modifier = Modifier
                        .height(28.dp)
                        .testTag("copy_date_button")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Symmetrical, Equal-Height Dual Calendar Equivalent Cards (Bangla & English side-by-side)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Full Bangla Date Box
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(BoxShape)
                        .background(colors.pillTrackBackground, BoxShape)
                        .border(
                            width = 1.dp,
                            color = if (calendarMode == CalendarMode.BANGLA) {
                                colors.accentPrimary.copy(alpha = 0.52f)
                            } else {
                                colors.glassBorderHighlight
                            },
                            shape = BoxShape
                        )
                        .padding(horizontal = 12.dp, vertical = 11.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "বাংলা তারিখ • বঙ্গাব্দ",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.5.sp,
                            lineHeight = 14.sp
                        ),
                        color = colors.textSecondary,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formattedBanglaDate,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp,
                            lineHeight = 20.sp
                        ),
                        color = colors.textPrimary,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("detail_bangla_date_text")
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${selectedBanglaDate.weekdayBengali} • ${selectedBanglaDate.season.bengaliName} ঋতু",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        ),
                        color = colors.accentPrimary,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Full English Date Box (locked to exact same height & 3-line symmetry as Bangla box)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(BoxShape)
                        .background(colors.pillTrackBackground, BoxShape)
                        .border(
                            width = 1.dp,
                            color = if (calendarMode == CalendarMode.ENGLISH) {
                                colors.accentPrimary.copy(alpha = 0.52f)
                            } else {
                                colors.glassBorderHighlight
                            },
                            shape = BoxShape
                        )
                        .padding(horizontal = 12.dp, vertical = 11.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "English • Gregorian",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.5.sp,
                            lineHeight = 14.sp
                        ),
                        color = colors.textSecondary,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formattedEnglishDate,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp,
                            lineHeight = 20.sp
                        ),
                        color = colors.textPrimary,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("detail_english_date_text")
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${selectedBanglaDate.weekdayEnglish} • ${selectedBanglaDate.season.englishSeasonOnly}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        ),
                        color = colors.accentPrimary,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("detail_english_weekday_season_text")
                    )
                }
            }

            // Weekend indicator if Friday or Saturday
            if (isWeekend) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(colors.weekendText, CircleShape)
                    )
                    Text(
                        text = if (useBengaliChrome) {
                            "সাপ্তাহিক ছুটির দিন (${selectedBanglaDate.weekdayBengali})"
                        } else {
                            "Bangladesh Weekend (${selectedBanglaDate.weekdayEnglish})"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.weekendText,
                        maxLines = 1
                    )
                }
            }

            // Holidays & Special Days Section
            if (holidays.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.testTag("detail_holiday_list")
                ) {
                    holidays.forEach { holiday ->
                        val badgeColor = if (holiday.isPublicHoliday) {
                            colors.holidayNationalDot
                        } else {
                            colors.holidayCulturalDot
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(HolidayShape)
                                .background(
                                    color = if (holiday.isPublicHoliday) colors.weekendSurfaceTint
                                    else colors.pillTrackBackground,
                                    shape = HolidayShape
                                )
                                .border(
                                    width = 1.dp,
                                    color = badgeColor.copy(alpha = 0.35f),
                                    shape = HolidayShape
                                )
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(7.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(badgeColor, CircleShape)
                                )
                                Text(
                                    text = holiday.category.label(useBengali = useBengaliChrome),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = badgeColor
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = holiday.titleBengali,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = colors.textPrimary
                            )
                            Text(
                                text = holiday.titleEnglish,
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.textSecondary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = holiday.description(useBengali = useBengaliChrome),
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatRelativeDayLabel(daysOffset: Int, useBengali: Boolean): String {
    return when {
        daysOffset == 0 -> if (useBengali) "আজ" else "Today"
        daysOffset == 1 -> if (useBengali) "আগামীকাল" else "Tomorrow"
        daysOffset == -1 -> if (useBengali) "গতকাল" else "Yesterday"
        daysOffset > 1 -> if (useBengali) {
            "${BanglaNumeralFormatter.toBengaliDigits(daysOffset)} দিন পর"
        } else {
            "In $daysOffset days"
        }
        else -> {
            val absDays = -daysOffset
            if (useBengali) {
                "${BanglaNumeralFormatter.toBengaliDigits(absDays)} দিন আগে"
            } else {
                "$absDays days ago"
            }
        }
    }
}
