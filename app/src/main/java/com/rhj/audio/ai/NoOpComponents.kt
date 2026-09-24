package com.rhj.audio.ai

class NoOpWakeWordDetector : WakeWordDetector {
    override fun start() {}
    override fun stop() {}
    override fun setKeywords(keywords: List<String>) {}
    override fun setListener(listener: WakeWordListener) {}
}

class NoOpVad : VoiceActivityDetector {
    override fun start() {}
    override fun stop() {}
    override fun setListener(listener: VadListener) {}
}

class NoOpNluEngine : NluEngine {
    override fun parse(utterance: String): NluResult {
        return NluResult(intent = null, raw = utterance)
    }
}

class NoOpSkillRouter : SkillRouter {
    override fun canHandle(result: NluResult): Boolean = false
    override fun handle(result: NluResult): Boolean = false
}

class NoOpAudioCapture : AudioCapture {
    override fun start(listener: AudioFrameListener) {}
    override fun stop() {}
}
