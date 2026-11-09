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

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.OfflineBolt
import androidx.compose.material.icons.rounded.SettingsBrightness
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banglacalendar.app.data.UserPreferences
import com.banglacalendar.app.domain.engine.BanglaCalendarEngine
import com.banglacalendar.app.domain.engine.BanglaNumeralFormatter
import com.banglacalendar.app.domain.model.AppThemeMode
import com.banglacalendar.app.domain.model.BanglaDate
import com.banglacalendar.app.domain.model.BanglaMonth
import com.banglacalendar.app.domain.model.CalendarMode
import com.banglacalendar.app.domain.model.InterfaceLanguage
import com.banglacalendar.app.ui.theme.GlassTheme
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

/**
 * Full-screen edge-to-edge Liquid Glass Settings overlay.
 * Eliminates the ModalBottomSheet top rounded cutout in White (সাদা) mode so
 * both White (সাদা) and Dark (কালো) modes share 100% identical geometry.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsBottomSheet(
    preferences: UserPreferences,
    useBengaliChrome: Boolean,
    onThemeModeChange: (AppThemeMode) -> Unit,
    onDefaultCalendarModeChange: (CalendarMode) -> Unit,
    onInterfaceLanguageChange: (InterfaceLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = GlassTheme.colors
    val uriHandler = LocalUriHandler.current
    val innerBoxShape = remember { RoundedCornerShape(14.dp) }
    val badgeShape = remember { RoundedCornerShape(10.dp) }

    BackHandler(onBack = onDismiss)

    LiquidGlassBackground(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {} // Consume background taps
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .displayCutoutPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .testTag("settings_sheet_content"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 720.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (useBengaliChrome) "সেটিংস ও তথ্য" else "Settings & Information",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = colors.textPrimary
                        )
                        Text(
                            text = "DinKhon • দিনক্ষণ",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.textSecondary
                        )
                    }

                    LiquidGlassIconButton(
                        icon = Icons.Rounded.Close,
                        contentDescription = "Close Settings",
                        onClick = onDismiss,
                        size = 38.dp
                    )
                }

                // 1. Theme Selection Section (সিস্টেম | সাদা | কালো)
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 22.dp,
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = if (useBengaliChrome) "থিম (Theme)" else "Appearance & Theme",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SettingsOptionChip(
                                title = if (useBengaliChrome) "সিস্টেম" else "System",
                                icon = Icons.Rounded.SettingsBrightness,
                                selected = preferences.themeMode == AppThemeMode.SYSTEM,
                                onClick = { onThemeModeChange(AppThemeMode.SYSTEM) },
                                modifier = Modifier.weight(1f).testTag("theme_system_option")
                            )
                            SettingsOptionChip(
                                title = if (useBengaliChrome) "সাদা" else "Light",
                                icon = Icons.Rounded.LightMode,
                                selected = preferences.themeMode == AppThemeMode.LIGHT,
                                onClick = { onThemeModeChange(AppThemeMode.LIGHT) },
                                modifier = Modifier.weight(1f).testTag("theme_light_option")
                            )
                            SettingsOptionChip(
                                title = if (useBengaliChrome) "কালো" else "Dark",
                                icon = Icons.Rounded.DarkMode,
                                selected = preferences.themeMode == AppThemeMode.DARK,
                                onClick = { onThemeModeChange(AppThemeMode.DARK) },
                                modifier = Modifier.weight(1f).testTag("theme_dark_option")
                            )
                        }
                    }
                }

                // 2. Default Calendar Mode & Interface Language
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 22.dp,
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = if (useBengaliChrome) "ডিফল্ট ক্যালেন্ডার মোড" else "Default Calendar Mode",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SettingsOptionChip(
                                title = "বাংলা (Bangla)",
                                selected = preferences.defaultCalendarMode == CalendarMode.BANGLA,
                                onClick = { onDefaultCalendarModeChange(CalendarMode.BANGLA) },
                                modifier = Modifier.weight(1f)
                            )
                            SettingsOptionChip(
                                title = "English (Gregorian)",
                                selected = preferences.defaultCalendarMode == CalendarMode.ENGLISH,
                                onClick = { onDefaultCalendarModeChange(CalendarMode.ENGLISH) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = if (useBengaliChrome) "ইন্টারফেসের ভাষা" else "Interface Language",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SettingsOptionChip(
                                title = if (useBengaliChrome) "স্বয়ংক্রিয়" else "Auto",
                                icon = Icons.Rounded.Language,
                                selected = preferences.interfaceLanguage == InterfaceLanguage.FOLLOW_CALENDAR,
                                onClick = { onInterfaceLanguageChange(InterfaceLanguage.FOLLOW_CALENDAR) },
                                modifier = Modifier.weight(1f)
                            )
                            SettingsOptionChip(
                                title = "বাংলা",
                                selected = preferences.interfaceLanguage == InterfaceLanguage.BANGLA,
                                onClick = { onInterfaceLanguageChange(InterfaceLanguage.BANGLA) },
                                modifier = Modifier.weight(1f)
                            )
                            SettingsOptionChip(
                                title = "English",
                                selected = preferences.interfaceLanguage == InterfaceLanguage.ENGLISH,
                                onClick = { onInterfaceLanguageChange(InterfaceLanguage.ENGLISH) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // 3. Calendar System Information (2019 Revised Bangladesh Bangla Academy System)
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 22.dp,
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = null,
                                tint = colors.accentPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (useBengaliChrome) {
                                    "বাংলা একাডেমি সংশোধিত বর্ষপঞ্জি (২০১৯)"
                                } else {
                                    "Bangladesh Bangla Academy System (2019)"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = colors.textPrimary
                            )
                        }

                        Text(
                            text = if (useBengaliChrome) {
                                "এই অ্যাপে গণপ্রজাতন্ত্রী বাংলাদেশ সরকার ও বাংলা একাডেমি কর্তৃক ২০১৯ সালে সংশোধিত সরকারি বাংলা বর্ষপঞ্জি অনুসরণ করা হয়েছে:"
                            } else {
                                "This application implements the official 2019 revised Bangladesh Bangla Academy calendar system:"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(innerBoxShape)
                                .background(colors.pillTrackBackground, innerBoxShape)
                                .border(
                                    width = 1.dp,
                                    color = colors.glassBorderHighlight,
                                    shape = innerBoxShape
                                )
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            CalendarRuleBullet("• বৈশাখ থেকে আশ্বিন (প্রথম ৬ মাস): ৩১ দিন / 31 days")
                            CalendarRuleBullet("• কার্তিক থেকে মাঘ ও চৈত্র (৫ মাস): ৩০ দিন / 30 days")
                            CalendarRuleBullet("• ফাল্গুন মাস: ২৯ দিন (অধিবর্ষে / Leap year-এ ৩০ দিন)")
                            CalendarRuleBullet("• পহেলা বৈশাখ: সর্বদা ১৪ এপ্রিল / Always April 14")
                            CalendarRuleBullet("• সময় অঞ্চল (Timezone): Asia/Dhaka (BST, UTC+6)")
                        }
                    }
                }

                // 4. About Application & Creator Card
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 22.dp,
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "DinKhon",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = "দিনক্ষণ • বাংলা ও ইংরেজি ক্যালেন্ডার",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = colors.textSecondary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(badgeShape)
                                    .background(colors.accentContainer, badgeShape)
                                    .border(
                                        width = 1.dp,
                                        color = colors.accentPrimary.copy(alpha = 0.42f),
                                        shape = badgeShape
                                    )
                                    .padding(horizontal = 11.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "1.0.0",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = colors.accentPrimary
                                )
                            }
                        }

                        Text(
                            text = if (useBengaliChrome) {
                                "শতভাগ অফলাইন, বিজ্ঞাপনমুক্ত এবং দ্রুতগতির বাংলা ও ইংরেজি ক্যালেন্ডার এবং তারিখ রূপান্তরকারী।"
                            } else {
                                "100% offline, ad-free, privacy-first Bangladeshi Bangla and Gregorian calendar with instant two-way date conversion."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary
                        )

                        // Creator & Social Links Row (Name only on left + SupportKori Logo & Facebook Logo on right)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(innerBoxShape)
                                .background(colors.pillTrackBackground, innerBoxShape)
                                .border(
                                    width = 1.dp,
                                    color = colors.glassBorderHighlight,
                                    shape = innerBoxShape
                                )
                                .padding(horizontal = 14.dp, vertical = 11.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (useBengaliChrome) "নির্মাতা / Creator" else "Created by",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.textSecondary
                                )
                                Text(
                                    text = "Sadman Wasi",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    ),
                                    color = colors.textPrimary,
                                    maxLines = 1
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // SupportKori Logo Button (https://www.supportkori.com/sadmanwasi)
                                CreatorSocialLogoButton(
                                    text = "SupportKori",
                                    contentDescription = "Support Sadman Wasi on SupportKori",
                                    backgroundBrush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFFFFDD00).copy(alpha = if (colors.isDark) 0.20f else 0.28f),
                                            Color(0xFFF59E0B).copy(alpha = if (colors.isDark) 0.16f else 0.22f)
                                        )
                                    ),
                                    borderColor = Color(0xFFF59E0B).copy(alpha = 0.55f),
                                    textColor = if (colors.isDark) Color(0xFFFFEA79) else Color(0xFF78350F),
                                    onClick = {
                                        runCatching {
                                            uriHandler.openUri("https://www.supportkori.com/sadmanwasi")
                                        }
                                    },
                                    modifier = Modifier.testTag("creator_supportkori_button")
                                ) {
                                    SupportKoriLogoMark(size = 19.dp)
                                }

                                // Facebook Profile Logo Button (https://www.facebook.com/s4dmanwasi)
                                CreatorSocialLogoButton(
                                    text = "Facebook",
                                    contentDescription = "Sadman Wasi on Facebook",
                                    backgroundBrush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF1877F2).copy(alpha = if (colors.isDark) 0.26f else 0.18f),
                                            Color(0xFF0C5DC7).copy(alpha = if (colors.isDark) 0.22f else 0.15f)
                                        )
                                    ),
                                    borderColor = Color(0xFF1877F2).copy(alpha = 0.55f),
                                    textColor = if (colors.isDark) Color(0xFF93C5FD) else Color(0xFF1D4ED8),
                                    onClick = {
                                        runCatching {
                                            uriHandler.openUri("https://www.facebook.com/s4dmanwasi")
                                        }
                                    },
                                    modifier = Modifier.testTag("creator_facebook_button")
                                ) {
                                    FacebookLogoMark(size = 19.dp)
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.OfflineBolt,
                                contentDescription = null,
                                tint = colors.holidayCulturalDot,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (useBengaliChrome) {
                                    "কোনো ইন্টারনেট বা অ্যাকাউন্টের প্রয়োজন নেই"
                                } else {
                                    "Works 100% offline • Optimized for all Android devices"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.holidayCulturalDot
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CreatorSocialLogoButton(
    text: String,
    contentDescription: String,
    backgroundBrush: Brush,
    borderColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    logoContent: @Composable () -> Unit
) {
    val reducedMotion = rememberIsReducedMotionEnabled()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pillShape = remember { RoundedCornerShape(50) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed && !reducedMotion) 0.93f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "socialButtonScale"
    )

    Row(
        modifier = modifier
            .height(30.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(pillShape)
            .background(brush = backgroundBrush, shape = pillShape)
            .border(width = 1.dp, color = borderColor, shape = pillShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .semantics { this.contentDescription = contentDescription }
            .padding(start = 7.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        logoContent()
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp
            ),
            color = textColor,
            maxLines = 1,
            softWrap = false
        )
    }
}

/**
 * Vector Canvas rendering of the official SupportKori logo mark:
 * signature yellow (#FFDD00) rounded badge with dark navy (#0D1321) smiling eyes
 * and U-shaped hug-smile arc.
 */
