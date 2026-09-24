package com.rhj.audio.ai

/**
 * Placeholder STT. Wake-up and ASR stay in AIUI / DUI until a cloud STT is wired.
 */
class NoOpSpeechToText : SpeechToText {
    override fun start(onPartial: (String) -> Unit, onFinal: (String) -> Unit) {
        // no-op: existing RhjAudioManager / AIUIAudioManager own the microphone
    }

    override fun stop() {
        // no-op
    }
}
