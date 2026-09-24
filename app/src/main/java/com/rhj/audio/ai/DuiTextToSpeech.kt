package com.rhj.audio.ai

import com.rhj.audio.RhjAudioManager

/**
 * TTS adapter on top of the existing DUI / Speechocean engine.
 */
class DuiTextToSpeech : TextToSpeech {
    override fun speak(text: String, utteranceId: String?, voice: String?) {
        if (utteranceId.isNullOrEmpty()) {
            RhjAudioManager.getInstance().speak(text)
        } else {
            RhjAudioManager.getInstance().speak(text, utteranceId)
        }
    }

    override fun stop() {
        try {
            RhjAudioManager.getInstance().shutupTts()
        } catch (_: Exception) {
            // ignore if TTS engine is not ready
        }
    }
}
