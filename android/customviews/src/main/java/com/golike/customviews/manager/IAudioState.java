package com.golike.customviews.manager;

/**
 * Created by admin on 2017/8/24.
 */

public abstract class IAudioState {
    public IAudioState() {
    }

    void enter() {
    }

    abstract void handleMessage(AudioStateMessage var1);
}