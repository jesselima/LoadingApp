package com.udacity.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import com.udacity.R

private const val START_LEFT = 0.0f
private const val START_TOP = 0.0f
private const val START_ANGLE = 0.0f
private const val END_ANGLE = 360
private const val CIRCLE_LOADING_DURATION = 4000L
private const val CIRCLE_LOADING_ELEVATION = 20f

class CircleIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var arc: Arc? = null
    private var valueAnimator: ValueAnimator? = null
    private var currentAngle = 0
    private val paintCircleLoadingView: Paint = Paint()

    init {
        elevation = CIRCLE_LOADING_ELEVATION
        paintCircleLoadingView.style = Paint.Style.FILL
        paintCircleLoadingView.isAntiAlias = true
        arc = Arc(
            start = START_ANGLE,
            sweep = END_ANGLE.toFloat(),
            color = ContextCompat.getColor(context, R.color.colorAccent)
        )
    }

    fun startAnimation() {
        valueAnimator?.cancel()
        valueAnimator = ValueAnimator.ofInt(START_ANGLE.toInt(), END_ANGLE).apply {
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            duration = CIRCLE_LOADING_DURATION
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animator ->
                currentAngle = animator.animatedValue as Int
                invalidate()
            }
        }
        valueAnimator?.start()
     }

    fun stopAnimation() {
        valueAnimator?.end()
        valueAnimator?.removeAllListeners()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        arc?.let { arc ->
            paintCircleLoadingView.color = arc.color
            if (currentAngle > arc.start + arc.sweep) {
                canvas.drawArc(
                    START_LEFT, START_TOP,  width.toFloat(), height.toFloat(),
                    START_ANGLE + arc.start,
                    arc.sweep,
                    true,
                    paintCircleLoadingView
                )
            } else {
                if (currentAngle > arc.start) {
                    canvas.drawArc(
                        START_LEFT, START_TOP,  width.toFloat(), height.toFloat(),
                        START_ANGLE + arc.start,
                        currentAngle - arc.start,
                        true,
                        paintCircleLoadingView
                    )
                }
            }
        }
    }
}

private class Arc(val start: Float, val sweep: Float, val color: Int)