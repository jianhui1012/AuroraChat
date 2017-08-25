package com.golike.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by admin on 2017/8/10.
 */

public class SquareLayout extends RelativeLayout {
    public SquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(this.getMeasuredWidth(), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}