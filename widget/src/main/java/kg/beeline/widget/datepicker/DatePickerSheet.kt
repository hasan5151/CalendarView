package kg.beeline.widget.datepicker

import android.view.LayoutInflater
import kg.beeline.widget.databinding.SheetDatePickerBinding
import kg.beeline.widget.sheet.SheetFragment
import java.time.LocalDate

/** Created by Jahongir on 18/02/2021.*/
class DatePickerSheet : SheetFragment<SheetDatePickerBinding>() {

    var onDateChangeListener: ((localDate: LocalDate) -> Unit)? = null
    var onDateSelectListener: ((localDate: LocalDate) -> Unit)? = null

    /**Open picker with selected date*/
    var openAt: LocalDate? = null

    /**Set picker max year*/
    var maxYear: Int? = null

    /**Set picker min year*/
    var minYear: Int? = null

    /**Set title string res*/
    var titleRes:Int? = null

    var isTodayMaxDate : Boolean? = false
//    var isTodayMinDate : Boolean? = false


    override fun createSheet(inflater: LayoutInflater): SheetDatePickerBinding {
        return SheetDatePickerBinding.inflate(inflater)
    }

    override fun setupSheet() = with(binding) {
        setupPickerDates()
        btnSelect.setOnClickListener {
            dismiss()
            onDateSelectListener?.invoke(datePicker.selectedDate)
        }
        datePicker.setOnDateChangeListener {
            onDateChangeListener?.invoke(it)
        }
    }

    private fun setupPickerDates() = with(binding){
        openAt?.let { datePicker.selectedDate = it }
        maxYear?.let { datePicker.maxYear = it }
        minYear?.let { datePicker.minYear = it }
        titleRes?.let { title.setText(it) }
        isTodayMaxDate?.let { datePicker.isTodayMaxDate= it }
//        isTodayMinDate?.let { datePicker.isTodayMinDate = it }
        datePicker.setupPicker()
    }
}