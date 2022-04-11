package kg.beeline.widget.datepicker

import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.withStyledAttributes
import kg.beeline.widget.R
import kg.beeline.widget.databinding.DatePickerBinding
import kg.beeline.widget.view.NumberPicker
import kg.beeline.widget.view.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE
import java.time.LocalDate
import java.time.ZoneOffset

class StarkDatePickerView : FrameLayout {

    constructor(context: Context) :
            this(context, null)

    constructor(context: Context, attrs: AttributeSet?) :
            this(context, attrs, R.attr.starkDatePickerStyle)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            this(context, attrs, defStyleAttr, R.style.Widget_StarkDatePicker)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs, defStyleAttr, defStyleRes)
    }

    private val inflater by lazy { LayoutInflater.from(context) }

    private val binding = DatePickerBinding.inflate(inflater, this, true)

    var monthLabels = emptyList<String>()

    var textColor: Int = -1

    var dividerHeight: Int = -1
    var dividerColor: Int = -1
    var hapticEnabled = true

    private var dateChangeListener: ((localeDate: LocalDate) -> Unit)? = null
    private var epochChangeListener: ((epoch: Long) -> Unit)? = null

    var selectedDate = LocalDate.now()
    var minYear = LocalDate.now().minusYears(70).year
    var maxYear = LocalDate.now().year
    val today = LocalDate.now()

    var isTodayMaxDate: Boolean? = null

    var isTodayMinDate: Boolean? = null


    private val mediaPlayer: MediaPlayer by lazy { createMediaPlayer() }

    private fun init(set: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        context.withStyledAttributes(
            set,
            R.styleable.StarkDatePickerView,
            defStyleAttr,
            defStyleRes
        ) {
            if (textColor == -1) {
                textColor = getColorOrThrow(R.styleable.StarkDatePickerView_android_textColor)
            }
            if (dividerColor == -1) {
                dividerColor = getColorOrThrow(R.styleable.StarkDatePickerView_dividerColor)
            }
            if (dividerHeight == -1) {
                dividerHeight = getDimensionPixelSizeOrThrow(R.styleable.StarkDatePickerView_dividerHeight)
            }
            if (monthLabels.isEmpty()) {
                val resId = getResourceIdOrThrow(R.styleable.StarkDatePickerView_monthLabels)
                monthLabels = resources.getStringArray(resId).toList()
            }
        }
        setupView()
    }

    /**Call this method if you changed picker configuration*/
    fun setupPicker() {
        Log.d("StarkDatePicker","setupPicker")
        checkDates()
        setupDateRange()

        invalidateDayRange()
        invalidateMonthRange()
    }

    private fun setupView() {
        Log.d("StarkDatePicker","setupView")
        setupDateRange()
        setupDayPicker()
        setupMonthPicker()
        setupYearPicker()

    }

    private fun setupDayPicker() = with(binding) {
        pickerDay.setOnValueChangedListener { _, _, newVal ->
            selectedDate = selectedDate.withDayOfMonth(newVal)
            vibrateShot()
        }
        setupPickers(pickerDay)
    }

    private fun setupDateRange() = with(binding) {
        pickerDay.apply {
            minValue = 1
            maxValue = selectedDate.lengthOfMonth()
            value = selectedDate.dayOfMonth
            wrapSelectorWheel = false
        }
        pickerMonth.apply {
            minValue = 12
            maxValue = 12
            displayedValues = monthLabels.toTypedArray()
            value = selectedDate.monthValue
            wrapSelectorWheel = false
        }
        pickerYear.apply {
            minValue = minYear
            maxValue = maxYear
            value = selectedDate.year
            wrapSelectorWheel = false
        }
    }

    private fun setupMonthPicker() = with(binding) {
        pickerMonth.setOnValueChangedListener { _, _, newVal ->
            selectedDate = selectedDate.withMonth(newVal)
            invalidateDayRange()
            invalidateMonthRange()
            vibrateShot()
        }
        setupPickers(pickerMonth)
    }

    private fun setupYearPicker() = with(binding) {
        pickerYear.setOnValueChangedListener { _, _, newVal ->
            selectedDate = selectedDate.withYear(newVal)
            invalidateDayRange()
            invalidateMonthRange()
            vibrateShot()
        }
        setupPickers(pickerYear)
    }

    private fun setupPickers(picker: NumberPicker) {
        picker.apply {
            setOnScrollListener { _, scrollState ->
                if (scrollState == SCROLL_STATE_IDLE) {
                    onDateSelected()
                }
            }
            setTextColor(textColor)
            setSelectorColor(dividerColor)
            setDividerHeight(dividerHeight)
            wrapSelectorWheel = false
        }
    }

    private fun invalidateDayRange() {
        //Reset Max date
        with(binding.pickerDay) {
            maxValue = selectedDate.lengthOfMonth()
            wrapSelectorWheel = false

            if (isTodayMaxDate == true && selectedDate.year == today.year && selectedDate.monthValue == today.monthValue) {
                maxValue = today.dayOfMonth
                wrapSelectorWheel = true
            }

         /*   if (isTodayMinDate == true && selectedDate.year == today.year  && selectedDate.monthValue == today.monthValue) {
                minValue = today.dayOfMonth
                wrapSelectorWheel = true
            }*/
        }
    }

    private fun invalidateMonthRange() {
        //Reset Max Month
        with(binding.pickerMonth) {
            maxValue = 12
            wrapSelectorWheel = false
            Log.d("StarkDatePicker", "todays year ${today.year}")
            Log.d("StarkDatePicker", "selectedDate year ${selectedDate.year}")

            if (isTodayMaxDate == true && selectedDate.year == today.year) {
                maxValue = today.monthValue
                wrapSelectorWheel = false
            }

          /*  if (isTodayMinDate == true && selectedDate.year == today.year ) {
                Log.d("StarkDatePicker", "selectedDate min ${today.monthValue}")
                minValue = 12
//                value=12
                maxValue =12
                wrapSelectorWheel = false
            }*/
        }

        Log.d("StarkDatePicker", "selectedDate pickerMonth ${binding.pickerMonth.minValue}")

    }

    private fun checkDates() {
        if (minYear > maxYear) {
            val holder = maxYear
            maxYear = minYear
            minYear = holder
        }
        if (selectedDate.year > maxYear) {
            selectedDate = selectedDate.withYear(maxYear)
        }
        if (selectedDate.year < minYear) {
            selectedDate = selectedDate.withYear(minYear)
        }
    }

    private fun vibrateShot() {
        if (!hapticEnabled) return
        try {
            performHapticFeedback(HapticFeedbackConstants.CONFIRM, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
            }
        } catch (ex: Exception) {
            Log.e("StarkDatePicker", "vibrateShot Exception:", ex)
            ex.printStackTrace()
        }
    }

    private fun createMediaPlayer(): MediaPlayer {
        val player = MediaPlayer.create(context, R.raw.single_tick)
        player.setVolume(0.02f, 0.02f)
        return player
    }

    private fun onDateSelected() {
        dateChangeListener?.invoke(selectedDate)
        epochChangeListener?.apply {
            val epoch = selectedDate.atStartOfDay(ZoneOffset.UTC).toEpochSecond()
            this.invoke(epoch)
        }
    }

    fun setOnDateChangeListener(listener: (localeDate: LocalDate) -> Unit) {
        this.dateChangeListener = listener
    }
}