package com.rhj.audio.ai

/**
 * On-device or cloud wake-word engine (AIUI IVW, DUI wakeup, etc.).
 */
interface WakeWordDetector {
    fun start()
    fun stop()
    fun setKeywords(keywords: List<String>)
    fun setListener(listener: WakeWordListener)
}

interface WakeWordListener {
    fun onWake(keyword: String, doaDegrees: Int? = null)
    fun onSleep()
}