@Composable
private fun SupportKoriLogoMark(
    size: Dp = 18.dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val corner = w * 0.26f

        // 1. Official SupportKori Yellow (#FFDD00) rounded square badge
        drawRoundRect(
            color = Color(0xFFFFDD00),
            cornerRadius = CornerRadius(corner, corner)
        )

        val navy = Color(0xFF0D1321)
        val eyeRadius = w * 0.105f

        // 2. Left & Right smiling eyes
        drawCircle(
            color = navy,
            radius = eyeRadius,
            center = Offset(w * 0.37f, h * 0.35f)
        )
        drawCircle(
            color = navy,
            radius = eyeRadius,
            center = Offset(w * 0.63f, h * 0.35f)
        )

        // 3. Signature thick U-shaped SupportKori hug-smile arc
        drawArc(
            color = navy,
            startAngle = 12f,
            sweepAngle = 156f,
            useCenter = false,
            topLeft = Offset(w * 0.19f, h * 0.18f),
            size = Size(w * 0.62f, h * 0.62f),
            style = Stroke(width = w * 0.145f, cap = StrokeCap.Round)
        )

        // 4. Subtle inner golden hug-clasp line across the bottom of the smile
        drawArc(
            color = Color(0xFFFFDD00),
            startAngle = 45f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(w * 0.25f, h * 0.24f),
            size = Size(w * 0.50f, h * 0.50f),
            style = Stroke(width = w * 0.035f, cap = StrokeCap.Round)
        )
    }
}

