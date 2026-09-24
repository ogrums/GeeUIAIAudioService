package com.geeui.aiui.observer;

public interface WakeupStateChangeCallback {
    /**
     * @param stateData avatar.silence: waiting for wake-up
     *                  avatar.listening: listening
     *                  avatar.understanding: understanding
     *                  avatar.speaking: speaking
     */
    void onState(String stateData);
}
