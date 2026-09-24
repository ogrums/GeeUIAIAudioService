package com.rhj.audio.ai

import com.rhj.speech.model.AIModel
import okhttp3.HttpUrl.Builder as HttpUrlBuilder

/**
 * Builds an [AiProvider] from remote [AIModel] config.
 * Changing cloud is a config change (voiceType / baseUrl / keys), not a rewrite of LTPAudioService.
 */
object AiProviderFactory {

    fun createDefault(): AiProvider {
        return create(AiRuntimeConfig.from(null))
    }

    fun create(model: AIModel?): AiProvider {
        return create(AiRuntimeConfig.from(model))
    }

    fun create(config: AiRuntimeConfig): AiProvider {
        val llm = when (config.providerId) {
            AiRuntimeConfig.PROVIDER_OPENAI -> openAiLlm(config)
            AiRuntimeConfig.PROVIDER_SPARK -> sparkLlm(config)
            else -> sparkLlm(config)
        }
        return DefaultAiProvider(config = config, llm = llm)
    }

    private fun openAiLlm(config: AiRuntimeConfig): LlmChat {
        return SseQueryLlmChat(config) { builder: HttpUrlBuilder ->
            builder.addQueryParameter("openai_key", config.apiKey ?: "")
        }
    }

    private fun sparkLlm(config: AiRuntimeConfig): LlmChat {
        return SseQueryLlmChat(config) { builder: HttpUrlBuilder ->
            builder.addQueryParameter("ts", System.currentTimeMillis().toString())
            builder.addQueryParameter("aide", config.voiceType ?: "")
        }
    }
}
