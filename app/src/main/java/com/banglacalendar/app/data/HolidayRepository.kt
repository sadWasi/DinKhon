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

import com.banglacalendar.app.domain.model.BanglaDate
import com.banglacalendar.app.domain.model.BanglaMonth
import com.banglacalendar.app.domain.model.HolidayCategory
import com.banglacalendar.app.domain.model.HolidayEvent
import java.time.LocalDate
import java.time.Month

/**
 * Interface for updateable, year-specific government-gazetted and moon-sighting-dependent
 * holidays. Kept separate from permanently fixed national and cultural dates.
 */
interface VariableHolidayDataSource {
    fun getHolidaysForDate(gregorianDate: LocalDate): List<HolidayEvent>
}

/**
 * Default updateable source for year-specific variable holidays.
 * Can be updated at runtime when official government gazettes or moon-sighting
 * announcements are published, without treating lunar dates as permanently fixed.
 */
class UpdateableVariableHolidayDataSource(
    initialYearEvents: Map<LocalDate, List<HolidayEvent>> = emptyMap()
) : VariableHolidayDataSource {

    private val eventsByDate = initialYearEvents.toMutableMap()

    fun updateHolidaysForDate(date: LocalDate, events: List<HolidayEvent>) {
        eventsByDate[date] = events
    }

    override fun getHolidaysForDate(gregorianDate: LocalDate): List<HolidayEvent> =
        eventsByDate[gregorianDate].orEmpty()
}

/**
 * Repository providing verified Bangladeshi national holidays and fixed cultural observances,
 * cleanly separated from the calendar calculation engine and from variable lunar holidays.
 */
