package kg.beeline.shared.widget


import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.animation.addListener

/** Created by Jahongir on 29/12/2020.*/
class ExpandableLayout : LinearLayout {

    enum class State {
        EXPAND,
        COLLAPSE
    }

    private var currentState: State = State.COLLAPSE

    @Volatile
    private var animating: Boolean = false

    @Volatile
    private var expandableViewHeight: Int = 0


    init {
        orientation = VERTICAL
        setState(State.COLLAPSE)
    }

    private val heightAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        addUpdateListener {
            this@ExpandableLayout.layoutParams.height = (it.animatedFraction * expandableViewHeight).toInt()
            requestLayout()
        }
    }

    constructor(context: Context) : super(context)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attributeSet, defStyleAttr, defStyleRes)

    constructor(context: Context, attributeSet: AttributeSet)
            : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int)
            : super(context, attributeSet, defStyleAttr)


    private fun setState(state: State) {
        when (state) {
            State.EXPAND -> expand(0)
            State.COLLAPSE -> collapse(0)
        }
    }

    fun expand(duration: Long = 300) {
        animate(State.EXPAND, duration)
    }

    fun collapse(duration: Long = 300) {
        animate(State.COLLAPSE, duration)
    }

    fun toggle(): Boolean {
        return if (currentState == State.EXPAND) {
            collapse()
            false
        } else {
            expand()
            true
        }
    }

    private fun animate(state: State, duration: Long) {
        if (!animating) {
            expandableViewHeight = this.getMeasurements(this).second
            if (duration == 0L) {
                if (state == State.EXPAND) {
                    this.layoutParams.height = expandableViewHeight
                } else {
                    this.layoutParams.height = 0
                }
                requestLayout()
            } else {
                heightAnimator.duration = duration
                heightAnimator.addListener(
                        onStart = { animating = true },
                        onEnd = {
                            this.currentState = state
                            animating = false
                        })
                if (state == State.EXPAND) {
                    heightAnimator.start()
                } else {
                    heightAnimator.reverse()
                }
            }
        }
    }
}

fun View.getMeasurements(parent: View): Pair<Int, Int> {
    measure(View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED))
    return Pair(measuredWidth, measuredHeight)
}

fun View.getMeasuredHeight(parent: View):Int {
    measure(View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.AT_MOST))
    return measuredHeight
}