package com.udacity.widgets

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import com.udacity.ButtonState
import com.udacity.R
import kotlin.properties.Delegates

class LoadingButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var buttonText: String = ""
    private var buttonTextColor: Int = 0
    private var buttonBackgroundColor: Int = 0
    private var isLoading = false

    private var widthSize = 0
    private var heightSize = 0

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) {
        property, oldValue, newValue ->
            // TODO here
    }

    init {
        elevation = 20f
        isClickable = true
        buttonText = resources.getString(R.string.button_name)
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonBackgroundColor = getColor(R.styleable.LoadingButton_buttonBackgroundColor, 0)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
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