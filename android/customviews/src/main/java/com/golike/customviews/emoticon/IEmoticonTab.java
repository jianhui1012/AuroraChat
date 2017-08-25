package com.golike.customviews.emoticon;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by admin on 2017/8/8.
 */

public interface IEmoticonTab {
    Drawable obtainTabDrawable(Context var1);

    View obtainTabPager(Context var1);

    void onTableSelected(int var1);
}
