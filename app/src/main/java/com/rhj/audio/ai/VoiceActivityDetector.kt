package com.rhj.audio.ai

/**
 * Voice activity detection (speech start / end).
 */
interface VoiceActivityDetector {
    fun start()
    fun stop()
    fun setListener(listener: VadListener)
}

interface VadListener {
    fun onSpeechBegin()
    fun onSpeechEnd()
    fun onTimeout()
}
