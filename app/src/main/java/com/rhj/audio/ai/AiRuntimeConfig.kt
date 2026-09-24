package com.rhj.audio.ai

import com.rhj.speech.model.AIModel

/**
 * Runtime credentials and routing for one AI backend.
 *
 * [providerId] values:
 * - spark: iFlytek Spark / WeChat proxy (voiceType xh, wx)
 * - openai: OpenAI-compatible proxy (voiceType other, other4, sysGpt)
 * - iflytek: reserved for a future full AIUI stack swap
 */
data class AiRuntimeConfig(
    val providerId: String,
    val baseUrl: String,
    val apiKey: String? = null,
    val apiSecret: String? = null,
    val appId: String? = null,
    val model: String? = null,
    val voiceType: String? = null,
    val extra: Map<String, String> = emptyMap()
) {
    companion object {
        const val PROVIDER_SPARK = "spark"
        const val PROVIDER_OPENAI = "openai"
        const val PROVIDER_IFLYTEK = "iflytek"

        const val DEFAULT_SPARK_URL = "https://yourdomain.com/apipath"
        const val DEFAULT_OPENAI_URL = "http://yourdomain/apipath"

        /**
         * Map backend [AIModel.voiceType] onto a provider id.
         * Same rules as the previous when (voiceType) in LTPAudioService.
         */
        fun providerIdFor(voiceType: String?): String {
            return when (voiceType) {
                "other", "other4", "sysGpt" -> PROVIDER_OPENAI
                "xh", "wx" -> PROVIDER_SPARK
                else -> PROVIDER_SPARK
            }
        }

        fun from(model: AIModel?): AiRuntimeConfig {
            val voice = model?.voiceType
            val id = providerIdFor(voice)
            val url = if (id == PROVIDER_OPENAI) DEFAULT_OPENAI_URL else DEFAULT_SPARK_URL
            return AiRuntimeConfig(
                providerId = id,
                baseUrl = url,
                apiKey = model?.apiKey,
                apiSecret = model?.apiSecret,
                appId = model?.appId,
                voiceType = voice
            )
        }
    }
}
