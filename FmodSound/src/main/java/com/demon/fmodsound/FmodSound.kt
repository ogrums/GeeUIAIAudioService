package com.demon.fmodsound

import java.lang.Exception

/**
 * @author DeMon
 * Created on 2020/12/31.
 * E-mail 757454343@qq.com
 * Desc:
 */

object FmodSound {
    //Type d'effet sonore

    init {
        System.loadLibrary("fmodL")
        System.loadLibrary("fmod")
        System.loadLibrary("FmodSound")
    }

    external fun playTts(url: String, listener: IPlaySoundListener)
    interface IPlaySoundListener {
        //Succès
        fun onFinish()
    }

}