package com.geeui.aiui.observer;

public interface WakeupResultCallback {
    /**
     * @param stateData avatar.silence : en attente de réveil
     *                  avatar.listening : écoute
     *                  avatar.understanding : compréhension
     *                  avatar.speaking : lecture vocale
     */
    void getResult(String result);
}
