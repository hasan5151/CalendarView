package kg.beeline.shared.extensions

import android.util.Log
import java.time.*
import java.time.format.DateTimeFormatter

/** Created by Jahongir on 10/01/2021.*/

val defaultZoneId: ZoneId by lazy { ZoneId.systemDefault() }

val defaultOffset: ZoneOffset by lazy { OffsetDateTime.now(defaultZoneId).offset }

val LocalDate.startDateTime: LocalDateTime
    get() = this.atTime(LocalTime.MIDNIGHT)

val LocalDate.endDateTime: LocalDateTime
    get() = this.atTime(LocalTime.MAX)

val LocalDateTime.epochSecond: Long
    get() = this.toEpochSecond(defaultOffset)

val LocalDate.epochSecond: Long
    get() = this.startDateTime.epochSecond

fun String?.isoDateToEpoch(): Long {
    if (this.isNullOrBlank()) return 0L
    return try {
        val remoteFormatter = DateTimeFormatter.ISO_DATE_TIME
        val dateTime = LocalDateTime.parse(this, remoteFormatter)
        dateTime.atZone(defaultZoneId).toEpochSecond()
    } catch (ex: Exception) {
        Log.e("String", "isoDateToEpoch: Exception:", ex)
        0
    }
}

fun String?.toBasicDate(): String {
    if (this.isNullOrBlank()) return ""
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    return try {
        val dateTime = LocalDateTime.parse(this)
        return formatter.format(dateTime)
    } catch (ex: Exception) {
        ""
    }
}
