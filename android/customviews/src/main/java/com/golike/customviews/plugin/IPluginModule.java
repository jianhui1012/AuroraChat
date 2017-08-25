package com.golike.customviews.plugin;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;

import com.golike.customviews.EditExtension;

/**
 * Created by admin on 2017/8/8.
 */

public interface IPluginModule {
    Drawable obtainDrawable(Context var1);

    String obtainTitle(Context var1);

    void onClick(Fragment var1, EditExtension var2);

    void onActivityResult(int var1, int var2, Intent var3);
}
