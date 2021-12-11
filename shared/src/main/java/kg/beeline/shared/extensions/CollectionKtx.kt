package kg.beeline.shared.extensions

/** Created by Jahongir on 05/01/2021.*/
class CollectionKtx {
}

fun <T> Array<out T>.containsIndex(index: Int): Boolean = index in 0..this.lastIndex

fun <T> List<T>.isIndexValid(index: Int): Boolean = index in 0..this.lastIndex

fun <T> List<T>.isIndexInvalid(index: Int): Boolean = index !in 0..this.lastIndex




fun <T> List<T>.safeGet(index: Int): T? {
    if (this.isIndexInvalid(index)) return null
    return this[index]
}