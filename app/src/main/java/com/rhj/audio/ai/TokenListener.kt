package com.rhj.audio.ai

/**
 * Streaming callbacks for an LLM turn.
 * [sessionId] lets the caller drop stale responses after a new request starts.
 */
interface TokenListener {
    fun onOpen(sessionId: String) {}

    fun onToken(sessionId: String, token: String, eventType: String?)

    fun onComplete(sessionId: String)

    fun onError(sessionId: String, error: Throwable?, message: String?)
}
