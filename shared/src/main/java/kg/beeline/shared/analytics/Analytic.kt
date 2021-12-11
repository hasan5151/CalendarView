package kg.beeline.shared.analytics

import androidx.annotation.Keep

/** Created by Jahongir on 20/01/2021.*/
@Keep
object Analytic {
    object Event {
        const val PASSPORT_IDENTIFICATION = "passport_identification"
        const val SIM_ACTIVATION = "sim_activation"
    }

    object Param {
        const val LEVEL = "level"
        const val DURATION = "duration"
    }
}