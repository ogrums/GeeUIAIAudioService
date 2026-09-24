package com.rhj.audio.ai

import android.os.Build
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.sse.RealEventSource
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import java.util.concurrent.TimeUnit

/**
 * Generic GET + SSE LLM client used by both the OpenAI proxy and the Spark / WeChat proxy.
 * Query parameter names stay compatible with the current backend.
 */
class SseQueryLlmChat(
    private val config: AiRuntimeConfig,
    private val extraQuery: (HttpUrl.Builder) -> Unit = {}
) : LlmChat {

    override fun stream(prompt: String, sessionId: String, listener: TokenListener) {
        val parsed = config.baseUrl.toHttpUrlOrNull()
            ?: throw IllegalArgumentException("Invalid LLM baseUrl: ${config.baseUrl}")
        val urlBuilder = parsed.newBuilder()
        extraQuery(urlBuilder)
        urlBuilder.addQueryParameter("q", prompt)
        urlBuilder.addQueryParameter("sn", Build.getSerial())
        urlBuilder.addQueryParameter("country", "cn")
        urlBuilder.addQueryParameter("key", "")
        if (!config.voiceType.isNullOrEmpty()) {
            urlBuilder.addQueryParameter("voice_type", config.voiceType)
        }

        val request = Request.Builder().url(urlBuilder.build()).build()
        val client = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .build()

        val callback = object : EventSourceListener() {
            override fun onOpen(eventSource: EventSource, response: Response) {
                listener.onOpen(sessionId)
            }

            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                listener.onToken(sessionId, data, type)
            }

            override fun onClosed(eventSource: EventSource) {
                listener.onComplete(sessionId)
            }

            override fun onFailure(
                eventSource: EventSource,
                t: Throwable?,
                response: Response?
            ) {
                listener.onError(sessionId, t, response?.message)
            }
        }
        RealEventSource(request, callback).connect(client)
    }
}
