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
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banglacalendar.app.domain.engine.BanglaNumeralFormatter
import com.banglacalendar.app.domain.model.CalendarDayCell
import com.banglacalendar.app.domain.model.CalendarMode
import com.banglacalendar.app.ui.theme.GlassTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate

private val MetricSafeTextStyle = PlatformTextStyle(
    includeFontPadding = false
)

private val SmoothOutEasing = CubicBezierEasing(0.16f, 1.0f, 0.30f, 1.0f)

/**
 * 7-column monthly calendar grid inside a frosted Liquid Glass container.
 *
 * Uses interactive finger-follow resistance coupled with a `withFrameNanos`-synchronized
 * `Animatable` read strictly inside `Modifier.graphicsLayer` so month swipes respond on the
 * very first touch pixel and glide at 60/120fps without frame-skipping.
 */
@Composable
fun MonthlyCalendarCard(
    primaryMonthHeading: String,
    secondaryMonthSubtitle: String,
    calendarMode: CalendarMode,
    monthGridCells: List<CalendarDayCell>,
    selectedGregorianDate: LocalDate,
    onSelectDate: (LocalDate) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onOpenDateJump: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GlassTheme.colors
    val reducedMotion = rememberIsReducedMotionEnabled()
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    val currentOnPreviousMonth by rememberUpdatedState(onPreviousMonth)
    val currentOnNextMonth by rememberUpdatedState(onNextMonth)

    val swipeTriggerDistancePx = remember(density) { with(density) { 22.dp.toPx() } }
    val slideDistancePx = remember(density) { with(density) { 26.dp.toPx() } }
    val maxFingerFollowPx = remember(density) { with(density) { 16.dp.toPx() } }

    // Single Animatable driving both translationX and alpha on the RenderThread (1f = settled)
    val transitionProgress = remember { Animatable(1f) }
    val fingerFollowOffsetPx = remember { Animatable(0f) }
    var activeDirection by remember { mutableFloatStateOf(1f) }
    var transitionJob by remember { mutableStateOf<Job?>(null) }
    var previousMode by remember { mutableStateOf(calendarMode) }

    androidx.compose.runtime.LaunchedEffect(calendarMode) {
        if (calendarMode != previousMode) {
            previousMode = calendarMode
            if (!reducedMotion) {
                transitionJob?.cancel()
                activeDirection = 0f
                fingerFollowOffsetPx.snapTo(0f)
                transitionProgress.snapTo(0.28f)
                withFrameNanos { }
                transitionProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 200, easing = SmoothOutEasing)
                )
            }
        }
    }

    fun animateCleanMonthTransition(direction: Int) {
        if (reducedMotion) {
            if (direction > 0) currentOnNextMonth() else currentOnPreviousMonth()
            return
        }

        transitionJob?.cancel()
        transitionJob = coroutineScope.launch {
            activeDirection = if (direction >= 0) 1f else -1f
            fingerFollowOffsetPx.snapTo(0f)
            // 1. Pre-position before state swap so the incoming month never flashes at x=0
            transitionProgress.snapTo(0f)
            if (direction > 0) {
                currentOnNextMonth()
            } else {
                currentOnPreviousMonth()
            }
            // 2. Wait one frame for the new month's text layout pass to finish before starting the clock
            withFrameNanos { }
            // 3. Smoothly glide from 0f -> 1f on the GPU RenderThread
            transitionProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 230,
                    easing = SmoothOutEasing
                )
            )
        }
    }

    var accumulatedDragPx by remember { mutableFloatStateOf(0f) }
    var hasTriggeredInCurrentGesture by remember { mutableStateOf(false) }

    val selectedBrush = remember(colors.selectedFillTop, colors.selectedFillBottom) {
        Brush.linearGradient(colors = listOf(colors.selectedFillTop, colors.selectedFillBottom))
    }

    LiquidGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        accumulatedDragPx = 0f
                        hasTriggeredInCurrentGesture = false
                    },
                    onDragEnd = {
                        if (!hasTriggeredInCurrentGesture && fingerFollowOffsetPx.value != 0f) {
                            coroutineScope.launch {
                                fingerFollowOffsetPx.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioNoBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                )
                            }
                        }
                        accumulatedDragPx = 0f
                        hasTriggeredInCurrentGesture = false
                    },
                    onDragCancel = {
                        if (!hasTriggeredInCurrentGesture && fingerFollowOffsetPx.value != 0f) {
                            coroutineScope.launch {
                                fingerFollowOffsetPx.snapTo(0f)
                            }
                        }
                        accumulatedDragPx = 0f
                        hasTriggeredInCurrentGesture = false
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        if (!hasTriggeredInCurrentGesture) {
                            accumulatedDragPx += dragAmount
                            if (accumulatedDragPx <= -swipeTriggerDistancePx) {
                                hasTriggeredInCurrentGesture = true
                                animateCleanMonthTransition(direction = 1)
                            } else if (accumulatedDragPx >= swipeTriggerDistancePx) {
                                hasTriggeredInCurrentGesture = true
                                animateCleanMonthTransition(direction = -1)
                            } else if (!reducedMotion) {
                                val damped = (accumulatedDragPx * 0.42f)
                                    .coerceIn(-maxFingerFollowPx, maxFingerFollowPx)
                                coroutineScope.launch {
                                    fingerFollowOffsetPx.snapTo(damped)
                                }
                            }
                        }
                    }
                )
            }
            .testTag("monthly_calendar_card"),
        cornerRadius = 26.dp,
        isElevated = false,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Fixed-height (46.dp) Month & Year Header Row + Navigation Arrows
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LiquidGlassIconButton(
                    icon = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                    contentDescription = if (calendarMode == CalendarMode.BANGLA) "পূর্ববর্তী মাস / Previous Month" else "Previous Month",
                    onClick = { animateCleanMonthTransition(direction = -1) },
                    size = 38.dp,
                    modifier = Modifier.testTag("previous_month_button")
                )

                // Tappable Month/Year Heading (opens Quick Date Jump / Converter dialog)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .padding(horizontal = 8.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            role = Role.Button,
                            onClick = onOpenDateJump
                        )
                        .graphicsLayer {
                            val p = transitionProgress.value
                            val drag = fingerFollowOffsetPx.value * 0.45f
                            translationX = drag + ((1f - p) * activeDirection * (slideDistancePx * 0.55f))
                            alpha = 0.36f + (0.64f * p)
                        }
                        .testTag("month_header_title"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = primaryMonthHeading,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                lineHeight = 22.sp,
                                platformStyle = MetricSafeTextStyle
                            ),
                            color = colors.textPrimary,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Icon(
                            imageVector = Icons.Rounded.CalendarMonth,
                            contentDescription = null,
                            tint = colors.accentPrimary.copy(alpha = 0.85f),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = secondaryMonthSubtitle,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 11.sp,
                            lineHeight = 14.sp,
                            platformStyle = MetricSafeTextStyle
                        ),
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                LiquidGlassIconButton(
                    icon = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = if (calendarMode == CalendarMode.BANGLA) "পরবর্তী মাস / Next Month" else "Next Month",
                    onClick = { animateCleanMonthTransition(direction = 1) },
                    size = 38.dp,
                    modifier = Modifier.testTag("next_month_button")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Fixed-height (32.dp) 7-Column Weekday Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(colors.pillTrackBackground, RoundedCornerShape(11.dp))
                    .padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val useBengaliWeekdays = calendarMode == CalendarMode.BANGLA
                BanglaNumeralFormatter.WEEK_DAYS_SUNDAY_START.forEach { dayOfWeek ->
                    val isWeekend = BanglaNumeralFormatter.isBangladeshWeekend(dayOfWeek)
                    Text(
                        text = BanglaNumeralFormatter.getWeekdayShort(dayOfWeek, useBengaliWeekdays),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            lineHeight = 15.sp,
                            platformStyle = MetricSafeTextStyle
                        ),
                        color = if (isWeekend) colors.weekendText else colors.textSecondary,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Persistent 6x7 Monthly Date Grid animated strictly on the GPU RenderThread
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        val p = transitionProgress.value
                        val drag = fingerFollowOffsetPx.value
                        translationX = drag + ((1f - p) * activeDirection * slideDistancePx)
                        alpha = 0.34f + (0.66f * p)
                        val s = 0.985f + (0.015f * p)
                        scaleX = s
                        scaleY = s
                    },
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                for (rowIndex in 0 until 6) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        for (colIndex in 0 until 7) {
                            val cellIndex = rowIndex * 7 + colIndex
                            val cell = monthGridCells.getOrNull(cellIndex)
                            if (cell != null) {
                                CalendarGridDayCell(
                                    cell = cell,
                                    calendarMode = calendarMode,
                                    isSelected = cell.gregorianDate == selectedGregorianDate,
                                    selectedBrush = selectedBrush,
                                    onClick = { onSelectDate(cell.gregorianDate) },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f).aspectRatio(0.90f))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(9.dp))

            // Fixed-height (18.dp) Legend Row at bottom of calendar card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp)
                    .padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(
                    dotColor = colors.weekendText,
                    label = if (calendarMode == CalendarMode.BANGLA) "শুক্র–শনি সাপ্তাহিক ছুটি" else "Fri–Sat Weekend"
                )
                LegendItem(
                    dotColor = colors.holidayNationalDot,
                    label = if (calendarMode == CalendarMode.BANGLA) "জাতীয় ও বিশেষ দিবস" else "Holidays & Events"
                )
            }
        }
    }
}

