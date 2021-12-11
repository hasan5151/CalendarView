package kg.beeline.widget.period

import kg.beeline.widget.models.PeriodRange

/** Created by Jahongir on 05/01/2021.*/
@DslMarker
internal annotation class PeriodFragmentMaker

@PeriodFragmentMaker
class PeriodFragmentBuilder {

    var onPeriodSelected: ((period: PeriodRange) -> Unit)? = null
    var currentPeriod: PeriodRange? = null

    fun build(): PeriodPickerFragment {
        val instance = PeriodPickerFragment()
        currentPeriod?.let {
            instance.setCurrentPeriod(it)
        }
        onPeriodSelected?.let {
            instance.setSelectionListener(it)
        }
        return instance
    }
}

fun periodPicker(init: PeriodFragmentBuilder.() -> Unit) =
        PeriodFragmentBuilder().also(init).build()