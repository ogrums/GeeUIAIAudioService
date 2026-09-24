package com.rhj.audio.ai

/**
 * One multi-turn conversation with a single LLM / NLU backend.
 */
interface DialogueSession {
    val sessionId: String
    fun sendUserText(text: String, listener: TokenListener)
    fun interrupt()
    fun end()
}
