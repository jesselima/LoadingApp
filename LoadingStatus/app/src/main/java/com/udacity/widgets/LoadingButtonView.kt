package com.udacity.widgets

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.withStyledAttributes
import com.udacity.R
import kotlin.properties.Delegates

private const val BUTTON_ELEVATION = 20f
private const val START_LEFT = 0f
private const val START_TOP = 0f
private const val START_RIGHT = 0f
private const val START_BOTTOM = 0f
private const val LOADING_PROGRESS_REFERENCE = 360
private const val LOADING_DURATION = 4000L

class LoadingButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var valueAnimator: ValueAnimator? = null
    private val buttonRect: RectF = RectF(START_LEFT, START_TOP, START_RIGHT, START_BOTTOM)
    private var currentProgress = 0

    var isLoading = false
    var buttonText: CharSequence = ""

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
                    startAnimation()
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
        buttonRect.set(0f, 0f, currentProgress.toFloat(), height.toFloat())
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

    fun startAnimation() {
        valueAnimator?.cancel()
        valueAnimator = ValueAnimator.ofInt(START_LEFT.toInt(), width).apply {
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            duration = LOADING_DURATION
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animator ->
                currentProgress = animator.animatedValue as Int
                invalidate()
            }
        }
        valueAnimator?.start()
    }

    fun stopAnimation() {
        valueAnimator?.end()
    }

}