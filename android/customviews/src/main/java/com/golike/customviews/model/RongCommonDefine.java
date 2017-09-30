package com.golike.customviews.model;

/**
 * Created by admin on 2017/9/6.
 */

public interface RongCommonDefine {
    public static enum GetMessageDirection {
        BEHIND(0),
        FRONT(1);

        int value;

        private GetMessageDirection(int v) {
            this.value = v;
        }

        int getValue() {
            return this.value;
        }
    }
}