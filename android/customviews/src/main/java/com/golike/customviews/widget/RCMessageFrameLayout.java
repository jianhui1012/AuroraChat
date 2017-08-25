package com.golike.customviews.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by admin on 2017/8/24.
 */

public class RCMessageFrameLayout extends FrameLayout {
    private Drawable mOldDrawable;

    public RCMessageFrameLayout(Context context) {
        super(context);
    }

    public RCMessageFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RCMessageFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);
        this.mOldDrawable = this.getBackground();
        this.setBackgroundDrawable((Drawable)null);
        this.setPadding(0, 0, 0, 0);
    }

    public Drawable getBackgroundDrawable() {
        return this.mOldDrawable;
    }
}
