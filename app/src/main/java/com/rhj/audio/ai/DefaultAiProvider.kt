package com.rhj.audio.ai

/**
 * Default composite provider: chosen LLM + DUI TTS + no-op capture / wake-word.
 */
class DefaultAiProvider(
    override val config: AiRuntimeConfig,
    override val llm: LlmChat,
    override val stt: SpeechToText = NoOpSpeechToText(),
    override val tts: TextToSpeech = DuiTextToSpeech(),
    override val wakeWord: WakeWordDetector = NoOpWakeWordDetector(),
    override val vad: VoiceActivityDetector = NoOpVad(),
    override val nlu: NluEngine = NoOpNluEngine(),
    override val skills: SkillRouter = NoOpSkillRouter(),
    override val capture: AudioCapture = NoOpAudioCapture()
) : AiProvider {
    override val id: String = config.providerId
}
