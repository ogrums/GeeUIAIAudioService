package com.geeui.aiui.observer;

public interface WakeupResultCallback {
    /**
     * @param stateData avatar.silence: waiting for wake-up
     *                  avatar.listening: listening
     *                  avatar.understanding: understanding
     *                  avatar.speaking: speaking
     */
    void getResult(String result);
}
