package com.rhj.audio.ai

/**
 * Streaming chat completion. Implementations must not block the caller
 * for the full answer; they push tokens through [TokenListener].
 */
interface LlmChat {
    fun stream(prompt: String, sessionId: String, listener: TokenListener)
}
