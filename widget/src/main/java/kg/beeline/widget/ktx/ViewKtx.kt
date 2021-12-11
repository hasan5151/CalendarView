package kg.beeline.widget.ktx

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kg.beeline.shared.extensions.setAnimationListener
import kg.beeline.shared.extensions.visible
import kg.beeline.widget.R

/** Created by Jahongir on 05/01/2021.*/
fun View.visibleAnim(show: Boolean, onEnd: (() -> Unit)? = null) {

    val animRes = if (show) R.anim.fade_in_fast else R.anim.fade_out_fast
    val animation = AnimationUtils.loadAnimation(context, animRes)
    animation.setAnimationListener(
            onStart = {
                if (show) {
                    this.visible(true)
                }
            },
            onEnd = {
                if (!show) {
                    this.visible(false)
                }
                onEnd?.invoke()
            }
    )
    val currentAnim: Animation? = this.animation

    if (currentAnim == null) {
        this.startAnimation(animation)
    } else {
        this.animation = animation
    }
}