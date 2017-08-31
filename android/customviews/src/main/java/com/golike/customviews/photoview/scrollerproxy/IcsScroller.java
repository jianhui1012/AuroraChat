package com.golike.customviews.photoview.scrollerproxy;

import android.annotation.TargetApi;
import android.content.Context;

/**
 * Created by admin on 2017/8/31.
 */

@TargetApi(14)
public class IcsScroller extends GingerScroller {
    public IcsScroller(Context context) {
        super(context);
    }

    public boolean computeScrollOffset() {
        return this.mScroller.computeScrollOffset();
    }
}

