package com.rhj.audio.ai

/**
 * Text-to-speech playback used after an LLM (or skill) response.
 */
interface TextToSpeech {
    fun speak(text: String, utteranceId: String? = null, voice: String? = null)

    fun stop()
}
