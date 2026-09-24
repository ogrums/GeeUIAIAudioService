package com.rhj.audio.ai

/**
 * TTS playback callbacks. Maps to the existing DUI / AIUI speak begin, progress, and end events.
 */
interface TtsPlaybackListener {
    fun onSpeakBegin(utteranceId: String?)

    fun onSpeakProgress(utteranceId: String?, current: Int, total: Int) {}

    fun onSpeakEnd(utteranceId: String?, errorCode: Int)

    fun onError(utteranceId: String?, message: String?) {}
}
