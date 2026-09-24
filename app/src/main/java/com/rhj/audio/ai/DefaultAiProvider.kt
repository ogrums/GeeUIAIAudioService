package com.rhj.audio.ai

/**
 * Default composite provider: chosen LLM + DUI TTS + no-op STT.
 */
class DefaultAiProvider(
    override val config: AiRuntimeConfig,
    override val llm: LlmChat,
    override val stt: SpeechToText = NoOpSpeechToText(),
    override val tts: TextToSpeech = DuiTextToSpeech()
) : AiProvider {
    override val id: String = config.providerId
}
