package com.rhj.audio.ai

/**
 * Facade over one cloud (or local) AI stack.
 * Swap STT / LLM / TTS together by changing the factory output, not LTPAudioService.
 */
interface AiProvider {
    val id: String
    val config: AiRuntimeConfig
    val stt: SpeechToText
    val llm: LlmChat
    val tts: TextToSpeech
}
