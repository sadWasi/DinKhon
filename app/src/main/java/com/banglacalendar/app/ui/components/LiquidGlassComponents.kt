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

import android.provider.Settings
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.banglacalendar.app.domain.model.CalendarMode
import com.banglacalendar.app.ui.theme.GlassTheme

/**
 * Checks whether the user has enabled reduced motion / disabled animations in Android accessibility settings.
 */
@Composable
fun rememberIsReducedMotionEnabled(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        val scale = runCatching {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1f
            )
        }.getOrDefault(1f)
        scale == 0f
    }
}

/**
 * Full-screen Apple-inspired ambient Liquid Glass canvas with soft atmospheric light orbs.
 * Uses hardware-accelerated cached radial shaders (`drawWithCache`) so all shaders are allocated
 * once per layout size—delivering the full Liquid Glass look at 60/120fps even on low-end phones.
 */
@Composable
fun LiquidGlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val colors = GlassTheme.colors

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawWithCache {
                val w = size.width
                val h = size.height

                val baseGradient = Brush.verticalGradient(
                    colors = listOf(colors.canvasTop, colors.canvasBottom)
                )

                val center1 = Offset(w * 0.18f, h * 0.14f)
                val radius1 = w * 0.78f
                val orb1 = Brush.radialGradient(
                    0.0f to colors.ambientOrbPrimary,
                    0.55f to colors.ambientOrbPrimary.copy(alpha = colors.ambientOrbPrimary.alpha * 0.42f),
                    1.0f to Color.Transparent,
                    center = center1,
                    radius = radius1
                )

                val center2 = Offset(w * 0.86f, h * 0.42f)
                val radius2 = w * 0.74f
                val orb2 = Brush.radialGradient(
                    0.0f to colors.ambientOrbSecondary,
                    0.55f to colors.ambientOrbSecondary.copy(alpha = colors.ambientOrbSecondary.alpha * 0.42f),
                    1.0f to Color.Transparent,
                    center = center2,
                    radius = radius2
                )

                val center3 = Offset(w * 0.25f, h * 0.84f)
                val radius3 = w * 0.72f
                val orb3 = Brush.radialGradient(
                    0.0f to colors.ambientOrbTertiary,
                    0.55f to colors.ambientOrbTertiary.copy(alpha = colors.ambientOrbTertiary.alpha * 0.42f),
                    1.0f to Color.Transparent,
                    center = center3,
                    radius = radius3
                )

                onDrawBehind {
                    drawRect(brush = baseGradient)
                    drawCircle(brush = orb1, radius = radius1, center = center1)
                    drawCircle(brush = orb2, radius = radius2, center = center2)
                    drawCircle(brush = orb3, radius = radius3, center = center3)
                }
            },
        content = content
    )
}

/**
 * Translucent frosted Liquid Glass container with anti-aliased rounded corners, specular rim border,
 * and inner top-edge refraction highlight—100% consistent in both Light (সাদা) and Dark (কালো) modes.
 */
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 26.dp,
    isElevated: Boolean = false,
    accentTint: Color = Color.Transparent,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val colors = GlassTheme.colors
    val shape: Shape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }
    val reducedMotion = rememberIsReducedMotionEnabled()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (onClick != null && isPressed && !reducedMotion) 0.985f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "glassCardScale"
    )

    val topSurface = if (isElevated) colors.glassElevatedTop else colors.glassSurfaceTop
    val bottomSurface = if (isElevated) colors.glassElevatedBottom else colors.glassSurfaceBottom

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .drawWithCache {
                val cornerPx = cornerRadius.toPx()
                val cr = CornerRadius(cornerPx, cornerPx)
                val strokeWidthPx = 1.dp.toPx()
                val halfStroke = strokeWidthPx / 2f

                val surfaceBrush = Brush.linearGradient(
                    colors = listOf(
                        topSurface,
                        if (accentTint != Color.Transparent) accentTint else bottomSurface,
                        bottomSurface
                    ),
                    start = Offset.Zero,
                    end = Offset(size.width, size.height)
                )
                val sheenHeight = size.height * 0.26f
                val sheenBrush = Brush.verticalGradient(
                    colors = listOf(colors.glassInnerGlow, Color.Transparent),
                    startY = 0f,
                    endY = sheenHeight
                )
                val borderBrush = Brush.linearGradient(
                    colors = listOf(
                        colors.glassBorderHighlight,
                        colors.glassBorderSubtle,
                        colors.glassBorderHighlight.copy(alpha = colors.glassBorderHighlight.alpha * 0.65f)
                    ),
                    start = Offset.Zero,
                    end = Offset(size.width, size.height)
                )

                onDrawWithContent {
                    // 1. Anti-aliased rounded glass surface
                    drawRoundRect(
                        brush = surfaceBrush,
                        cornerRadius = cr
                    )
                    // 2. Top-edge optical specular sheen
                    drawRoundRect(
                        brush = sheenBrush,
                        cornerRadius = cr
                    )
                    // 3. Card inner content
                    drawContent()
                    // 4. Crisp 360-degree rounded glass border
                    drawRoundRect(
                        brush = borderBrush,
                        topLeft = Offset(halfStroke, halfStroke),
                        size = Size(
                            width = (size.width - strokeWidthPx).coerceAtLeast(0f),
                            height = (size.height - strokeWidthPx).coerceAtLeast(0f)
                        ),
                        cornerRadius = CornerRadius(
                            (cornerPx - halfStroke).coerceAtLeast(0f),
                            (cornerPx - halfStroke).coerceAtLeast(0f)
                        ),
                        style = Stroke(width = strokeWidthPx)
                    )
                }
            }
            .then(clickableModifier)
            .padding(contentPadding),
        content = content
    )
}

