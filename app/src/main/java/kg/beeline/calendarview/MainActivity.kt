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
            this.maxYear = LocalDate.now().minusYears(14).year
            this.minYear = 1940
            this.openAt = LocalDate.now()
            this.onDateSelected = {
                Log.d("bur","selected birthday ${it}")
            }
        }
        picker.show(supportFragmentManager, "history_range_picker")
    }
}