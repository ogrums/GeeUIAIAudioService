package com.rhj.audio.ai

/**
 * Default turn routing: NLU + skill router, otherwise stream the prompt to the LLM.
 */
class DefaultTurnController(
    private val nlu: NluEngine,
    private val skills: SkillRouter,
    private val llm: LlmChat
) : TurnController {

    override fun handleUtterance(text: String, sessionId: String, listener: TokenListener) {
        val result = nlu.parse(text)
        if (skills.canHandle(result) && skills.handle(result)) {
            listener.onComplete(sessionId)
            return
        }
        llm.stream(text, sessionId, listener)
    }
}
