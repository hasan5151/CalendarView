package kg.beeline.shared.extensions

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

/**Created by Jahongir on 24/08/2020.*/

fun RecyclerView.setDivider(@DrawableRes drawableRes: Int) {
    /*val divider = DividerItemDecoration(
            this.context,
            DividerItemDecoration.VERTICAL
    )*/
    val drawable = ContextCompat.getDrawable(
            this.context,
            drawableRes
    )
    drawable?.let {
        //divider.setDrawable(it)
        addItemDecoration(DividerItemDecorator(it))
    }
}

class DividerItemDecorator(private val divider: Drawable?) : RecyclerView.ItemDecoration() {

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val dividerLeft = parent.paddingLeft
        val dividerRight = parent.width - parent.paddingRight
        val childCount = parent.childCount
        for (i in 0..childCount - 2) {
            val child: View = parent.getChildAt(i)
            val params =
                    child.layoutParams as RecyclerView.LayoutParams
            val dividerTop: Int = child.bottom + params.bottomMargin
            val dividerBottom = dividerTop + (divider?.intrinsicHeight?:0)
            divider?.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
            divider?.draw(canvas)
        }
    }
}