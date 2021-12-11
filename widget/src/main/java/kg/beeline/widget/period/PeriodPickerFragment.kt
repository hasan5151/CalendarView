package kg.beeline.widget.period

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.addListener
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.previous
import com.kizitonwose.calendarview.utils.yearMonth
import kg.beeline.shared.common.BaseBottomSheet
import kg.beeline.shared.extensions.*
import kg.beeline.widget.R
import kg.beeline.widget.databinding.FragmentPeriodPickerBinding
import kg.beeline.widget.ktx.visibleAnim
import kg.beeline.widget.models.PeriodRange
import kg.beeline.widget.models.PeriodType
import kg.beeline.widget.models.PeriodType.*
import kg.beeline.widget.period.PeriodUtils.getButtonIdByType
import kg.beeline.widget.period.PeriodUtils.getPeriod
import kg.beeline.widget.period.PeriodUtils.getTypeByButtonId
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*

/** Created by Jahongir on 05/01/2021.*/
class PeriodPickerFragment : BaseBottomSheet<FragmentPeriodPickerBinding>() {

    private val today = LocalDate.now()
    private var startDate: LocalDate? = null
    private var endDate: LocalDate? = null

    private var onSelectionListener: ((period: PeriodRange) -> Unit)? = null
    private var currentPeriod: PeriodRange? = null

    private var selectedPeriod: PeriodRange? = null

    private val monthLabels: Array<String> by lazy {
        requireContext().resources.getStringArray(R.array.month_labels)
    }

    override fun setupBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPeriodPickerBinding {
        return FragmentPeriodPickerBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        setupRadioGroup()
        setupCalendarControl()
        setupCalendar()
    }

    /**Radio Button Group*/
    private fun setupRadioGroup() = with(binding) {
        currentPeriod?.let {
            if (it.type != CUSTOM_RANGE) {
                val id = getButtonIdByType(it.type)
                periodGroup.check(id)
            } else {
                startDate = it.startDate
                endDate = if (it.endDate.isAfter(it.startDate)) it.endDate else null
            }
        }

        periodGroup.setOnCheckedChangeListener { _, checkedId ->
            val type = getTypeByButtonId(checkedId)
            lifecycleScope.launch {
                delay(350)
                onPeriodTypeSelected(type)
            }
        }
    }

    private fun hideRadioGroup() = with(binding) {

        val width = root.measuredWidth.toFloat()
        val transitionLong = resources.getInteger(R.integer.transition_long).toLong()
        val transitionShort = resources.getInteger(R.integer.transition_short).toLong()

        val radioAlpha = ObjectAnimator.ofFloat(periodGroup, "alpha", 1f, 0f)
        radioAlpha.duration = transitionShort

        val calendarAlpha = ObjectAnimator.ofFloat(calendarCon, "alpha", 0f, 1f)
        calendarAlpha.duration = transitionLong
        val calendarSlide = ObjectAnimator.ofFloat(calendarCon, "translationX", width, 0f)
        calendarSlide.duration = transitionLong

        val animSet = AnimatorSet()
        animSet.playTogether(radioAlpha, calendarAlpha, calendarSlide)

        animSet.addListener(
                onStart = {
                    calendarCon.isVisible = true
                },
                onEnd = {
                    periodGroup.isVisible = false
                    calendarView.notifyCalendarChanged()
                }
        )
        animSet.start()
    }

    private fun onPeriodTypeSelected(type: PeriodType) {
        when (type) {
            TODAY, THIS_MONTH, LAST_7_DAYS, LAST_30_DAYS -> {
                selectedPeriod = getPeriod(type)
                dismiss()
            }
            CUSTOM_RANGE -> {
                hideRadioGroup()
            }
        }
    }

    /**Calendar*/
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
        calendarView.setup(firstMonth, currentMonth, firstDayOfWeek)

        val scrollMonth = startDate?.yearMonth ?: currentMonth

        calendarView.scrollToMonth(scrollMonth)
        calendarView.monthScrollListener = {
            onMonthChanged(it.yearMonth)
        }
        val cellHeight = resources.getDimensionPixelSize(R.dimen.cell_height)
        calendarView.daySize = CalendarView.sizeAutoWidth(cellHeight)
        calendarCon.doOnPreDraw {
            calendarCon.isVisible = false
        }
    }

    private fun setupCalendarControl() = with(binding) {
        val yearMonth = today.yearMonth
        val monthIndex = yearMonth.month.ordinal
        if (monthLabels.containsIndex(monthIndex)) {
            val month = monthLabels[monthIndex]
            val label = "$month ${yearMonth.year}"
            calendarLabel.text = label
        }

        calendarNext.setOnClickListener {
            val month = calendarView.findFirstVisibleMonth() ?: return@setOnClickListener
            calendarView.smoothScrollToMonth(month.yearMonth.next)
        }
        calendarPrev.setOnClickListener {
            val month = calendarView.findFirstVisibleMonth() ?: return@setOnClickListener
            calendarView.smoothScrollToMonth(month.yearMonth.previous)
        }
        calendarConfirm.setOnClickListener {
            onCalendarConfirm()
        }
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
        invalidateActionButton()
    }

    private fun onBindCalendarDay(container: CellContainer, day: CalendarDay) {
        val cellLabel = container.cellLabel
        val cellStar = container.cellStart
        val cellEnd = container.cellEnd

        val startDate = startDate
        val endDate = endDate
        cellLabel.background = null
        cellStar.background = null
        cellEnd.background = null

        cellLabel.text = day.day.toString()
        if (day.owner == DayOwner.THIS_MONTH) {
            cellLabel.setColorRes(R.color.textColorPrimary)
        } else {
            cellLabel.setColorRes(R.color.textColorSecondary)
        }
        when {
            day.date == startDate && endDate == null -> {
                cellLabel.setColorRes(R.color.buttonTextColor)
                cellLabel.setBackgroundResource(R.drawable.cal_day_bg_single)
            }
            day.date == startDate -> {
                cellLabel.setColorRes(R.color.buttonTextColor)
                cellLabel.setBackgroundResource(R.drawable.cal_day_bg_single)
                cellEnd.setBackgroundResource(R.drawable.cal_day_bg_middle)
            }
            day.date == endDate -> {
                cellLabel.setColorRes(R.color.buttonTextColor)
                cellLabel.setBackgroundResource(R.drawable.cal_day_bg_single)
                cellStar.setBackgroundResource(R.drawable.cal_day_bg_middle)
            }
            startDate != null && endDate != null && (day.date > startDate && day.date < endDate) -> {
                cellStar.setBackgroundResource(R.drawable.cal_day_bg_middle)
                cellEnd.setBackgroundResource(R.drawable.cal_day_bg_middle)
            }
            day.date == today -> {
                cellLabel.setBackgroundResource(R.drawable.cal_day_bg_today)
            }
        }
    }

    private fun invalidateActionButton() = with(binding) {
        calendarConfirm.isEnabled = startDate != null
    }

    private fun onCalendarConfirm() {
        val startDate = startDate ?: return
        val endDate = endDate ?: startDate
        selectedPeriod = PeriodRange(startDate = startDate, endDate = endDate, type = CUSTOM_RANGE)
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val listener = onSelectionListener ?: return
        val period = selectedPeriod ?: return
        listener.invoke(period)
    }

    fun setCurrentPeriod(period: PeriodRange) {
        this.currentPeriod = period
    }

    fun setSelectionListener(listener: (period: PeriodRange) -> Unit) {
        this.onSelectionListener = listener
    }
}