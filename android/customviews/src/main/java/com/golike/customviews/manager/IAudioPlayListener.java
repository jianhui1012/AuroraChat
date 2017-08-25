package com.golike.customviews.manager;

import android.net.Uri;

/**
 * Created by admin on 2017/8/24.
 */

public interface IAudioPlayListener {
    void onStart(Uri var1);

    void onStop(Uri var1);

    void onComplete(Uri var1);
}