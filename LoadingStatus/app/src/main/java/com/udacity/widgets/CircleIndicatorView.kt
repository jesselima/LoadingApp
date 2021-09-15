package com.udacity.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.udacity.R

private const val START_ANGLE = 0.0f
private const val END_ANGLE = 360
private const val CIRCLE_LOADING_DURATION = 4000L

class CircleIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val rect: RectF = RectF(0f, 0f, 0f, 0f)

    private var valueAnimator: ValueAnimator? = null
    private var currentAngle = 0
    private var isLastLoop = false

    private val paint: Paint = Paint()
    private var arc: Arc? = null

    init {
        elevation = 20f
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        arc = Arc(
            start = START_ANGLE,
            sweep = END_ANGLE.toFloat(),
            color = ContextCompat.getColor(context, R.color.colorAccent)
        )
    }

    override fun onSizeChanged(width: Int, height: Int, oldwidth: Int, oldheight: Int) {
        super.onSizeChanged(width, height, oldwidth, oldheight)
        rect.set(0f, 0f, width.toFloat(), height.toFloat())
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

    fun setLastAnimationLoop() {
        isLastLoop = true
    }

    override fun onDraw(canvas: Canvas) {
        arc?.let { arc ->
            paint.color = arc.color
            if (currentAngle > arc.start + arc.sweep) {
                canvas.drawArc(
                    rect,
                    START_ANGLE + arc.start,
                    arc.sweep,
                    true,
                    paint
                )
            } else {
                if (currentAngle > arc.start) {
                    canvas.drawArc(
                        rect,
                        START_ANGLE + arc.start,
                        currentAngle - arc.start,
                        true,
                        paint
                    )
                }
            }
        }
    }
}

private class Arc(val start: Float, val sweep: Float, val color: Int)