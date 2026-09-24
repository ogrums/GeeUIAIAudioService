package com.rhj.audio.ai

/**
 * High-level robot dialogue events (wake, listen, think, speak).
 * Maps to DUI avatar states: silence / listening / understanding / speaking.
 */
interface AiEventListener {
    fun onState(state: AiDialogueState)
    fun onAsrPartial(text: String) {}
    fun onAsrFinal(text: String) {}
    fun onTtsBegin(utteranceId: String?) {}
    fun onTtsEnd(utteranceId: String?) {}
}

enum class AiDialogueState {
    IDLE,
    LISTENING,
    UNDERSTANDING,
    SPEAKING,
    STANDBY
}
