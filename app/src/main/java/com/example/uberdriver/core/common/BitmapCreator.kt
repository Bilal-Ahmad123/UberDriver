package com.example.uberdriver.core.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

object BitmapCreator {
     fun bitmapDescriptorFromVector(vectorResId: Int,context: Context, width:Double = 0.24, height:Double = 0.24): BitmapDescriptor? {
        val drawable: Drawable? = ContextCompat.getDrawable(context, vectorResId)
        drawable?.let {
            val originalWidth = it.intrinsicWidth
            val originalHeight = it.intrinsicHeight

            val scaledWidth = (originalWidth * width).toInt()
            val scaledHeight = (originalHeight * height).toInt()

            val bitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            it.setBounds(0, 0, canvas.width, canvas.height)
            it.draw(canvas)

            return BitmapDescriptorFactory.fromBitmap(bitmap)
        }
        return null
    }

}