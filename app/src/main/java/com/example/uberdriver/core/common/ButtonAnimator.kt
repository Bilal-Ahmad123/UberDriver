package com.example.uberdriver.core.common

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import androidx.core.animation.addListener

object ButtonAnimator {
   private var animatorSet:AnimatorSet? = null
   private var buttonRippleAnimatorSet:AnimatorSet? = null
   fun startRippleAnimation(view:View) {
        val rippleView = view

        val scaleX = ObjectAnimator.ofFloat(rippleView, View.SCALE_X, 1f, 1.5f)
        val scaleY = ObjectAnimator.ofFloat(rippleView, View.SCALE_Y, 1f, 1.5f)
        val alpha = ObjectAnimator.ofFloat(rippleView, View.ALPHA, 1f, 0f)

        scaleX.repeatCount = ObjectAnimator.INFINITE
        scaleY.repeatCount = ObjectAnimator.INFINITE
        alpha.repeatCount = ObjectAnimator.INFINITE

        scaleX.duration = 1000
        scaleY.duration = 1000
        alpha.duration = 1000

       buttonRippleAnimatorSet = AnimatorSet()
       buttonRippleAnimatorSet?.playTogether(scaleX, scaleY, alpha)
       buttonRippleAnimatorSet?.start()
    }

   fun startHorizontalAnimation(view: View, context: Context) {
      view.post {
         val screenWidth = context.resources.displayMetrics.widthPixels.toFloat()
         val maxTranslationX = screenWidth - view.width

         val translateXPositive = ObjectAnimator.ofFloat(view, "translationX", 0f, maxTranslationX).apply {
            duration = 1000
         }

         val translateXNegative = ObjectAnimator.ofFloat(view, "translationX", maxTranslationX, 0f).apply {
            duration = 1000
         }

         animatorSet = AnimatorSet().apply {
            playSequentially(translateXPositive, translateXNegative)
            addListener(object : AnimatorListenerAdapter() {
               override fun onAnimationEnd(animation: Animator) {
                  start()
               }
            })
         }

         animatorSet?.start()
      }
   }

   fun stopAnimation(){
      animatorSet?.cancel()
   }

    fun stopRippleAnimation(){
        buttonRippleAnimatorSet?.cancel()
    }


}