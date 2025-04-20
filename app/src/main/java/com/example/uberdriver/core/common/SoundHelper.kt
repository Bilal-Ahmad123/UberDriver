package com.example.uberdriver.core.common

import android.content.Context
import android.media.MediaPlayer

object SoundHelper {
    var mediaPlayer : MediaPlayer ? = null
    fun startSound(context: Context,sound:Int){
        mediaPlayer = MediaPlayer.create(context,sound)
        mediaPlayer?.start()
    }

    fun resumeSound() {
        if(mediaPlayer != null) mediaPlayer?.start()
    }

    fun destroySoundInstance(){
        mediaPlayer = null
    }
}