package kg.beeline.widget.datepicker

import androidx.annotation.StringRes
import java.time.LocalDate

/** Created by Jahongir on 16/02/2021.*/

@DslMarker
internal annotation class DatePickerMaker

@DatePickerMaker
class DatePickerDsl internal constructor() {

    /**Open picker with selected date*/
    var openAt: LocalDate? = null

    /**Set picker max year*/
    var maxYear: Int? = null

    /**Set picker min year*/
    var minYear: Int? = null

    @StringRes
    var titleRes:Int? = null

    var onDateChange: ((localDate: LocalDate) -> Unit)? = null
    var onDateSelected: ((localDate: LocalDate) -> Unit)? = null

    fun build(): DatePickerSheet {
        val picker = DatePickerSheet()
        setupDate(picker)
        onDateChange?.let { picker.onDateChangeListener = it }
        onDateSelected?.let { picker.onDateSelectListener = it }
        return picker
    }

    private fun setupDate(picker: DatePickerSheet) {
        openAt?.let { picker.openAt = it }
        minYear?.let { picker.minYear = it }
        maxYear?.let { picker.maxYear = it }
        titleRes?.let { picker.titleRes = it }
    }
}

fun datePicker(init: DatePickerDsl.() -> Unit) =
        DatePickerDsl().also(init).build()