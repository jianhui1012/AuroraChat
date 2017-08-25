package com.golike.customviews;

import android.net.Uri;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.golike.customviews.plugin.IPluginModule;

import java.util.List;

/**
 * Created by admin on 2017/8/8.
 */

public interface IExtensionClickListener extends TextWatcher {
    void onSendToggleClick(View var1, String var2);

    void onImageResult(List<Uri> var1, boolean var2);

    void onLocationResult(double var1, double var3, String var5, Uri var6);

    void onSwitchToggleClick(View var1, ViewGroup var2);

    void onVoiceInputToggleTouch(View var1, MotionEvent var2);

    void onEmoticonToggleClick(View var1, ViewGroup var2);

    void onPluginToggleClick(View var1, ViewGroup var2);

    void onMenuClick(int var1, int var2);

    void onEditTextClick(EditText var1);

    boolean onKey(View var1, int var2, KeyEvent var3);

    void onExtensionCollapsed();

    void onExtensionExpanded(int var1);

    void onPluginClicked(IPluginModule var1, int var2);
}
