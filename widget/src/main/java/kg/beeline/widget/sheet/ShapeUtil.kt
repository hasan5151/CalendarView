package kg.beeline.widget.sheet

/** Created by Jahongir on 16/02/2021.*/
object ShapeUtil {
    fun dimenToOpacity(dimen: Int): Float {
        return when {
            dimen <= 0 -> 0.0f
            dimen == 1 -> 0.05f
            dimen == 2 -> 0.07f
            dimen == 3 -> 0.08f
            dimen in 4..5 -> 0.09f
            dimen in 6..7 -> 0.11f
            dimen in 8..11 -> 0.12f
            dimen in 12..16 -> 0.14f
            dimen in 16..22 -> 0.15f
            dimen in 22..24 -> 0.16f
            else -> 0.16f
        }
    }
}