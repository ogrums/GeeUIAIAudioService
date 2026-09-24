package com.rhj.audio.ai

/**
 * Natural-language understanding: intent + slots from an utterance.
 * Used for on-robot skills (volume, music, alarm) that must not go to the LLM.
 */
interface NluEngine {
    fun parse(utterance: String): NluResult
}

data class NluResult(
    val intent: String?,
    val skillId: String? = null,
    val skillName: String? = null,
    val slots: Map<String, String> = emptyMap(),
    val raw: String? = null
)
