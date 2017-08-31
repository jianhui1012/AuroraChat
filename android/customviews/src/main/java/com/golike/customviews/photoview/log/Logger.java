package com.golike.customviews.photoview.log;

/**
 * Created by admin on 2017/8/31.
 */


public interface Logger {
    int v(String var1, String var2);

    int v(String var1, String var2, Throwable var3);

    int d(String var1, String var2);

    int d(String var1, String var2, Throwable var3);

    int i(String var1, String var2);

    int i(String var1, String var2, Throwable var3);

    int w(String var1, String var2);

    int w(String var1, String var2, Throwable var3);

    int e(String var1, String var2);

    int e(String var1, String var2, Throwable var3);
}
