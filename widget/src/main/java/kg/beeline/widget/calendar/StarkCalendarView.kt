package kg.beeline.widget.calendar

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.withStyledAttributes
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.previous
import com.kizitonwose.calendarview.utils.yearMonth
import kg.beeline.shared.extensions.containsIndex
import kg.beeline.widget.R
import kg.beeline.widget.databinding.CalendarBinding
import kg.beeline.widget.ktx.toEpochSeconds
import kg.beeline.widget.ktx.toLocalDate
import kg.beeline.widget.ktx.visibleAnim
import kg.beeline.widget.period.CellContainer
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*

/** Created by Jahongir on 10/01/2021.*/
class StarkCalendarView : FrameLayout {

    private val inflater by lazy { LayoutInflater.from(context) }

    private val binding: CalendarBinding = CalendarBinding.inflate(inflater, this, true)

    constructor(context: Context) :
            this(context, null)

    constructor(context: Context, attrs: AttributeSet?) :
            this(context, attrs, R.attr.starkCalendarStyle)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            this(context, attrs, defStyleAttr, R.style.Widget_StarkCalendar)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs, defStyleAttr, defStyleRes)
    }

    var leftChevron: Drawable? = null
        set(value) {
            field = value
            requestLayout()
        }

    var rightChevron: Drawable? = null
        set(value) {
            field = value
            requestLayout()
        }

    private var weekDays: Array<String> = emptyArray()
        set(value) {
            field = value
            requestLayout()
        }

    private var monthLabels: Array<String> = emptyArray()

    var selectedBackgroundDrawable: Drawable? = null
        set(value) {
            field = value
            refreshCalendar()
        }

    var selectedTextColor: Int = 0
        set(value) {
            field = value
            refreshCalendar()
        }

    var middleBackgroundColor: Int = 0
        set(value) {
            field = value
            refreshCalendar()
        }

    var middleTextColor: Int = 0
        set(value) {
            field = value
            refreshCalendar()
        }

    var todayBackgroundDrawable: Drawable? = null
        set(value) {
            field = value
            refreshCalendar()
        }

    var todayTextColor: Int = 0
        set(value) {
            field = value
            refreshCalendar()
        }

    var monthDayColor: Int = 0
        set(value) {
            field = value
            refreshCalendar()
        }

    var outMonthDayColor: Int = 0
        set(value) {
            field = value
            refreshCalendar()
        }

    var cellHeight: Int = 0
        set(value) {
            field = value
            calendarDaySizeChanged()
        }

    private val today = LocalDate.now()

    private var startDate: LocalDate? = null
    private var endDate: LocalDate? = null

    private var isInitializing = true

    private var listener: CalendarSelectListener? = null

    private fun init(attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        context.withStyledAttributes(attributeSet, R.styleable.StarkCalendarView, defStyleAttr, defStyleRes) {
            //Control
            leftChevron = getDrawableOrThrow(R.styleable.StarkCalendarView_scv_leftChevron)
            rightChevron = getDrawableOrThrow(R.styleable.StarkCalendarView_scv_rightChevron)
            //WeekDays
            val weekResId = getResourceIdOrThrow(R.styleable.StarkCalendarView_scv_weekLabels)
            weekDays = resources.getStringArray(weekResId)

            val monthResId = getResourceIdOrThrow(R.styleable.StarkCalendarView_scv_monthLabels)
            monthLabels = resources.getStringArray(monthResId)

            monthDayColor = getColorOrThrow(R.styleable.StarkCalendarView_scv_monthDayColor)
            outMonthDayColor = getColorOrThrow(R.styleable.StarkCalendarView_scv_outMonthDayColor)

            selectedBackgroundDrawable = getDrawableOrThrow(R.styleable.StarkCalendarView_scv_selectedBackground)
            selectedTextColor = getColorOrThrow(R.styleable.StarkCalendarView_scv_selectedTextColor)

            middleBackgroundColor = getColorOrThrow(R.styleable.StarkCalendarView_scv_middleBackgroundColor)
            middleTextColor = getColorOrThrow(R.styleable.StarkCalendarView_scv_middleTextColor)

            todayBackgroundDrawable = getDrawableOrThrow(R.styleable.StarkCalendarView_scv_todayBackground)
            todayTextColor = getColorOrThrow(R.styleable.StarkCalendarView_scv_todayTextColor)

            cellHeight = getDimensionPixelSizeOrThrow(R.styleable.StarkCalendarView_scv_cell_height)

            isInitializing = false
        }
        setup()
    }

    private fun setup() {
        setupCalendarControl()
        setupCalendar()
    }

    private fun setupWeekLabels() = with(binding) {
        if (weekDays.size < 7) throw RuntimeException("Week days label references must be exact 7 days")
        dowMon.text = weekDays[0]
        dowTue.text = weekDays[1]
        dowWed.text = weekDays[2]
        dowThu.text = weekDays[3]
        dowFri.text = weekDays[4]
        dowSat.text = weekDays[5]
        dowSun.text = weekDays[6]
    }

    private fun setupCalendarControl() = with(binding) {
        val yearMonth = today.yearMonth
        val monthIndex = yearMonth.month.ordinal
        if (monthLabels.containsIndex(monthIndex)) {
            val month = monthLabels[monthIndex]
            val label = "$month ${yearMonth.year}"
            calendarLabel.text = label
        }
        calendarPrev.setImageDrawable(leftChevron)
        calendarNext.setImageDrawable(rightChevron)

        calendarNext.setOnClickListener {
            val month = calendarView.findFirstVisibleMonth() ?: return@setOnClickListener
            calendarView.smoothScrollToMonth(month.yearMonth.next)
        }
        calendarPrev.setOnClickListener {
            val month = calendarView.findFirstVisibleMonth() ?: return@setOnClickListener
            calendarView.smoothScrollToMonth(month.yearMonth.previous)
        }
        setupWeekLabels()
    }

    private fun setupCalendar() = with(binding) {
        calendarView.dayBinder = object : DayBinder<CellContainer> {
            override fun create(view: View) = CellContainer(view)
            override fun bind(container: CellContainer, day: CalendarDay) {
                onBindCalendarDay(container, day)
                container.view.setOnClickListener {
                    onDayClicked(day)
                }
            }
        }
        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(12)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek

        if (cellHeight > 0) {
            calendarView.daySize = CalendarView.sizeAutoWidth(cellHeight)
        }

        calendarView.setup(firstMonth, currentMonth, firstDayOfWeek)

        calendarView.scrollToMonth(currentMonth)

        calendarView.monthScrollListener = {
            onMonthChanged(it.yearMonth)
        }

    }

    private fun onBindCalendarDay(container: CellContainer, day: CalendarDay) {
        val cellLabel = container.cellLabel
        val cellStar = container.cellStart
        val cellEnd = container.cellEnd

        val startDate = this.startDate
        val endDate = this.endDate
        cellLabel.background = null
        cellStar.background = null
        cellEnd.background = null

        cellLabel.text = day.day.toString()
        if (day.owner == DayOwner.THIS_MONTH) {
            cellLabel.setTextColor(monthDayColor)
        } else {
            cellLabel.setTextColor(outMonthDayColor)
        }
        when {
            day.date == startDate && endDate == null -> {
                cellLabel.setTextColor(selectedTextColor)
                cellLabel.background = selectedBackgroundDrawable
            }
            day.date == startDate -> {
                cellLabel.setTextColor(selectedTextColor)
                cellLabel.background = selectedBackgroundDrawable
                cellEnd.setBackgroundColor(middleBackgroundColor)
            }
            day.date == endDate -> {
                cellLabel.setTextColor(selectedTextColor)
                cellLabel.background = selectedBackgroundDrawable
                cellStar.setBackgroundColor(middleBackgroundColor)
            }
            startDate != null && endDate != null && (day.date > startDate && day.date < endDate) -> {
                cellStar.setBackgroundColor(middleBackgroundColor)
                cellEnd.setBackgroundColor(middleBackgroundColor)
            }
            day.date == today -> {
                cellLabel.background = todayBackgroundDrawable
                cellLabel.setTextColor(todayTextColor)
            }
        }
    }

    private fun onDayClicked(day: CalendarDay) = with(binding) {
        val date = day.date
        when {
            startDate == null -> startDate = date
            endDate != null -> {
                startDate = date
                endDate = null
            }
            date > startDate -> endDate = date
            date < startDate -> {
                endDate = startDate
                startDate = date
            }
        }
        calendarView.notifyCalendarChanged()
        if (day.owner != DayOwner.THIS_MONTH) {
            val scrollMonth = date.yearMonth
            calendarView.scrollToMonth(scrollMonth)
        }
        onDateSelected()
    }

    private fun onMonthChanged(yearMonth: YearMonth) = with(binding) {
        val monthIndex = yearMonth.month.ordinal
        if (monthLabels.containsIndex(monthIndex)) {
            val month = monthLabels[monthIndex]
            val label = "$month ${yearMonth.year}"
            if (calendarLabel.text != label) {
                calendarLabel.visibleAnim(false) {
                    calendarLabel.text = label
                    calendarLabel.visibleAnim(true)
                }
            }
        }
    }

    private fun onDateSelected() {
        val starEpoch = startDate?.toEpochSeconds() ?: return
        val endEpoch = endDate?.toEpochSeconds() ?: 0L
        listener?.onSelected(startData = starEpoch, endData = endEpoch)
    }

    private fun refreshCalendar() {
        if (isInitializing) return
        Log.d("StarkCalendarView", "refreshCalendar: ")
    }

    private fun calendarDaySizeChanged() = with(binding) {
        if (isInitializing) return
        Log.d("StarkCalendarView", "calendarDaySizeChanged: ")
        if (cellHeight > 0) {
            calendarView.daySize = CalendarView.sizeAutoWidth(cellHeight)
        }
    }

    fun setStartDate(startDate: Long) {
        this.startDate = startDate.toLocalDate()
    }

    fun setEndDate(endDate: Long) {
        this.endDate = endDate.toLocalDate()
    }

    fun setRangeDate(startDate: Long, endDate: Long) {
        this.startDate = startDate.toLocalDate()
        this.endDate = endDate.toLocalDate()
    }

    fun setDateSelectListener(listener: CalendarSelectListener) {
        this.listener = listener
    }


}