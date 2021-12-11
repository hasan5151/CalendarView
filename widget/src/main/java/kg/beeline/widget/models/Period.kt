package kg.beeline.widget.models

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/** Created by Jahongir on 07/01/2021.*/
data class PeriodRange(val startDate: LocalDate, val endDate: LocalDate, val type: PeriodType) {
    val startDateTime: LocalDateTime
        get() = startDate.atTime(LocalTime.MIDNIGHT)

    val endDateTime: LocalDateTime
        get() = endDate.plusDays(1).atTime(LocalTime.MIDNIGHT)
}