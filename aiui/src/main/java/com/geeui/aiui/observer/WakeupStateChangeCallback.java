package com.geeui.aiui.observer;

public interface WakeupStateChangeCallback {
    /**
     * @param stateData avatar.silence : en attente de réveil
     *                  avatar.listening : écoute
     *                  avatar.understanding : compréhension
     *                  avatar.speaking : lecture vocale
     */
    void onState(String stateData);
}
