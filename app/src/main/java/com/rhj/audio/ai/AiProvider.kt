package com.rhj.audio.ai

/**
 * Facade over one cloud (or local) AI stack.
 * Swap STT, LLM, TTS, and wake-word by changing the factory output, not LTPAudioService.
 */
interface AiProvider {
    val id: String
    val config: AiRuntimeConfig
    val stt: SpeechToText
    val llm: LlmChat
    val tts: TextToSpeech
    val wakeWord: WakeWordDetector
    val vad: VoiceActivityDetector
    val nlu: NluEngine
    val skills: SkillRouter
    val capture: AudioCapture
    val turns: TurnController
}
