package com.udacity.widgets

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import com.udacity.R
import kotlin.properties.Delegates

private const val BUTTON_ELEVATION = 20f

class LoadingButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var valueAnimator: ValueAnimator? = null

    var isLoading = false
    var buttonText: CharSequence = ""

    private val buttonRect: RectF = RectF(0f, 0f, 0f, 0f)
    private var buttonTextColor: Int = 0
    private var buttonBackgroundColor: Int = 0

    private var widthSize = 0
    private var heightSize = 0

    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private val paintButton = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) {
        _, _, newValue ->
            when(newValue) {
                ButtonState.Loading -> {
                    buttonText = ""
                }
               else -> {

               }
            }
    }

    init {
        elevation = BUTTON_ELEVATION
        isClickable = true
        buttonText = resources.getString(R.string.button_name)
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonBackgroundColor = getColor(R.styleable.LoadingButton_buttonBackgroundColor, 0)
        }
    }

    override fun onDraw(canvas: Canvas) {
        // Update the "third param" value to set progress
        buttonRect.set(0f, 0f, width.toFloat() / 2, height.toFloat())
        canvas.drawRect(buttonRect, paintButton)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val width: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val height: Int = resolveSizeAndState(
            MeasureSpec.getSize(width),
            heightMeasureSpec,
            0
        )
        widthSize = width
        heightSize = height
        setMeasuredDimension(width, height)
    }
}