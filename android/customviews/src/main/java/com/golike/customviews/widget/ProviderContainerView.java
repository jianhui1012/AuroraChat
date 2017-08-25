package com.golike.customviews.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.golike.customviews.widget.provider.IContainerItemProvider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by admin on 2017/8/15.
 */

public class ProviderContainerView extends FrameLayout {
    Map<Class<? extends IContainerItemProvider>, AtomicInteger> mViewCounterMap;
    Map<Class<? extends IContainerItemProvider>, View> mContentViewMap;
    View mInflateView;
    int mMaxContainSize = 3;

    public ProviderContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!this.isInEditMode()) {
            this.init(attrs);
        }

    }

    private void init(AttributeSet attrs) {
        this.mViewCounterMap = new HashMap();
        this.mContentViewMap = new HashMap();
    }

    public <T extends IContainerItemProvider> View inflate(T t) {
        View result = null;
        if(this.mInflateView != null) {
            this.mInflateView.setVisibility(GONE);
        }

        if(this.mContentViewMap.containsKey(t.getClass())) {
            result = (View)this.mContentViewMap.get(t.getClass());
            this.mInflateView = result;
            ((AtomicInteger)this.mViewCounterMap.get(t.getClass())).incrementAndGet();
        }

        if(result != null) {
            if(result.getVisibility() == GONE) {
                result.setVisibility(VISIBLE);
            }

            return result;
        } else {
            this.recycle();
            result = t.newView(this.getContext(), this);
            if(result != null) {
                super.addView(result);
                this.mContentViewMap.put(t.getClass(), result);
                this.mViewCounterMap.put(t.getClass(), new AtomicInteger());
            }

            this.mInflateView = result;
            return result;
        }
    }

    public View getCurrentInflateView() {
        return this.mInflateView;
    }

    public void containerViewLeft() {
        if(this.mInflateView != null) {
            LayoutParams params = (LayoutParams)this.mInflateView.getLayoutParams();
            params.gravity = 19;
        }
    }

    public void containerViewRight() {
        if(this.mInflateView != null) {
            LayoutParams params = (LayoutParams)this.mInflateView.getLayoutParams();
            params.gravity = 21;
        }
    }

    public void containerViewCenter() {
        if(this.mInflateView != null) {
            LayoutParams params = (LayoutParams)this.mInflateView.getLayoutParams();
            params.gravity = 17;
        }
    }

    private void recycle() {
        if(this.mInflateView != null) {
            int count = this.getChildCount();
            if(count >= this.mMaxContainSize) {
                Map.Entry min = null;

                Map.Entry item;
                for(Iterator view = this.mViewCounterMap.entrySet().iterator(); view.hasNext(); min = ((AtomicInteger)min.getValue()).get() > ((AtomicInteger)item.getValue()).get()?item:min) {
                    item = (Map.Entry)view.next();
                    if(min == null) {
                        min = item;
                    }
                }

                this.mViewCounterMap.remove(min.getKey());
                View view1 = (View)this.mContentViewMap.remove(min.getKey());
                this.removeView(view1);
            }

        }
    }
}
