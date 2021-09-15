package com.udacity.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View

fun Animator.disableViewDuringAnimation(view: View) {
    /**
     * This adapter class provides empty implementations of the methods from
     * Animator.AnimatorListener. Any custom listener that cares only about a subset of the
     * methods of this listener can simply subclass this adapter class instead of implementing
     * the interface directly.
     */
    addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator?) {
            view.isEnabled = false
        }

        override fun onAnimationEnd(animation: Animator?) {
            view.isEnabled = true
        }
    })
}

fun ObjectAnimator.runAnimationWithDefaultProperties(
    view: View,
    customRepeatCount: Int = 1,
    customRepeatMode: Int = ObjectAnimator.REVERSE,
    animatorDuration: AnimatorDuration = AnimatorDuration.DEFAULT
) {
    disableViewDuringAnimation(view)
    duration = animatorDuration.timeInMillis
    repeatCount = customRepeatCount
    repeatMode = customRepeatMode
    start()
}

enum class AnimatorDuration(val timeInMillis: Long) {
    DEFAULT(300),
    MEDIUM(500),
    LONG(1000),
    EXTRA(2000)
}