package com.golike.customviews.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by admin on 2017/8/16.
 */

public class AutoLinkTextView extends TextView {
    public AutoLinkTextView(Context context) {
        super(context);
    }

    public AutoLinkTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoLinkTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public AutoLinkTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setText(CharSequence text, BufferType type) {
        super.setAutoLinkMask(0);
        super.setText(text, type);
        //RongLinkify.addLinks(this, 15);
    }
}
