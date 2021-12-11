package kg.beeline.shared.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.annotation.StyleRes
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**Created by Jahongir on 9/5/2019.*/

abstract class BottomSheetFragment(@StyleRes private val customTheme: Int? = null)
    : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = customTheme?.let {
            BottomSheetDialogExpanded(requireContext(), customTheme)
        } ?: BottomSheetDialogExpanded(requireContext())
        dialog.setOnShowListener {
            setCallBack(dialog)
        }
        return dialog
    }

    private fun setCallBack(dialog: BottomSheetDialogExpanded) {
        dialog.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                onSlide(slideOffset)
                if (slideOffset == -1f)
                    dialog.dismiss()
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
        })
    }

    open fun onSlide(slideOffset: Float) {

    }
}