/**
 * Crisp vector Canvas rendering of the official Facebook (#1877F2) circular 'f' logo mark.
 */
@Composable
private fun FacebookLogoMark(
    size: Dp = 19.dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        drawCircle(
            color = Color(0xFF1877F2),
            radius = w * 0.5f,
            center = Offset(w * 0.5f, h * 0.5f)
        )

        val fPath = Path().apply {
            // Vertical stem of 'f' + top hook + crossbar
            moveTo(w * 0.58f, h * 0.86f)
            lineTo(w * 0.58f, h * 0.54f)
            lineTo(w * 0.70f, h * 0.54f)
            lineTo(w * 0.72f, h * 0.41f)
            lineTo(w * 0.58f, h * 0.41f)
            lineTo(w * 0.58f, h * 0.33f)
            cubicTo(
                w * 0.58f, h * 0.28f,
                w * 0.60f, h * 0.26f,
                w * 0.66f, h * 0.26f
            )
            lineTo(w * 0.72f, h * 0.26f)
            lineTo(w * 0.72f, h * 0.15f)
            lineTo(w * 0.62f, h * 0.15f)
            cubicTo(
                w * 0.49f, h * 0.15f,
                w * 0.44f, h * 0.22f,
                w * 0.44f, h * 0.33f
            )
            lineTo(w * 0.44f, h * 0.41f)
            lineTo(w * 0.34f, h * 0.41f)
            lineTo(w * 0.34f, h * 0.54f)
            lineTo(w * 0.44f, h * 0.54f)
            lineTo(w * 0.44f, h * 0.86f)
            close()
        }
        drawPath(path = fPath, color = Color.White)
    }
}

