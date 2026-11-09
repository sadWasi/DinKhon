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
 * The 12 months of the official Bangladesh Bangla Academy revised calendar (2019 revision).
 *
 * Rules:
 * - Months 1..6 (Boishakh to Ashwin): 31 days
 * - Months 7..10 (Kartik to Magh): 30 days
 * - Month 11 (Falgun): 29 days, or 30 days in a Gregorian leap year
 * - Month 12 (Choitro): 30 days
 */
enum class BanglaMonth(
    val monthNumber: Int,
    val bengaliName: String,
    val englishName: String,
    val shortBengaliName: String,
    val shortEnglishName: String,
    val baseDays: Int,
    val season: BanglaSeason
) {
    BOISHAKH(
        monthNumber = 1,
        bengaliName = "বৈশাখ",
        englishName = "Boishakh",
        shortBengaliName = "বৈশাখ",
        shortEnglishName = "Boi",
        baseDays = 31,
        season = BanglaSeason.GRISHMO
    ),
    JYOISHTHO(
        monthNumber = 2,
        bengaliName = "জ্যৈষ্ঠ",
        englishName = "Jyoishtho",
        shortBengaliName = "জ্যৈষ্ঠ",
        shortEnglishName = "Jyo",
        baseDays = 31,
        season = BanglaSeason.GRISHMO
    ),
    ASHARH(
        monthNumber = 3,
        bengaliName = "আষাঢ়",
        englishName = "Asharh",
        shortBengaliName = "আষাঢ়",
        shortEnglishName = "Ash",
        baseDays = 31,
        season = BanglaSeason.BORSHA
    ),
    SHRABON(
        monthNumber = 4,
        bengaliName = "শ্রাবণ",
        englishName = "Shrabon",
        shortBengaliName = "শ্রাবণ",
        shortEnglishName = "Shr",
        baseDays = 31,
        season = BanglaSeason.BORSHA
    ),
    BHADRO(
        monthNumber = 5,
        bengaliName = "ভাদ্র",
        englishName = "Bhadro",
        shortBengaliName = "ভাদ্র",
        shortEnglishName = "Bha",
        baseDays = 31,
        season = BanglaSeason.SHOROT
    ),
    ASHWIN(
        monthNumber = 6,
        bengaliName = "আশ্বিন",
        englishName = "Ashwin",
        shortBengaliName = "আশ্বিন",
        shortEnglishName = "Asw",
        baseDays = 31,
        season = BanglaSeason.SHOROT
    ),
    KARTIK(
        monthNumber = 7,
        bengaliName = "কার্তিক",
        englishName = "Kartik",
        shortBengaliName = "কার্তিক",
        shortEnglishName = "Kar",
        baseDays = 30,
        season = BanglaSeason.HEMONTO
    ),
    OGROHAYON(
        monthNumber = 8,
        bengaliName = "অগ্রহায়ণ",
        englishName = "Ogrohayon",
        shortBengaliName = "অগ্রহায়ণ",
        shortEnglishName = "Ogr",
        baseDays = 30,
        season = BanglaSeason.HEMONTO
    ),
    POUSH(
        monthNumber = 9,
        bengaliName = "পৌষ",
        englishName = "Poush",
        shortBengaliName = "পৌষ",
        shortEnglishName = "Pou",
        baseDays = 30,
        season = BanglaSeason.SHEET
    ),
    MAGH(
        monthNumber = 10,
        bengaliName = "মাঘ",
        englishName = "Magh",
        shortBengaliName = "মাঘ",
        shortEnglishName = "Mag",
        baseDays = 30,
        season = BanglaSeason.SHEET
    ),
    FALGUN(
        monthNumber = 11,
        bengaliName = "ফাল্গুন",
        englishName = "Falgun",
        shortBengaliName = "ফাল্গুন",
        shortEnglishName = "Fal",
        baseDays = 29,
        season = BanglaSeason.BOSHONTO
    ),
    CHOITRO(
        monthNumber = 12,
        bengaliName = "চৈত্র",
        englishName = "Choitro",
        shortBengaliName = "চৈত্র",
        shortEnglishName = "Cho",
        baseDays = 30,
        season = BanglaSeason.BOSHONTO
    );

    fun displayName(useBengali: Boolean): String =
        if (useBengali) bengaliName else englishName

    companion object {
        fun fromMonthNumber(monthNumber: Int): BanglaMonth {
            require(monthNumber in 1..12) { "Bangla month number must be between 1 and 12, got $monthNumber" }
            return entries[monthNumber - 1]
        }
    }
}
