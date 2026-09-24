package com.rhj.audio.ai

/**
 * One user turn: local skill first, then the cloud LLM if no skill claims the utterance.
 */
interface TurnController {
    fun handleUtterance(text: String, sessionId: String, listener: TokenListener)
}
