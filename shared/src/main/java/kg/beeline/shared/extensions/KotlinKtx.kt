package kg.beeline.shared.extensions

/**Created by Jahongir on 11/08/2020.*/

@Suppress("NON_EXHAUSTIVE_WHEN")
val <T> T.checkAllMatched: T
    get() = this

fun String?.safeMatch(regex: Regex): Boolean = this?.matches(regex) == true

fun CharSequence?.isNotNullOrBlank(): Boolean {
    return !this.isNullOrBlank()
}