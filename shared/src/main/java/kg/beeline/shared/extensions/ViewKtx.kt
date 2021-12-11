package kg.beeline.shared.extensions

import android.graphics.PointF
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import kg.beeline.shared.R

/**Created by Jahongir on 1/27/2020.*/

fun View.visible(show: Boolean) {
    if (this.isVisible == show) return
    this.isVisible = show
}

fun View.visibleAnimate(show: Boolean) {
    val animRes = if (show) R.anim.fade_in else R.anim.fade_out
    val animation = AnimationUtils.loadAnimation(context, animRes)
    animation.setAnimationListener(
            onStart = {
                if (show) {
                    this.visible(true)
                }
                this.isEnabled = false
            },
            onEnd = {
                this.isEnabled = true
                if (!show) {
                    this.visible(false)
                }
            }
    )
    this.startAnimation(animation)
}

/*fun View.postDelayed(delay: Long = 200, executor: () -> Unit) {
    this.postDelayed({
           executor.invoke()
     }, delay)
}*/


fun View.visibleOrInvisible(show: Boolean) {
    val visibility = if (show)
        View.VISIBLE
    else View.INVISIBLE
    if (this.visibility != visibility)
        this.visibility = visibility
}

fun View.toggleVisibility() {
    this.isVisible = !this.isVisible
}

fun View.getLocation(): PointF {
    val location = IntArray(2)
    this.getLocationInWindow(location)
    val x = location[0] + this.width / 2f
    val y = location[1] + this.height / 2f
    return PointF(x, y)
}


fun View.setMarginTop(value: Int) = updateLayoutParams<ViewGroup.MarginLayoutParams> {
    topMargin = value
}

fun View.setMarginBottom(value: Int) = updateLayoutParams<ViewGroup.MarginLayoutParams> {
    bottomMargin = value
}

val ViewGroup.inflater: LayoutInflater
    get() = LayoutInflater.from(context)
