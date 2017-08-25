package com.golike.customviews.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.golike.customviews.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by admin on 2017/8/8.
 */

public class RongUtils {
    private static final String TAG = "ScreenUtil";
    private static double RATIO = 0.85D;
    public static int screenWidth;
    public static int screenHeight;
    public static int screenMin;
    public static int screenMax;
    public static float density;
    public static float scaleDensity;
    public static float xdpi;
    public static float ydpi;
    public static int densityDpi;
    public static int dialogWidth;
    public static int statusbarheight;
    public static int navbarheight;

    public RongUtils() {
    }

    public static int dip2px(float dipValue) {
        return (int)(dipValue * density + 0.5F);
    }

    public static int px2dip(float pxValue) {
        return (int)(pxValue / density + 0.5F);
    }

    public static int getDialogWidth() {
        dialogWidth = (int)((double)screenMin * RATIO);
        return dialogWidth;
    }

    public static void init(Context context) {
        if(null != context) {
            DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
            screenWidth = dm.widthPixels;
            screenHeight = dm.heightPixels;
            screenMin = screenWidth > screenHeight?screenHeight:screenWidth;
            density = dm.density;
            scaleDensity = dm.scaledDensity;
            xdpi = dm.xdpi;
            ydpi = dm.ydpi;
            densityDpi = dm.densityDpi;
        }
    }

    public static void GetInfo(Context context) {
        if(null != context) {
            DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
            screenWidth = dm.widthPixels;
            screenHeight = dm.heightPixels;
            screenMin = screenWidth > screenHeight?screenHeight:screenWidth;
            screenMax = screenWidth < screenHeight?screenHeight:screenWidth;
            density = dm.density;
            scaleDensity = dm.scaledDensity;
            xdpi = dm.xdpi;
            ydpi = dm.ydpi;
            densityDpi = dm.densityDpi;
            statusbarheight = getStatusBarHeight(context);
            navbarheight = getNavBarHeight(context);
            Log.d("ScreenUtil", "screenWidth=" + screenWidth + " screenHeight=" + screenHeight + " density=" + density);
        }
    }

