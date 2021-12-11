package kg.beeline.shared.extensions

import android.text.Editable
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.postDelayed
import androidx.core.widget.doAfterTextChanged

/**Created by Jahongir on 1/17/2020.*/

inline fun EditText.setImeOptionDone(
        crossinline afterAfterKeyDone: (text: Editable?) -> Unit = {}): TextView.OnEditorActionListener {
    val keyListener = TextView.OnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            afterAfterKeyDone.invoke(text)
            return@OnEditorActionListener false
        }
        false
    }
    setOnEditorActionListener(keyListener)
    return keyListener
}

inline fun EditText.doAfterActionDone(
        crossinline action: (text: Editable?) -> Unit
) = setImeOptionDone(afterAfterKeyDone = action)


fun EditText.focusEnd() {
    if (isFocused) {
        setSelection(length())
    }
}

fun EditText.postText(newText: String?) {
    if (this.text.toString() != newText) {
        this.setText(newText)
    }
}

fun EditText.postTextChanged(listener: (text: Editable?) -> Unit) {
    this.doAfterTextChanged {
        listener.invoke(it)
    }
}


val Editable?.text: String?
    get() {
        return if (this.isNullOrBlank()) null
        else this.toString()
    }