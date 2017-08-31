package com.golike.customviews.photoview.gestures;

/**
 * Created by admin on 2017/8/31.
 */

import android.content.Context;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

import com.golike.customviews.photoview.log.LogManager;

public class CupcakeGestureDetector implements GestureDetector {
    protected OnGestureListener mListener;
    private static final String LOG_TAG = "CupcakeGestureDetector";
    float mLastTouchX;
    float mLastTouchY;
    final float mTouchSlop;
    final float mMinimumVelocity;
    private VelocityTracker mVelocityTracker;
    private boolean mIsDragging;

    public void setOnGestureListener(OnGestureListener listener) {
        this.mListener = listener;
    }

    public CupcakeGestureDetector(Context context) {
        ViewConfiguration configuration = ViewConfiguration.get(context);
        this.mMinimumVelocity = (float)configuration.getScaledMinimumFlingVelocity();
        this.mTouchSlop = (float)configuration.getScaledTouchSlop();
    }

    float getActiveX(MotionEvent ev) {
        return ev.getX();
    }

    float getActiveY(MotionEvent ev) {
        return ev.getY();
    }

    public boolean isScaling() {
        return false;
    }

    public boolean isDragging() {
        return this.mIsDragging;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        float vX;
        float vY;
        switch(ev.getAction()) {
            case 0:
                this.mVelocityTracker = VelocityTracker.obtain();
                if(null != this.mVelocityTracker) {
                    this.mVelocityTracker.addMovement(ev);
                } else {
                    LogManager.getLogger().i("CupcakeGestureDetector", "Velocity tracker is null");
                }

                this.mLastTouchX = this.getActiveX(ev);
                this.mLastTouchY = this.getActiveY(ev);
                this.mIsDragging = false;
                break;
            case 1:
                if(this.mIsDragging && null != this.mVelocityTracker) {
                    this.mLastTouchX = this.getActiveX(ev);
                    this.mLastTouchY = this.getActiveY(ev);
                    this.mVelocityTracker.addMovement(ev);
                    this.mVelocityTracker.computeCurrentVelocity(1000);
                    vX = this.mVelocityTracker.getXVelocity();
                    vY = this.mVelocityTracker.getYVelocity();
                    if(Math.max(Math.abs(vX), Math.abs(vY)) >= this.mMinimumVelocity) {
                        this.mListener.onFling(this.mLastTouchX, this.mLastTouchY, -vX, -vY);
                    }
                }

                if(null != this.mVelocityTracker) {
                    this.mVelocityTracker.recycle();
                    this.mVelocityTracker = null;
                }
                break;
            case 2:
                vX = this.getActiveX(ev);
                vY = this.getActiveY(ev);
                float dx = vX - this.mLastTouchX;
                float dy = vY - this.mLastTouchY;
                if(!this.mIsDragging) {
                    this.mIsDragging = Math.sqrt((double)(dx * dx + dy * dy)) >= (double)this.mTouchSlop;
                }

                if(this.mIsDragging) {
                    this.mListener.onDrag(dx, dy);
                    this.mLastTouchX = vX;
                    this.mLastTouchY = vY;
                    if(null != this.mVelocityTracker) {
                        this.mVelocityTracker.addMovement(ev);
                    }
                }
                break;
            case 3:
                if(null != this.mVelocityTracker) {
                    this.mVelocityTracker.recycle();
                    this.mVelocityTracker = null;
                }
        }

        return true;
    }
}
