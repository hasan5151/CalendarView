package kg.beeline.widget.ktx

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

/** Created by Jahongir on 10/01/2021.*/
fun Long.toLocalDate(): LocalDate {
    if (this <= 1) return LocalDate.MIN
    return try {
        val instant = Instant.ofEpochSecond(this).atZone(ZoneId.systemDefault())
        instant.toLocalDate()
    } catch (ex: Exception) {
        LocalDate.MIN
    }
}

fun LocalDate.toEpochSeconds(): Long {
    return this.atTime(LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault()).toEpochSecond()
}