@Composable
private fun LegendItem(
    dotColor: Color,
    label: String
) {
    val colors = GlassTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(dotColor, CircleShape)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                lineHeight = 12.sp,
                platformStyle = MetricSafeTextStyle
            ),
            color = colors.textSecondary,
            maxLines = 1
        )
    }
}

@Composable
private fun CalendarGridDayCell(
    cell: CalendarDayCell,
    calendarMode: CalendarMode,
    isSelected: Boolean,
    selectedBrush: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GlassTheme.colors
    val contentAlpha = if (cell.isCurrentMonth || isSelected) 1f else 0.34f

    val primaryTextColor = when {
        isSelected -> colors.onSelectedText
        cell.isToday -> colors.accentPrimary
        cell.hasPublicHoliday || cell.isWeekend -> colors.weekendText
        else -> colors.textPrimary
    }

    val secondaryTextColor = when {
        isSelected -> colors.onSelectedText.copy(alpha = 0.88f)
        cell.isToday -> colors.accentPrimary.copy(alpha = 0.85f)
        else -> colors.textSecondary
    }

    val dotColor = when {
        !cell.hasHoliday -> Color.Transparent
        isSelected -> colors.onSelectedText
        cell.hasPublicHoliday -> colors.holidayNationalDot
        else -> colors.holidayCulturalDot
    }

    Box(
        modifier = modifier
            .aspectRatio(0.90f)
            .graphicsLayer {
                alpha = contentAlpha
            }
            .drawBehind {
                val r = 14.dp.toPx()
                val cr = CornerRadius(r, r)

                // 1. Background fill
                when {
                    isSelected -> drawRoundRect(brush = selectedBrush, cornerRadius = cr)
                    cell.isToday -> drawRoundRect(color = colors.todayGlow, cornerRadius = cr)
                    cell.isWeekend && cell.isCurrentMonth -> drawRoundRect(
                        color = colors.weekendSurfaceTint,
                        cornerRadius = cr
                    )
                }

                // 2. Border stroke
                when {
                    isSelected -> drawRoundRect(
                        color = Color.White.copy(alpha = 0.65f),
                        cornerRadius = cr,
                        style = Stroke(width = 1.dp.toPx())
                    )
                    cell.isToday -> drawRoundRect(
                        color = colors.todayRing,
                        cornerRadius = cr,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                    cell.isCurrentMonth -> drawRoundRect(
                        color = colors.glassBorderSubtle,
                        cornerRadius = cr,
                        style = Stroke(width = 0.7.dp.toPx())
                    )
                }

                // 3. Holiday dot indicator in bottom slot
                if (cell.hasHoliday) {
                    drawCircle(
                        color = dotColor,
                        radius = 2.25.dp.toPx(),
                        center = Offset(x = size.width / 2f, y = size.height - 6.dp.toPx())
                    )
                }
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .testTag("date_cell_${cell.gregorianDate}")
            .semantics {
                selected = isSelected
                contentDescription = "${cell.banglaDate.formatBengali()}, ${cell.gregorianDate}"
            }
            .padding(top = 2.dp, bottom = 7.dp, start = 2.dp, end = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = cell.primaryDayText(calendarMode),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = if (isSelected || cell.isToday) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 15.sp,
                    lineHeight = 16.sp,
                    platformStyle = MetricSafeTextStyle
                ),
                color = primaryTextColor,
                maxLines = 1,
                softWrap = false
            )

            Spacer(modifier = Modifier.height(1.dp))

            Text(
                text = cell.secondaryDateText(calendarMode),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 8.5.sp,
                    lineHeight = 10.sp,
                    platformStyle = MetricSafeTextStyle
                ),
                color = secondaryTextColor,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Clip
            )
        }
    }
}
