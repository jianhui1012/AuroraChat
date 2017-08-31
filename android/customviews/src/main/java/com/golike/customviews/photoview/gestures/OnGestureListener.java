package com.golike.customviews.photoview.gestures;

/**
 * Created by admin on 2017/8/31.
 */

public interface OnGestureListener {
    void onDrag(float var1, float var2);

    void onFling(float var1, float var2, float var3, float var4);

    void onScale(float var1, float var2, float var3);
}