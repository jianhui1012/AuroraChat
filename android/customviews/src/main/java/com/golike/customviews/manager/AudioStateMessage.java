package com.golike.customviews.manager;

/**
 * Created by admin on 2017/8/24.
 */

public class AudioStateMessage {
    public int what;
    public Object obj;

    public AudioStateMessage() {
    }

    public static AudioStateMessage obtain() {
        return new AudioStateMessage();
    }
}