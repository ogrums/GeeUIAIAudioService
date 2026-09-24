package com.rhj.audio.ai

/**
 * Routes a parsed [NluResult] to a local skill instead of the cloud LLM.
 */
interface SkillRouter {
    fun canHandle(result: NluResult): Boolean
    fun handle(result: NluResult): Boolean
}

interface SkillHandler {
    val skillId: String
    fun handle(result: NluResult): Boolean
}
