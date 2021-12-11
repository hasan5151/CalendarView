package kg.beeline.shared.extensions

import android.app.Activity
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

/**Created by Jahongir on 1/30/2020.*/

fun Activity.fetchColor(@ColorRes colorResId: Int): Int = ContextCompat.getColor(this, colorResId)