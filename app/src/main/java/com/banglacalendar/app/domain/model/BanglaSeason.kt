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
package com.banglacalendar.app.domain.model

/**
 * The six traditional seasons (ষড়ঋতু) of Bangladesh, each spanning two consecutive Bangla months.
 */
enum class BanglaSeason(
    val bengaliName: String,
    val englishName: String,
    val englishSeasonOnly: String,
    val bengaliSubtitle: String,
    val englishSubtitle: String
) {
    GRISHMO(
        bengaliName = "গ্রীষ্ম",
        englishName = "Grishmo (Summer)",
        englishSeasonOnly = "Summer",
        bengaliSubtitle = "বৈশাখ – জ্যৈষ্ঠ",
        englishSubtitle = "Boishakh – Jyoishtho"
    ),
    BORSHA(
        bengaliName = "বর্ষা",
        englishName = "Borsha (Monsoon)",
        englishSeasonOnly = "Monsoon",
        bengaliSubtitle = "আষাঢ় – শ্রাবণ",
        englishSubtitle = "Asharh – Shrabon"
    ),
    SHOROT(
        bengaliName = "শরৎ",
        englishName = "Shorot (Autumn)",
        englishSeasonOnly = "Autumn",
        bengaliSubtitle = "ভাদ্র – আশ্বিন",
        englishSubtitle = "Bhadro – Ashwin"
    ),
    HEMONTO(
        bengaliName = "হেমন্ত",
        englishName = "Hemonto (Late Autumn)",
        englishSeasonOnly = "Late Autumn",
        bengaliSubtitle = "কার্তিক – অগ্রহায়ণ",
        englishSubtitle = "Kartik – Ogrohayon"
    ),
    SHEET(
        bengaliName = "শীত",
        englishName = "Sheet (Winter)",
        englishSeasonOnly = "Winter",
        bengaliSubtitle = "পৌষ – মাঘ",
        englishSubtitle = "Poush – Magh"
    ),
    BOSHONTO(
        bengaliName = "বসন্ত",
        englishName = "Boshonto (Spring)",
        englishSeasonOnly = "Spring",
        bengaliSubtitle = "ফাল্গুন – চৈত্র",
        englishSubtitle = "Falgun – Choitro"
    );

    fun displayName(useBengali: Boolean): String =
        if (useBengali) "$bengaliName ঋতু" else "$englishSeasonOnly Season"

    fun compactDisplayName(useBengali: Boolean): String =
        if (useBengali) "$bengaliName ঋতু" else englishSeasonOnly
}
