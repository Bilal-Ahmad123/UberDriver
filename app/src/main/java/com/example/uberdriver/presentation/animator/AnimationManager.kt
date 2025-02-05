package com.example.uberdriver.presentation.animator

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.core.animation.doOnEnd

object AnimationManager {
    inline fun translateToRight(view:View, time:Long, crossinline dispatch:(Boolean)-> Unit){
        val animator = ObjectAnimator.ofFloat(view,"translationX",0f,1000f)
        animator.interpolator = AccelerateInterpolator(3f)
        animator.duration = time
        animator.start()
        animator.doOnEnd {
            dispatch(true)
        }
    }
}