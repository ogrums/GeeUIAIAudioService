package com.rhj.audio.ai

/**
 * Raw PCM capture so a cloud STT can receive audio without owning the microphone HAL.
 */
interface AudioCapture {
    fun start(listener: AudioFrameListener)
    fun stop()
}

interface AudioFrameListener {
    fun onPcm(samples: ByteArray, sampleRateHz: Int)
}
