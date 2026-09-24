package com.rhj.audio.observer;

/**
 * Callback d'angle de réveil (DOA)
 * {"doa":355,"wakeupWord":"嗨，小乐","wakeupType":"major"}
 */
public interface WakeupDoaCallback {
    void onDoa(String doaData);
}
