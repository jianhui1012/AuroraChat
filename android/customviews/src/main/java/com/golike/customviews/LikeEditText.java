package com.golike.customviews;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;


/**
 * Created by admin on 2017/7/26.
 */

public class LikeEditText extends EditText {

    private Context mContext;
    private Drawable delete_icon;

    public LikeEditText(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public LikeEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public LikeEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public void init() {
        delete_icon = mContext.getDrawable(R.drawable.search_clear_pressed_write);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (length() > 0)
                    setCompoundDrawablesWithIntrinsicBounds(null, null, delete_icon, null);
                else
                    setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取右边位置图片
        Drawable drawable = getCompoundDrawables()[2];
        if (drawable != null && event.getAction() == MotionEvent.ACTION_UP) {
            int eventX = (int) event.getRawX();
            int eventY = (int) event.getRawY();
            Rect rect = new Rect();
            getGlobalVisibleRect(rect);
            rect.left = rect.right - 50;
            if(rect.contains(eventX, eventY))
                setText("");
        }
        return super.onTouchEvent(event);
    }

}
