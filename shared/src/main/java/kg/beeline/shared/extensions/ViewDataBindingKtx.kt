package kg.beeline.shared.extensions

import androidx.databinding.ViewDataBinding

/**Created by Jahongir on 28/04/2020.*/

inline fun <T : ViewDataBinding> T.executeAfter(block: T.() -> Unit) {
    block()
    executePendingBindings()
}