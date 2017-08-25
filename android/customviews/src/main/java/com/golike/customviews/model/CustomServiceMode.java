package com.golike.customviews.model;

/**
 * Created by admin on 2017/8/9.
 */

public enum CustomServiceMode {
    CUSTOM_SERVICE_MODE_NO_SERVICE(0),
    CUSTOM_SERVICE_MODE_ROBOT(1),
    CUSTOM_SERVICE_MODE_HUMAN(2),
    CUSTOM_SERVICE_MODE_ROBOT_FIRST(3),
    CUSTOM_SERVICE_MODE_HUMAN_FIRST(4);

    private int mode;

    private CustomServiceMode(int mode) {
        this.mode = mode;
    }

    public static CustomServiceMode valueOf(int mode) {
        CustomServiceMode[] arr$ = values();
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            CustomServiceMode m = arr$[i$];
            if(m.mode == mode) {
                return m;
            }
        }

        return CUSTOM_SERVICE_MODE_ROBOT;
    }

    public int getValue() {
        return this.mode;
    }
}

