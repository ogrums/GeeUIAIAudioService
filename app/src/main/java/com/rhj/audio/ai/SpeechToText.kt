package com.rhj.audio.ai

/**
 * Cloud or on-device speech-to-text.
 * Wake-word detection stays in AIUI / DUI; this port is for ASR after wake-up.
 */
interface SpeechToText {
    fun start(onPartial: (String) -> Unit, onFinal: (String) -> Unit)

    fun stop()

    fun cancel() {
        stop()
    }
}