@Composable
private fun CalendarRuleBullet(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = GlassTheme.colors.textPrimary
    )
}

@Composable
private fun SettingsOptionChip(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    val colors = GlassTheme.colors
    val reducedMotion = rememberIsReducedMotionEnabled()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val shape = remember { RoundedCornerShape(12.dp) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed && !reducedMotion) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "chipPressScale"
    )

    val textColor = if (selected) colors.onSelectedText else colors.textPrimary
    val borderColor = if (selected) colors.accentPrimary else colors.glassBorderHighlight

    val bgBrush = if (selected) {
        Brush.horizontalGradient(listOf(colors.selectedFillTop, colors.selectedFillBottom))
    } else {
        Brush.linearGradient(listOf(colors.pillTrackBackground, colors.pillTrackBackground))
    }

    Row(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .background(brush = bgBrush, shape = shape)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 10.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            ),
            color = textColor,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

/**
 * Full-screen edge-to-edge Two-Way Date Converter & Quick Month/Date Jump overlay.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DateJumpConverterSheet(
    initialGregorianDate: LocalDate,
    initialBanglaDate: BanglaDate,
    initialMode: CalendarMode,
    useBengaliChrome: Boolean,
    onJumpToBanglaDate: (year: Int, month: BanglaMonth, day: Int) -> Unit,
    onJumpToGregorianDate: (year: Int, month: Int, day: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = GlassTheme.colors

    var converterTabMode by remember { mutableStateOf(initialMode) }

    // Bangla date picker state
    var banglaYear by remember { mutableIntStateOf(initialBanglaDate.year) }
    var banglaMonth by remember { mutableStateOf(initialBanglaDate.month) }
    var banglaDay by remember { mutableIntStateOf(initialBanglaDate.dayOfMonth) }

    // Gregorian date picker state
    var gregYear by remember { mutableIntStateOf(initialGregorianDate.year) }
    var gregMonth by remember { mutableIntStateOf(initialGregorianDate.monthValue) }
    var gregDay by remember { mutableIntStateOf(initialGregorianDate.dayOfMonth) }

    val maxBanglaDay = BanglaCalendarEngine.getDaysInBanglaMonth(banglaYear, banglaMonth)
    val safeBanglaDay = banglaDay.coerceIn(1, maxBanglaDay)
    val previewFromBangla = remember(banglaYear, banglaMonth, safeBanglaDay) {
        BanglaCalendarEngine.banglaToGregorian(banglaYear, banglaMonth, safeBanglaDay)
    }

    val maxGregDay = remember(gregYear, gregMonth) {
        YearMonth.of(gregYear, gregMonth).lengthOfMonth()
    }
    val safeGregDay = gregDay.coerceIn(1, maxGregDay)
    val previewFromGregorian = remember(gregYear, gregMonth, safeGregDay) {
        BanglaCalendarEngine.gregorianToBangla(LocalDate.of(gregYear, gregMonth, safeGregDay))
    }

    BackHandler(onBack = onDismiss)

    LiquidGlassBackground(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .displayCutoutPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .testTag("date_jump_sheet"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 720.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SwapHoriz,
                            contentDescription = null,
                            tint = colors.accentPrimary
                        )
                        Text(
                            text = if (useBengaliChrome) "তারিখ রূপান্তর ও জাম্প" else "Date Converter & Jump",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary
                        )
                    }

                    LiquidGlassIconButton(
                        icon = Icons.Rounded.Close,
                        contentDescription = "Close",
                        onClick = onDismiss,
                        size = 38.dp
                    )
                }

                // Mode selector inside converter
                SegmentedCalendarModeSwitch(
                    selectedMode = converterTabMode,
                    onModeChange = { converterTabMode = it },
                    modifier = Modifier.fillMaxWidth()
                )

                if (converterTabMode == CalendarMode.BANGLA) {
                    // Bangla Year Stepper
                    LiquidGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 22.dp,
                        contentPadding = PaddingValues(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "বঙ্গাব্দ (Bangla Year)",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = colors.textSecondary
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    LiquidGlassPillButton(
                                        text = "−",
                                        onClick = { banglaYear = (banglaYear - 1).coerceAtLeast(1300) }
                                    )
                                    Text(
                                        text = "${BanglaNumeralFormatter.toBengaliDigits(banglaYear)} ($banglaYear)",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = colors.accentPrimary
                                    )
                                    LiquidGlassPillButton(
                                        text = "+",
                                        onClick = { banglaYear = (banglaYear + 1).coerceAtMost(1600) }
                                    )
                                }
                            }

                            // 12 Bangla Months Grid
                            Text(
                                text = "বাংলা মাস (Bangla Month)",
                                style = MaterialTheme.typography.labelLarge,
                                color = colors.textSecondary
                            )
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                maxItemsInEachRow = 4
                            ) {
                                BanglaMonth.entries.forEach { m ->
                                    SettingsOptionChip(
                                        title = m.bengaliName,
                                        selected = banglaMonth == m,
                                        onClick = { banglaMonth = m },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            // Day horizontal selector (1..maxBanglaDay)
                            Text(
                                text = "তারিখ (Day: ১ – ${BanglaNumeralFormatter.toBengaliDigits(maxBanglaDay)})",
                                style = MaterialTheme.typography.labelLarge,
                                color = colors.textSecondary
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                for (d in 1..maxBanglaDay) {
                                    SettingsOptionChip(
                                        title = BanglaNumeralFormatter.toBengaliDigits(d),
                                        selected = safeBanglaDay == d,
                                        onClick = { banglaDay = d },
                                        modifier = Modifier.width(46.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Live Conversion Result Preview Card
                    LiquidGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 22.dp,
                        isElevated = true,
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${BanglaNumeralFormatter.toBengaliDigits(safeBanglaDay)} ${banglaMonth.bengaliName} ${BanglaNumeralFormatter.toBengaliDigits(banglaYear)} বঙ্গাব্দ",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = "≈ ${BanglaNumeralFormatter.formatGregorianDate(previewFromBangla, useBengali = false)} (${BanglaNumeralFormatter.getBengaliWeekdayFull(previewFromBangla.dayOfWeek)})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.accentPrimary
                                )
                            }

                            LiquidGlassPillButton(
                                text = if (useBengaliChrome) "দেখুন" else "Go to Date",
                                isHighlighted = true,
                                onClick = { onJumpToBanglaDate(banglaYear, banglaMonth, safeBanglaDay) }
                            )
                        }
                    }
                } else {
                    // Gregorian Date Converter
                    LiquidGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 22.dp,
                        contentPadding = PaddingValues(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Gregorian Year",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = colors.textSecondary
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    LiquidGlassPillButton(
                                        text = "−",
                                        onClick = { gregYear = (gregYear - 1).coerceAtLeast(1900) }
                                    )
                                    Text(
                                        text = gregYear.toString(),
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = colors.accentPrimary
                                    )
                                    LiquidGlassPillButton(
                                        text = "+",
                                        onClick = { gregYear = (gregYear + 1).coerceAtMost(2150) }
                                    )
                                }
                            }

                            Text(
                                text = "Month",
                                style = MaterialTheme.typography.labelLarge,
                                color = colors.textSecondary
                            )
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                maxItemsInEachRow = 4
                            ) {
                                Month.entries.forEach { m ->
                                    SettingsOptionChip(
                                        title = BanglaNumeralFormatter.getGregorianMonthShortEnglish(m),
                                        selected = gregMonth == m.value,
                                        onClick = { gregMonth = m.value },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            Text(
                                text = "Day (1 – $maxGregDay)",
                                style = MaterialTheme.typography.labelLarge,
                                color = colors.textSecondary
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                for (d in 1..maxGregDay) {
                                    SettingsOptionChip(
                                        title = d.toString(),
                                        selected = safeGregDay == d,
                                        onClick = { gregDay = d },
                                        modifier = Modifier.width(46.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Live Conversion Result Preview Card
                    LiquidGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 22.dp,
                        isElevated = true,
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "$safeGregDay ${BanglaNumeralFormatter.getGregorianMonthEnglish(Month.of(gregMonth))} $gregYear",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = "≈ ${previewFromGregorian.formatBengali(includeBongabdoSuffix = true)} (${previewFromGregorian.weekdayBengali})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.accentPrimary
                                )
                            }

                            LiquidGlassPillButton(
                                text = if (useBengaliChrome) "দেখুন" else "Go to Date",
                                isHighlighted = true,
                                onClick = { onJumpToGregorianDate(gregYear, gregMonth, safeGregDay) }
                            )
                        }
                    }
                }
            }
        }
    }
}
