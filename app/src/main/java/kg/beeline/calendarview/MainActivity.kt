package kg.beeline.calendarview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kg.beeline.widget.datepicker.datePicker
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val picker = datePicker {
            this.titleRes = R.string.app_name
            this.maxYear = LocalDate.now().year
            this.minYear = 2020
            this.openAt = LocalDate.now()
            this.isTodayMaxDate = true
//            this.isTodayMinDate = true
            this.onDateSelected = {
                Log.d("onDateSelected","invalidateMonthRange selected birthday ${it}")
            }
        }

        picker.show(supportFragmentManager, "history_range_picker")
    }
}