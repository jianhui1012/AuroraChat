package com.golike.customviews.photoview.gestures;

import android.view.MotionEvent;

/**
 * Created by admin on 2017/8/31.
 */

public interface GestureDetector {
    boolean onTouchEvent(MotionEvent var1);

    boolean isScaling();

    boolean isDragging();

    void setOnGestureListener(OnGestureListener var1);
}