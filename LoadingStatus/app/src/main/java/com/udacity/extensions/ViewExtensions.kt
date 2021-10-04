package com.udacity.extensions

import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import com.udacity.R

fun View.showWithFadeIn() {
    isVisible = true
    this.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
}

fun View.hideWithFadeOut() {
    this.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out))
    isVisible = false
}