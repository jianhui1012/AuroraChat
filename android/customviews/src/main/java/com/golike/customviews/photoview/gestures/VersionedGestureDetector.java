package com.golike.customviews.photoview.gestures;

import android.content.Context;
import android.os.Build;

/**
 * Created by admin on 2017/8/31.
 */

public final class VersionedGestureDetector {
    public VersionedGestureDetector() {
    }

    public static GestureDetector newInstance(Context context, OnGestureListener listener) {
        int sdkVersion = Build.VERSION.SDK_INT;
        Object detector;
        if(sdkVersion < 5) {
            detector = new CupcakeGestureDetector(context);
        } else if(sdkVersion < 8) {
            detector = new EclairGestureDetector(context);
        } else {
            detector = new FroyoGestureDetector(context);
        }

        ((GestureDetector)detector).setOnGestureListener(listener);
        return (GestureDetector)detector;
    }
}