class HolidayRepository(
    private val variableHolidayDataSource: VariableHolidayDataSource = UpdateableVariableHolidayDataSource()
) {

    private data class FixedGregorianRule(
        val month: Month,
        val dayOfMonth: Int,
        val event: HolidayEvent
    )

    private data class FixedBanglaRule(
        val month: BanglaMonth,
        val dayOfMonth: Int,
        val event: HolidayEvent
    )

    /**
     * Verified permanently fixed Bangladeshi national and public holidays on the Gregorian calendar.
     */
    private val fixedGregorianHolidays: List<FixedGregorianRule> = listOf(
        FixedGregorianRule(
            month = Month.FEBRUARY,
            dayOfMonth = 21,
            event = HolidayEvent(
                id = "intl_mother_language_day",
                titleBengali = "শহীদ দিবস ও আন্তর্জাতিক মাতৃভাষা দিবস",
                titleEnglish = "Shaheed Day & International Mother Language Day",
                descriptionBengali = "১৯৫২ সালের ভাষা আন্দোলনের শহীদদের স্মরণে জাতীয় ও আন্তর্জাতিক দিবস (৮ ফাল্গুন)।",
                descriptionEnglish = "National public holiday honoring the martyrs of the 1952 Bengali Language Movement (8 Falgun).",
                category = HolidayCategory.NATIONAL_HOLIDAY,
                isPublicHoliday = true
            )
        ),
        FixedGregorianRule(
            month = Month.MARCH,
            dayOfMonth = 26,
            event = HolidayEvent(
                id = "independence_day",
                titleBengali = "মহান স্বাধীনতা ও জাতীয় দিবস",
                titleEnglish = "Independence & National Day",
                descriptionBengali = "১৯৭১ সালের ২৬ মার্চ বাংলাদেশের স্বাধীনতা ঘোষণার ঐতিহাসিক জাতীয় দিবস (১২ চৈত্র)।",
                descriptionEnglish = "National public holiday commemorating Bangladesh's Declaration of Independence in 1971 (12 Choitro).",
                category = HolidayCategory.NATIONAL_HOLIDAY,
                isPublicHoliday = true
            )
        ),
        FixedGregorianRule(
            month = Month.MAY,
            dayOfMonth = 1,
            event = HolidayEvent(
                id = "may_day",
                titleBengali = "মে দিবস (আন্তর্জাতিক শ্রমিক দিবস)",
                titleEnglish = "May Day (International Workers' Day)",
                descriptionBengali = "শ্রমজীবী মানুষের অধিকার ও সংগ্রামের আন্তর্জাতিক স্মারক দিবস ও সরকারি ছুটি।",
                descriptionEnglish = "Public holiday celebrating international labor rights and workers' contributions.",
                category = HolidayCategory.PUBLIC_HOLIDAY,
                isPublicHoliday = true
            )
        ),
        FixedGregorianRule(
            month = Month.DECEMBER,
            dayOfMonth = 16,
            event = HolidayEvent(
                id = "victory_day",
                titleBengali = "মহান বিজয় দিবস",
                titleEnglish = "Victory Day of Bangladesh",
                descriptionBengali = "১৯৭১ সালের ১৬ ডিসেম্বর মহান মুক্তিযুদ্ধে চূড়ান্ত বিজয়ের জাতীয় দিবস (১ পৌষ)।",
                descriptionEnglish = "National public holiday celebrating the historic victory in the Liberation War of 1971 (1 Poush).",
                category = HolidayCategory.NATIONAL_HOLIDAY,
                isPublicHoliday = true
            )
        ),
        FixedGregorianRule(
            month = Month.DECEMBER,
            dayOfMonth = 25,
            event = HolidayEvent(
                id = "christmas_day",
                titleBengali = "যিশু খ্রিস্টের জন্মদিন (বড়দিন)",
                titleEnglish = "Christmas Day",
                descriptionBengali = "খ্রিস্টান সম্প্রদায়ের প্রধান ধর্মীয় উৎসব ও বাংলাদেশে সরকারি ছুটির দিন।",
                descriptionEnglish = "Major Christian festival and official public holiday in Bangladesh.",
                category = HolidayCategory.PUBLIC_HOLIDAY,
                isPublicHoliday = true
            )
        )
    )

    /**
     * Verified permanently fixed Bangla calendar holidays and cultural observances.
     */
    private val fixedBanglaEvents: List<FixedBanglaRule> = listOf(
        FixedBanglaRule(
            month = BanglaMonth.BOISHAKH,
            dayOfMonth = 1,
            event = HolidayEvent(
                id = "pohela_boishakh",
                titleBengali = "পহেলা বৈশাখ (বাংলা নববর্ষ)",
                titleEnglish = "Pohela Boishakh (Bengali New Year)",
                descriptionBengali = "বঙ্গাব্দের প্রথম দিন ও বাঙালির সার্বজনীন প্রাণের উৎসব (১৪ এপ্রিল)। সরকারি ছুটির দিন।",
                descriptionEnglish = "The first day of the Bengali New Year (14 April) and universal cultural festival of Bangladesh. Public holiday.",
                category = HolidayCategory.NATIONAL_HOLIDAY,
                isPublicHoliday = true
            )
        ),
        FixedBanglaRule(
            month = BanglaMonth.BOISHAKH,
            dayOfMonth = 25,
            event = HolidayEvent(
                id = "rabindra_jayanti",
                titleBengali = "রবীন্দ্রজয়ন্তী (২৫শে বৈশাখ)",
                titleEnglish = "Rabindra Jayanti (25 Boishakh)",
                descriptionBengali = "বিশ্বকবি রবীন্দ্রনাথ ঠাকুরের জন্মবার্ষিকী ও সাংস্কৃতিক উদযাপন দিবস।",
                descriptionEnglish = "Birth anniversary of Nobel laureate poet Rabindranath Tagore.",
                category = HolidayCategory.CULTURAL_OBSERVANCE,
                isPublicHoliday = false
            )
        ),
        FixedBanglaRule(
            month = BanglaMonth.JYOISHTHO,
            dayOfMonth = 11,
            event = HolidayEvent(
                id = "nazrul_jayanti",
                titleBengali = "নজরুলজয়ন্তী (১১ই জ্যৈষ্ঠ)",
                titleEnglish = "Nazrul Jayanti (11 Jyoishtho)",
                descriptionBengali = "বাংলাদেশের জাতীয় কবি কাজী নজরুল ইসলামের জন্মবার্ষিকী।",
                descriptionEnglish = "Birth anniversary of Kazi Nazrul Islam, the National Poet of Bangladesh.",
                category = HolidayCategory.CULTURAL_OBSERVANCE,
                isPublicHoliday = false
            )
        ),
        FixedBanglaRule(
            month = BanglaMonth.ASHARH,
            dayOfMonth = 1,
            event = HolidayEvent(
                id = "borsha_utsob",
                titleBengali = "বর্ষা উৎসব (পহেলা আষাঢ়)",
                titleEnglish = "Borsha Utsob (First Day of Monsoon)",
                descriptionBengali = "ষড়ঋতুর বর্ষা ঋতুর প্রথম দিন ও ঐতিহ্যবাহী বর্ষাবরণ উৎসব।",
                descriptionEnglish = "Traditional cultural observance welcoming the monsoon season on 1 Asharh.",
                category = HolidayCategory.CULTURAL_OBSERVANCE,
                isPublicHoliday = false
            )
        ),
        FixedBanglaRule(
            month = BanglaMonth.OGROHAYON,
            dayOfMonth = 1,
            event = HolidayEvent(
                id = "nabanna_utsob",
                titleBengali = "নবান্ন উৎসব (পহেলা অগ্রহায়ণ)",
                titleEnglish = "Nabanna Harvest Festival (1 Ogrohayon)",
                descriptionBengali = "হেমন্তে নতুন আমন ধান কাটার পর বাংলার চিরায়ত কৃষি ও নবান্ন উৎসব।",
                descriptionEnglish = "Traditional Bengali harvest celebration marking the arrival of new rice crops in Ogrohayon.",
                category = HolidayCategory.CULTURAL_OBSERVANCE,
                isPublicHoliday = false
            )
        ),
        FixedBanglaRule(
            month = BanglaMonth.FALGUN,
            dayOfMonth = 1,
            event = HolidayEvent(
                id = "pohela_falgun",
                titleBengali = "পহেলা ফাল্গুন (বসন্ত উৎসব)",
                titleEnglish = "Pohela Falgun (Spring Festival)",
                descriptionBengali = "ঋতুরাজ বসন্তের প্রথম দিন ও বাঙালির ঐতিহ্যবাহী বসন্তবরণ উৎসব (১৪ ফেব্রুয়ারি)।",
                descriptionEnglish = "First day of Spring in the Bangla calendar (14 February), celebrated with vibrant cultural festivities.",
                category = HolidayCategory.CULTURAL_OBSERVANCE,
                isPublicHoliday = false
            )
        ),
        FixedBanglaRule(
            month = BanglaMonth.CHOITRO,
            dayOfMonth = 30,
            event = HolidayEvent(
                id = "chaitra_sankranti",
                titleBengali = "চৈত্র সংক্রান্তি",
                titleEnglish = "Chaitra Sankranti",
                descriptionBengali = "বাংলা বছরের শেষ দিন (৩০ চৈত্র / ১৩ এপ্রিল) ও পুরনো বছরকে বিদায় জানানোর লোকজ উৎসব।",
                descriptionEnglish = "The final day of the Bengali year (30 Choitro / 13 April), celebrated with traditional fairs and festivities.",
                category = HolidayCategory.CULTURAL_OBSERVANCE,
                isPublicHoliday = false
            )
        )
    )

    /**
     * Returns all verified holidays and special events occurring on the given [gregorianDate] and [banglaDate].
     */
    fun getHolidaysForDate(gregorianDate: LocalDate, banglaDate: BanglaDate): List<HolidayEvent> {
        val result = mutableListOf<HolidayEvent>()

        for (rule in fixedBanglaEvents) {
            if (rule.month == banglaDate.month && rule.dayOfMonth == banglaDate.dayOfMonth) {
                result.add(rule.event)
            }
        }

        for (rule in fixedGregorianHolidays) {
            if (rule.month == gregorianDate.month && rule.dayOfMonth == gregorianDate.dayOfMonth) {
                if (result.none { it.id == rule.event.id }) {
                    result.add(rule.event)
                }
            }
        }

        for (variableEvent in variableHolidayDataSource.getHolidaysForDate(gregorianDate)) {
            if (result.none { it.id == variableEvent.id }) {
                result.add(variableEvent)
            }
        }

        return result
    }
}
