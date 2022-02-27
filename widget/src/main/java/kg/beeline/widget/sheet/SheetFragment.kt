package kg.beeline.widget.sheet

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.withStyledAttributes
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.color.MaterialColors
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import kg.beeline.widget.R
import kotlin.math.roundToInt


/**
 * This class is the base of all types of sheets.
 * You can implement this class in your own and build your
 * own custom sheet with the already existing features which the base class offers.
 */
abstract class SheetFragment<VB : ViewBinding> : DialogFragment() {

    private var sheetTheme = R.style.SheetTheme
    private var behavior = BottomSheetBehavior.STATE_EXPANDED
    private var peekHeight = 0

    /** Override theme to allow auto switch between day & night design. */
    override fun getTheme(): Int = sheetTheme

    private var backgroundColor = -1
    private var elevation = -1
    private var cornerFamily = -1
    private var cornerRadius = -1

    private var bindingHolder: VB? = null
    protected val binding: VB
        get() = bindingHolder!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contextInflater = LayoutInflater.from(requireContext())
        bindingHolder = createSheet(contextInflater)
        return binding.root
    }

    abstract fun createSheet(inflater: LayoutInflater): VB

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBottomSheetBackground(view)
        setupBottomSheetBehavior(view)
        setupSheet()
    }

    abstract fun setupSheet()

    private fun createSheetShape(): MaterialShapeDrawable {
        setupSheetStyle()
        val modelBuilder = ShapeAppearanceModel().toBuilder()
        if (cornerFamily != -1 && cornerRadius > 0) {
            val family = when (cornerFamily) {
                0 -> CornerFamily.ROUNDED
                else -> CornerFamily.CUT
            }
            modelBuilder.apply {
                setTopRightCorner(family, cornerRadius.toFloat())
                setTopLeftCorner(family, cornerRadius.toFloat())
            }
        }
        val model = modelBuilder.build()
        Log.d("SheetFragment", "createSheetShape: $elevation")

        val shapeColor = if (elevation > 0) {
            val realDp: Int = (elevation / resources.displayMetrics.density).roundToInt()
            val alpha = ShapeUtil.dimenToOpacity(realDp)

            /**For day night theme use this overlay color*/
            //val overlayColor = ContextCompat.getColor(requireContext(), R.color.sheetOverlayColor)
            MaterialColors.layer(backgroundColor, Color.WHITE, alpha)
        } else {
            backgroundColor
        }
        return MaterialShapeDrawable(model).apply {
            fillColor = ColorStateList.valueOf(shapeColor)
        }
    }

    private fun setupSheetStyle() {
        context?.withStyledAttributes(null, R.styleable.SheetFragment, R.attr.sheetStyle, R.style.SheetStyle) {
            if (backgroundColor == -1) {
                backgroundColor = getColorOrThrow(R.styleable.SheetFragment_android_colorBackground)
            }
            if (elevation == -1) {
                elevation = getDimensionPixelSizeOrThrow(R.styleable.SheetFragment_android_elevation)
            }
            if (cornerFamily == -1) {
                cornerFamily = getInt(R.styleable.SheetFragment_bs_cornerFamily, -1)
            }
            if (cornerRadius == -1) {
                cornerRadius = getDimensionPixelSizeOrThrow(R.styleable.SheetFragment_bs_cornerRadius)
            }
        }
    }

    private fun setupBottomSheetBackground(view: View) {
        val shape = createSheetShape()
        shape.fillColor?.let {
            dialog?.window?.navigationBarColor = it.defaultColor
        }
        view.background = shape
    }

    private fun setupBottomSheetBehavior(view: View) {

        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val dialog = dialog as? BottomSheetDialog? ?: return
                val dialogBehavior = dialog.behavior
                dialogBehavior.state = behavior
                dialogBehavior.peekHeight = peekHeight
                dialogBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onSlide(bottomSheet: View, dY: Float) {
                        // TODO: Make button layout stick to the bottom through translationY property.
                    }

                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                            dismiss()
                        }
                    }
                })
            }
        })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme)
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        val acd = dialog as AppCompatDialog
        when (style) {
            STYLE_NO_INPUT -> {
                dialog.getWindow()?.addFlags(
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
                acd.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
            }
            STYLE_NO_FRAME, STYLE_NO_TITLE -> acd.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        }
    }
}