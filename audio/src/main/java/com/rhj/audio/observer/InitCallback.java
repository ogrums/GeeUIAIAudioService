package com.rhj.audio.observer;

/**
 * Listener de changement d'état d'init
 */
public interface InitCallback {
    void stateChange(boolean initStatus);
}
