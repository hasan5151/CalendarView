package kg.beeline.widget.period

import kg.beeline.widget.R
import kg.beeline.widget.models.PeriodRange
import kg.beeline.widget.models.PeriodType
import kg.beeline.widget.models.PeriodType.*
import java.time.LocalDate

/** Created by Jahongir on 06/01/2021.*/
internal object PeriodUtils {

    private val buttonsMap = HashMap<Int, PeriodType>()

    init {
        initMap()
    }

    fun getPeriod(type: PeriodType): PeriodRange {
        val today = LocalDate.now()
        val starDate = when (type) {
            TODAY -> today
            THIS_MONTH -> today.withDayOfMonth(1)
            LAST_7_DAYS -> today.minusDays(7)
            LAST_30_DAYS -> today.minusDays(30)
            else -> throw IllegalArgumentException("Wrong period type passed")
        }
        return PeriodRange(startDate = starDate, endDate = today, type = type)
    }

    fun getTypeByButtonId(checkedId: Int): PeriodType {
        /*if (!buttonsMap.containsKey(checkedId)) {
            initMap()
        }*/
        return buttonsMap.getValue(checkedId)
    }

    fun getButtonIdByType(type: PeriodType): Int {
        /* if (!buttonsMap.containsValue(type)) {
             initMap()
         }*/
        for (id in buttonsMap.keys) {
            val buttonType = buttonsMap[id]
            if (buttonType == type) {
                return id
            }
        }
        throw NoSuchElementException("ButtonId not found for given period type : $type")
    }

    private fun initMap() {
        buttonsMap.clear()
        buttonsMap[R.id.period_today] = TODAY
        buttonsMap[R.id.period_this_month] = THIS_MONTH
        buttonsMap[R.id.period_7_days] = LAST_7_DAYS
        buttonsMap[R.id.period_30_days] = LAST_30_DAYS
        buttonsMap[R.id.period_range] = CUSTOM_RANGE
    }
}