package com.kizitonwose.calendar.compose

import com.kizitonwose.calendar.compose.yearcalendar.YearCalendarState
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.Year
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.minusYears
import com.kizitonwose.calendar.core.now
import com.kizitonwose.calendar.core.plusDays
import com.kizitonwose.calendar.core.plusYears
import com.kizitonwose.calendar.data.VisibleItemState
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlin.test.Test
import kotlin.test.assertEquals

class YearCalendarStateTests {
    @Test
    fun startYearUpdateIsReflectedInTheState() {
        val now = Year.now()
        val updatedStartYear = now.minusYears(8)
        val state = createState(
            startYear = now,
            endYear = now,
        )

        assertEquals(state.store[0].year, now)

        state.startYear = updatedStartYear

        assertEquals(state.store[0].year, updatedStartYear)
    }

    @Test
    fun endYearUpdateIsReflectedInTheState() {
        val now = Year.now()
        val updatedEndMonth = now.plusYears(8)
        val state = createState(
            startYear = now,
            endYear = now,
        )

        assertEquals(state.store[0].year, now)

        state.endYear = updatedEndMonth

        assertEquals(state.store[8].year, updatedEndMonth)
    }

    @Test
    fun firstDayOfTheWeekUpdateIsReflectedInTheState() {
        val firstDayOfWeek = LocalDate.now().dayOfWeek

        val state = createState(firstDayOfWeek = firstDayOfWeek)

        state.store[0].months
            .flatMap { month -> month.weekDays }
            .forEach { week ->
                assertEquals(week.first().date.dayOfWeek, firstDayOfWeek)
            }
        do {
            val updatedFirstDayOfWeek = state.firstDayOfWeek.plusDays(1)
            state.firstDayOfWeek = updatedFirstDayOfWeek

            state.store[0].months
                .flatMap { month -> month.weekDays }
                .forEach { week ->
                    assertEquals(week.first().date.dayOfWeek, updatedFirstDayOfWeek)
                }
        } while (firstDayOfWeek != state.firstDayOfWeek)
    }

    @Test
    fun outDateStyleUpdateIsReflectedInTheState() {
        val outDateStyle = OutDateStyle.EndOfRow
        // Nov 2022 has 5 weeks when Sun is the first day.
        val startYear = Year(2022)
        val state = createState(
            startYear = startYear,
            endYear = startYear,
            outDateStyle = outDateStyle,
            firstDayOfWeek = DayOfWeek.SUNDAY,
        )

        assertEquals(state.store[0].months[Month.NOVEMBER.ordinal].weekDays.count(), 5)

        state.outDateStyle = OutDateStyle.EndOfGrid

        assertEquals(state.store[0].months[Month.NOVEMBER.ordinal].weekDays.count(), 6)
    }

    private fun createState(
        startYear: Year = Year.now(),
        endYear: Year = startYear,
        firstVisibleYear: Year = startYear,
        outDateStyle: OutDateStyle = OutDateStyle.EndOfRow,
        firstDayOfWeek: DayOfWeek = firstDayOfWeekFromLocale(),
        visibleItemState: VisibleItemState = VisibleItemState(),
    ) = YearCalendarState(
        startYear = startYear,
        endYear = endYear,
        firstVisibleYear = firstVisibleYear,
        outDateStyle = outDateStyle,
        firstDayOfWeek = firstDayOfWeek,
        visibleItemState = visibleItemState,
    )
}
