package kg.beeline.shared.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView


/**Created by Jahongir on 24/03/2020.*/

class SignView : AppCompatImageView {


    private lateinit var canvas: Canvas
    private lateinit var linePaint: Paint
    private lateinit var bgPaint: Paint
    private lateinit var touchPath: Path
    private lateinit var bitmap: Bitmap

    private var isSignExists = false

    constructor(context: Context) : super(context) {
        setupView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setupView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setupView()
    }

    private fun setupView() {
        bgPaint = Paint()
        bgPaint.color = Color.RED

        linePaint = Paint()
        //Gives the width to the stroke.
        linePaint.strokeWidth = 7f
        //To give the smoothness to the outer side of the paint.
        linePaint.isAntiAlias = true
        //The colors that are higher precision than the device are   down-sampled y this flag.
        linePaint.isDither = true
        //It defines the style of how you want the paint to work, other is fill style which fills the area within the s
        linePaint.style = Paint.Style.STROKE
        //It defines how the end of the line should look.
        linePaint.strokeCap = Paint.Cap.ROUND
        linePaint.strokeJoin = Paint.Join.MITER

        //Set the color to the line.
        linePaint.color = Color.BLACK

        touchPath = Path()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchPath.moveTo(touchX, touchY)
            MotionEvent.ACTION_MOVE -> touchPath.lineTo(touchX, touchY)
            MotionEvent.ACTION_UP -> {
                touchPath.lineTo(touchX, touchY)
                canvas.drawPath(touchPath, linePaint)
                isSignExists = true
                touchPath = Path()
            }
            else -> return false
        }
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(bitmap, 0f, 0f, bgPaint)
        canvas?.drawPath(touchPath, linePaint)
    }

    fun clearCanvas() {
        touchPath.reset()
        //linePaint.reset()
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        invalidate()
        setImageBitmap(null)
        isSignExists = false
    }

    override fun setImageURI(uri: Uri?) {
        isSignExists = uri != null
        super.setImageURI(uri)
    }

    fun isSignExists(): Boolean = isSignExists
}