/**
 * Prominent Apple-inspired Liquid Glass segmented switch:
 * **বাংলা | English**
 */
@Composable
fun SegmentedCalendarModeSwitch(
    selectedMode: CalendarMode,
    onModeChange: (CalendarMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GlassTheme.colors
    val reducedMotion = rememberIsReducedMotionEnabled()
    val pillShape = remember { RoundedCornerShape(50) }

    BoxWithConstraints(
        modifier = modifier
            .height(44.dp)
            .clip(pillShape)
            .background(colors.pillTrackBackground, pillShape)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(colors.glassBorderHighlight, colors.glassBorderSubtle)
                ),
                shape = pillShape
            )
            .padding(4.dp)
            .testTag("calendar_mode_toggle")
    ) {
        val segmentWidth = maxWidth / 2
        val thumbProgress by animateFloatAsState(
            targetValue = if (selectedMode == CalendarMode.BANGLA) 0f else 1f,
            animationSpec = if (reducedMotion) {
                tween(0)
            } else {
                tween(
                    durationMillis = 220,
                    easing = CubicBezierEasing(0.22f, 1.0f, 0.36f, 1.0f)
                )
            },
            label = "segmentedThumbProgress"
        )

        // Sliding Liquid Glass Thumb (animated 100% on RenderThread via graphicsLayer)
        Box(
            modifier = Modifier
                .width(segmentWidth)
                .fillMaxHeight()
                .graphicsLayer {
                    translationX = thumbProgress * segmentWidth.toPx()
                }
                .clip(pillShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colors.pillThumbSurface,
                            colors.pillThumbSurface.copy(alpha = 0.84f)
                        )
                    ),
                    shape = pillShape
                )
                .border(
                    width = 1.dp,
                    color = colors.glassBorderHighlight,
                    shape = pillShape
                )
        )

        Row(modifier = Modifier.fillMaxSize()) {
            val isBangla = selectedMode == CalendarMode.BANGLA
            val banglaTextColor by animateColorAsState(
                targetValue = if (isBangla) colors.accentPrimary else colors.textSecondary,
                animationSpec = tween(if (reducedMotion) 0 else 160),
                label = "banglaSegmentColor"
            )
            val englishTextColor by animateColorAsState(
                targetValue = if (!isBangla) colors.accentPrimary else colors.textSecondary,
                animationSpec = tween(if (reducedMotion) 0 else 160),
                label = "englishSegmentColor"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(pillShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        role = Role.Tab,
                        onClick = { onModeChange(CalendarMode.BANGLA) }
                    )
                    .testTag("toggle_bangla_button")
                    .semantics { contentDescription = "বাংলা ক্যালেন্ডার মোড / Bangla Calendar Mode" },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "বাংলা",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = if (isBangla) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = banglaTextColor,
                    maxLines = 1
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(pillShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        role = Role.Tab,
                        onClick = { onModeChange(CalendarMode.ENGLISH) }
                    )
                    .testTag("toggle_english_button")
                    .semantics { contentDescription = "English Calendar Mode" },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "English",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = if (!isBangla) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = englishTextColor,
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Circular translucent glass icon button with subtle spring press feedback on the RenderThread.
 */
@Composable
fun LiquidGlassIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    tint: Color = GlassTheme.colors.textPrimary
) {
    val colors = GlassTheme.colors
    val reducedMotion = rememberIsReducedMotionEnabled()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && !reducedMotion) 0.90f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "iconButtonScale"
    )

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(colors.glassElevatedTop, colors.glassSurfaceBottom)
                ),
                shape = CircleShape
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(colors.glassBorderHighlight, colors.glassBorderSubtle)
                ),
                shape = CircleShape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(size * 0.48f)
        )
    }
}

/**
 * Translucent glass pill button for actions like "আজ / Today" or "কপি / Copy".
 */
@Composable
fun LiquidGlassPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isHighlighted: Boolean = false
) {
    val colors = GlassTheme.colors
    val reducedMotion = rememberIsReducedMotionEnabled()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && !reducedMotion) 0.93f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "pillButtonScale"
    )

    val shape = remember { RoundedCornerShape(50) }
    val backgroundBrush = if (isHighlighted) {
        Brush.horizontalGradient(
            colors = listOf(colors.selectedFillTop, colors.selectedFillBottom)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(colors.glassElevatedTop, colors.glassSurfaceBottom)
        )
    }
    val contentColor = if (isHighlighted) colors.onSelectedText else colors.accentPrimary

    Row(
        modifier = modifier
            .height(30.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .background(brush = backgroundBrush, shape = shape)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(colors.glassBorderHighlight, colors.glassBorderSubtle)
                ),
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(14.dp)
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = contentColor,
            maxLines = 1,
            softWrap = false
        )
    }
}
