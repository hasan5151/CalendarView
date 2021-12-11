package kg.beeline.shared.bottomsheet

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.StyleRes
import com.google.android.material.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

@SuppressWarnings("unused")
open class BottomSheetDialogExpanded : BottomSheetDialog {

    constructor(context: Context) : super(context)

    constructor(context: Context, @StyleRes theme: Int) : super(context, theme)

    private lateinit var mBehavior: BottomSheetBehavior<FrameLayout>
    private var callback: BottomSheetBehavior.BottomSheetCallback? = null

    override fun setContentView(view: View) {
        super.setContentView(view)
        val bottomSheet = window?.decorView?.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout
        mBehavior = BottomSheetBehavior.from(bottomSheet)
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    @Deprecated("'use setContentView(view: View)'", ReplaceWith("super.setContentView(layoutResId)", "com.google.android.material.bottomsheet.BottomSheetDialog"))
    override fun setContentView(layoutResId: Int) {
        super.setContentView(layoutResId)
    }

    override fun onStart() {
        super.onStart()
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onDetachedFromWindow() {
        callback?.let {
            mBehavior.removeBottomSheetCallback(it)
        }
        super.onDetachedFromWindow()
    }

    fun setBottomSheetCallback(callback: BottomSheetBehavior.BottomSheetCallback) {
        this.callback = callback
        mBehavior.addBottomSheetCallback(callback)
    }
}