package kg.beeline.widget.period

import android.view.View
import android.widget.TextView
import com.kizitonwose.calendarview.ui.ViewContainer
import kg.beeline.widget.databinding.ItemCalendarDayBinding

/** Created by Jahongir on 05/01/2021.*/
class CellContainer(view: View) : ViewContainer(view) {
    val cellLabel: TextView = ItemCalendarDayBinding.bind(view).cellLabel
    val cellStart: View = ItemCalendarDayBinding.bind(view).cellStartOverlay
    val cellEnd: View = ItemCalendarDayBinding.bind(view).cellEndOverlay
}