package com.rhj.audio.ai

import com.rhj.speech.model.AIModel

/**
 * Loads remote LLM credentials. Replaces direct calls to Api.getAiConfig inside LTPAudioService.
 */
interface AiConfigRepository {
    suspend fun loadActiveConfig(serialNumber: String): AiRuntimeConfig?

    suspend fun loadModels(serialNumber: String): List<AIModel>
}