    public static int getStatusBarHeight(Context context) {
        Class c = null;
        Object obj = null;
        Field field = null;
        boolean x = false;
        int sbar = 0;

        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            int x1 = Integer.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x1);
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return sbar;
    }

    public static int getScreenWidth() {
        return screenWidth;
    }

    public static int getNavBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resourceId > 0?resources.getDimensionPixelSize(resourceId):0;
    }

    public static Drawable getDrawable(Context context, String resource) {
        InputStream is = null;

        try {
            Resources e = context.getResources();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDensity = 240;
            options.inScreenDensity = e.getDisplayMetrics().densityDpi;
            options.inTargetDensity = e.getDisplayMetrics().densityDpi;
            is = context.getAssets().open(resource);
            Bitmap bitmap = BitmapFactory.decodeStream(is, new Rect(), options);
            BitmapDrawable var6 = new BitmapDrawable(context.getResources(), bitmap);
            return var6;
        } catch (Exception var16) {
            var16.printStackTrace();
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException var15) {
                    var15.printStackTrace();
                }
            }

        }

        return null;
    }

    public static View getHorizontalThinLine(Context context) {
        View line = new View(context);
        line.setBackgroundColor(context.getResources().getColor(R.color.rc_divider_line));
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(-1, 1);
        line.setLayoutParams(llp);
        return line;
    }

    public static View getVerticalThinLine(Context context) {
        View line = new View(context);
        line.setBackgroundColor(context.getResources().getColor(R.color.rc_divider_line));
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(1, -1);
        line.setLayoutParams(llp);
        return line;
    }

    public static Bitmap getResizedBitmap(Context context, Uri uri, int widthLimit, int heightLimit) throws IOException {
        String path = null;
        Bitmap result = null;
        if(uri.getScheme().equals("file")) {
            path = uri.getPath();
        } else {
            if(!uri.getScheme().equals("content")) {
                return null;
            }

            Cursor exifInterface = context.getContentResolver().query(uri, new String[]{"_data"}, (String)null, (String[])null, (String)null);
            exifInterface.moveToFirst();
            path = exifInterface.getString(0);
            exifInterface.close();
        }

        ExifInterface exifInterface1 = new ExifInterface(path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int orientation = exifInterface1.getAttributeInt("Orientation", 0);
        int width;
        if(orientation == 6 || orientation == 8 || orientation == 5 || orientation == 7) {
            width = widthLimit;
            widthLimit = heightLimit;
            heightLimit = width;
        }

        width = options.outWidth;
        int height = options.outHeight;
        int sampleW = 1;

        int sampleH;
        for(sampleH = 1; width / 2 > widthLimit; sampleW <<= 1) {
            width /= 2;
        }

        while(height / 2 > heightLimit) {
            height /= 2;
            sampleH <<= 1;
        }

        boolean sampleSize = true;
        options = new BitmapFactory.Options();
        int sampleSize1;
        if(widthLimit != 2147483647 && heightLimit != 2147483647) {
            sampleSize1 = Math.max(sampleW, sampleH);
        } else {
            sampleSize1 = Math.max(sampleW, sampleH);
        }

        options.inSampleSize = sampleSize1;

        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeFile(path, options);
        } catch (OutOfMemoryError var22) {
            var22.printStackTrace();
            options.inSampleSize <<= 1;
            bitmap = BitmapFactory.decodeFile(path, options);
        }

        Matrix matrix = new Matrix();
        if(bitmap == null) {
            return bitmap;
        } else {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            if(orientation == 6 || orientation == 8 || orientation == 5 || orientation == 7) {
                int xS = w;
                w = h;
                h = xS;
            }

            switch(orientation) {
                case 2:
                    matrix.preScale(-1.0F, 1.0F);
                    break;
                case 3:
                    matrix.setRotate(180.0F, (float)w / 2.0F, (float)h / 2.0F);
                    break;
                case 4:
                    matrix.preScale(1.0F, -1.0F);
                    break;
                case 5:
                    matrix.setRotate(90.0F, (float)w / 2.0F, (float)h / 2.0F);
                    matrix.preScale(1.0F, -1.0F);
                    break;
                case 6:
                    matrix.setRotate(90.0F, (float)w / 2.0F, (float)h / 2.0F);
                    break;
                case 7:
                    matrix.setRotate(270.0F, (float)w / 2.0F, (float)h / 2.0F);
                    matrix.preScale(1.0F, -1.0F);
                    break;
                case 8:
                    matrix.setRotate(270.0F, (float)w / 2.0F, (float)h / 2.0F);
            }

            float xS1 = (float)widthLimit / (float)bitmap.getWidth();
            float yS = (float)heightLimit / (float)bitmap.getHeight();
            matrix.postScale(Math.min(xS1, yS), Math.min(xS1, yS));

            try {
                result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                return result;
            } catch (OutOfMemoryError var21) {
                var21.printStackTrace();
                Log.e("ResourceCompressHandler", "OOMHeight:" + bitmap.getHeight() + "Width:" + bitmap.getHeight() + "matrix:" + xS1 + " " + yS);
                return null;
            }
        }
    }

    public static String md5(Object object) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(toByteArray(object));
        } catch (NoSuchAlgorithmException var7) {
            throw new RuntimeException("Huh, MD5 should be supported?", var7);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        byte[] arr$ = hash;
        int len$ = hash.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            byte b = arr$[i$];
            if((b & 255) < 16) {
                hex.append("0");
            }

            hex.append(Integer.toHexString(b & 255));
        }

        return hex.toString();
    }

    private static byte[] toByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ObjectOutputStream ex = new ObjectOutputStream(bos);
            ex.writeObject(obj);
            ex.flush();
            bytes = bos.toByteArray();
            ex.close();
            bos.close();
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        return bytes;
    }
}
