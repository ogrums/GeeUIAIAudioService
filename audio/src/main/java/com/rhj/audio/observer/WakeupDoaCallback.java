package com.rhj.audio.observer;

/**
 * Wake-up DOA callback
 * {"doa":355,"wakeupWord":"嗨，小乐","wakeupType":"major"}
 */
public interface WakeupDoaCallback {
    void onDoa(String doaData);
}
