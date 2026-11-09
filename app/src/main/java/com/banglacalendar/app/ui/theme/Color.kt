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
package com.banglacalendar.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Semantic color tokens for the Apple-inspired Liquid Glass aesthetic in Light (সাদা) and Dark (কালো) themes.
 */
@Immutable
data class LiquidGlassColors(
    val isDark: Boolean,
    val canvasTop: Color,
    val canvasBottom: Color,
    val ambientOrbPrimary: Color,
    val ambientOrbSecondary: Color,
    val ambientOrbTertiary: Color,
    val glassSurfaceTop: Color,
    val glassSurfaceBottom: Color,
    val glassElevatedTop: Color,
    val glassElevatedBottom: Color,
    val glassBorderHighlight: Color,
    val glassBorderSubtle: Color,
    val glassInnerGlow: Color,
    val glassShadowColor: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val accentPrimary: Color,
    val accentSecondary: Color,
    val accentContainer: Color,
    val onAccentPrimary: Color,
    val todayRing: Color,
    val todayGlow: Color,
    val selectedFillTop: Color,
    val selectedFillBottom: Color,
    val onSelectedText: Color,
    val weekendText: Color,
    val weekendSurfaceTint: Color,
    val holidayNationalDot: Color,
    val holidayCulturalDot: Color,
    val pillTrackBackground: Color,
    val pillThumbSurface: Color
)

val LightLiquidGlassColors = LiquidGlassColors(
    isDark = false,
    canvasTop = Color(0xFFF1F5FB),
    canvasBottom = Color(0xFFE4ECF7),
    ambientOrbPrimary = Color(0xFFBAE6FD).copy(alpha = 0.50f),
    ambientOrbSecondary = Color(0xFFC7D2FE).copy(alpha = 0.45f),
    ambientOrbTertiary = Color(0xFFFECDD3).copy(alpha = 0.38f),
    glassSurfaceTop = Color.White.copy(alpha = 0.82f),
    glassSurfaceBottom = Color.White.copy(alpha = 0.68f),
    glassElevatedTop = Color.White.copy(alpha = 0.90f),
    glassElevatedBottom = Color(0xFFF8FAFC).copy(alpha = 0.78f),
    glassBorderHighlight = Color(0xFF94A3B8).copy(alpha = 0.40f),
    glassBorderSubtle = Color(0xFF94A3B8).copy(alpha = 0.26f),
    glassInnerGlow = Color.White.copy(alpha = 0.45f),
    glassShadowColor = Color(0xFF0F172A).copy(alpha = 0.08f),
    textPrimary = Color(0xFF0F172A),
    textSecondary = Color(0xFF475569),
    textMuted = Color(0xFF94A3B8),
    accentPrimary = Color(0xFF0284C7),
    accentSecondary = Color(0xFF4F46E5),
    accentContainer = Color(0xFFE0F2FE).copy(alpha = 0.85f),
    onAccentPrimary = Color.White,
    todayRing = Color(0xFF0284C7),
    todayGlow = Color(0xFF0EA5E9).copy(alpha = 0.16f),
    selectedFillTop = Color(0xFF0284C7),
    selectedFillBottom = Color(0xFF2563EB),
    onSelectedText = Color.White,
    weekendText = Color(0xFFE11D48),
    weekendSurfaceTint = Color(0xFFFFF1F2).copy(alpha = 0.65f),
    holidayNationalDot = Color(0xFFE11D48),
    holidayCulturalDot = Color(0xFF0D9488),
    pillTrackBackground = Color(0xFF0F172A).copy(alpha = 0.06f),
    pillThumbSurface = Color.White.copy(alpha = 0.95f)
)

val DarkLiquidGlassColors = LiquidGlassColors(
    isDark = true,
    canvasTop = Color(0xFF070B14),
    canvasBottom = Color(0xFF0D1322),
    ambientOrbPrimary = Color(0xFF0EA5E9).copy(alpha = 0.18f),
    ambientOrbSecondary = Color(0xFF6366F1).copy(alpha = 0.16f),
    ambientOrbTertiary = Color(0xFFF43F5E).copy(alpha = 0.12f),
    glassSurfaceTop = Color(0xFF1E293B).copy(alpha = 0.72f),
    glassSurfaceBottom = Color(0xFF0F172A).copy(alpha = 0.58f),
    glassElevatedTop = Color(0xFF334155).copy(alpha = 0.76f),
    glassElevatedBottom = Color(0xFF1E293B).copy(alpha = 0.62f),
    glassBorderHighlight = Color.White.copy(alpha = 0.28f),
    glassBorderSubtle = Color.White.copy(alpha = 0.18f),
    glassInnerGlow = Color.White.copy(alpha = 0.10f),
    glassShadowColor = Color.Black.copy(alpha = 0.45f),
    textPrimary = Color(0xFFF8FAFC),
    textSecondary = Color(0xFFCBD5E1),
    textMuted = Color(0xFF64748B),
    accentPrimary = Color(0xFF38BDF8),
    accentSecondary = Color(0xFF818CF8),
    accentContainer = Color(0xFF0284C7).copy(alpha = 0.28f),
    onAccentPrimary = Color(0xFF082F49),
    todayRing = Color(0xFF38BDF8),
    todayGlow = Color(0xFF38BDF8).copy(alpha = 0.20f),
    selectedFillTop = Color(0xFF0EA5E9),
    selectedFillBottom = Color(0xFF3B82F6),
    onSelectedText = Color.White,
    weekendText = Color(0xFFFB7185),
    weekendSurfaceTint = Color(0xFFF43F5E).copy(alpha = 0.12f),
    holidayNationalDot = Color(0xFFFB7185),
    holidayCulturalDot = Color(0xFF2DD4BF),
    pillTrackBackground = Color.White.copy(alpha = 0.10f),
    pillThumbSurface = Color(0xFF334155).copy(alpha = 0.92f)
)

val LocalLiquidGlassColors = staticCompositionLocalOf { LightLiquidGlassColors }
