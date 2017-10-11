package com.golike.customviews.emoticon;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ReplacementSpan;
import android.util.DisplayMetrics;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/8/8.
 */

public class AndroidEmoji {
    private static float density;
    private static Context mContext;
    private static Map<Integer, EmojiInfo> sEmojiMap;
    private static List<EmojiInfo> sEmojiList;

    public AndroidEmoji() {
    }

    public static void init(Context context) {
        sEmojiMap = new HashMap();
        sEmojiList = new ArrayList();
        mContext = context.getApplicationContext();
        int[] codes = context.getResources().getIntArray(context.getResources().getIdentifier("rc_emoji_code", "array", context.getPackageName()));
        TypedArray array = context.getResources().obtainTypedArray(context.getResources().getIdentifier("rc_emoji_res", "array", context.getPackageName()));
        if(codes.length != array.length()) {
            throw new RuntimeException("Emoji resource init fail.");
        } else {
            int i = -1;

            while(true) {
                ++i;
                if(i >= codes.length) {
                    DisplayMetrics var5 = context.getResources().getDisplayMetrics();
                    density = var5.density;
                    array.recycle();
                    return;
                }

                AndroidEmoji.EmojiInfo dm = new AndroidEmoji.EmojiInfo(codes[i], array.getResourceId(i, -1));
                sEmojiMap.put(Integer.valueOf(codes[i]), dm);
                sEmojiList.add(dm);
            }
        }
    }

    public static List<AndroidEmoji.EmojiInfo> getEmojiList() {
        return sEmojiList;
    }

    public static int getEmojiCount(String input) {
        if(input == null) {
            return 0;
        } else {
            int count = 0;
            char[] chars = input.toCharArray();
            new SpannableStringBuilder(input);

            for(int i = 0; i < chars.length; ++i) {
                if(!Character.isHighSurrogate(chars[i])) {
                    int codePoint;
                    boolean isSurrogatePair;
                    if(Character.isLowSurrogate(chars[i])) {
                        if(i <= 0 || !Character.isSurrogatePair(chars[i - 1], chars[i])) {
                            continue;
                        }

                        codePoint = Character.toCodePoint(chars[i - 1], chars[i]);
                        isSurrogatePair = true;
                    } else {
                        codePoint = chars[i];
                        isSurrogatePair = false;
                    }

                    if(sEmojiMap.containsKey(Integer.valueOf(codePoint))) {
                        ++count;
                    }
                }
            }

            return count;
        }
    }

    public static CharSequence ensure(String input) {
        if(input == null) {
            return input;
        } else {
            char[] chars = input.toCharArray();
            SpannableStringBuilder ssb = new SpannableStringBuilder(input);

            for(int i = 0; i < chars.length; ++i) {
                if(!Character.isHighSurrogate(chars[i])) {
                    int codePoint;
                    boolean isSurrogatePair;
                    if(Character.isLowSurrogate(chars[i])) {
                        if(i <= 0 || !Character.isSurrogatePair(chars[i - 1], chars[i])) {
                            continue;
                        }

                        codePoint = Character.toCodePoint(chars[i - 1], chars[i]);
                        isSurrogatePair = true;
                    } else {
                        codePoint = chars[i];
                        isSurrogatePair = false;
                    }

                    if(sEmojiMap.containsKey(Integer.valueOf(codePoint))) {
                        ssb.setSpan(new AndroidEmoji.EmojiImageSpan(codePoint), isSurrogatePair?i - 1:i, i + 1, 33);
                    }
                }
            }

            return ssb;
        }
    }

    public static boolean isEmoji(String input) {
        if(input == null) {
            return false;
        } else {
            char[] chars = input.toCharArray();
            boolean codePoint = false;
            int length = chars.length;

            for(int i = 0; i < length; ++i) {
                if(!Character.isHighSurrogate(chars[i])) {
                    int var5;
                    if(Character.isLowSurrogate(chars[i])) {
                        if(i <= 0 || !Character.isSurrogatePair(chars[i - 1], chars[i])) {
                            continue;
                        }

                        var5 = Character.toCodePoint(chars[i - 1], chars[i]);
                    } else {
                        var5 = chars[i];
                    }

                    if(sEmojiMap.containsKey(Integer.valueOf(var5))) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public static void ensure(Spannable spannable) {
        char[] chars = spannable.toString().toCharArray();

        for(int i = 0; i < chars.length; ++i) {
            if(!Character.isHighSurrogate(chars[i])) {
                int codePoint;
                boolean isSurrogatePair;
                if(Character.isLowSurrogate(chars[i])) {
                    if(i <= 0 || !Character.isSurrogatePair(chars[i - 1], chars[i])) {
                        continue;
                    }

                    codePoint = Character.toCodePoint(chars[i - 1], chars[i]);
                    isSurrogatePair = true;
                } else {
                    codePoint = chars[i];
                    isSurrogatePair = false;
                }

                if(sEmojiMap.containsKey(Integer.valueOf(codePoint))) {
                    spannable.setSpan(new AndroidEmoji.EmojiImageSpan(codePoint), isSurrogatePair?i - 1:i, i + 1, 34);
                }
            }
        }

    }

    public static int getEmojiSize() {
        return sEmojiMap.size();
    }

    public static int getEmojiCode(int index) {
        AndroidEmoji.EmojiInfo info = (AndroidEmoji.EmojiInfo)sEmojiList.get(index);
        return info.code;
    }

    public static Drawable getEmojiDrawable(Context context, int index) {
        Drawable drawable = null;
        if(index >= 0 && index < sEmojiList.size()) {
            AndroidEmoji.EmojiInfo emoji = (AndroidEmoji.EmojiInfo)sEmojiList.get(index);
            drawable = context.getResources().getDrawable(emoji.resId);
        }

        return drawable;
    }

    private static class EmojiInfo {
        int code;
        int resId;

        public EmojiInfo(int code, int resId) {
            this.code = code;
            this.resId = resId;
        }
    }

    public static class EmojiImageSpan extends ReplacementSpan {
        Drawable mDrawable;
        private static final String TAG = "DynamicDrawableSpan";
        public static final int ALIGN_BOTTOM = 0;
        private WeakReference<Drawable> mDrawableRef;

        private EmojiImageSpan(int codePoint) {
            if(AndroidEmoji.sEmojiMap.containsKey(Integer.valueOf(codePoint))) {
                this.mDrawable = AndroidEmoji.mContext.getResources().getDrawable((AndroidEmoji.sEmojiMap.get(Integer.valueOf(codePoint))).resId);
                int width = this.mDrawable.getIntrinsicWidth() - (int)(4.0F * AndroidEmoji.density);
                int height = this.mDrawable.getIntrinsicHeight() - (int)(4.0F * AndroidEmoji.density);
                this.mDrawable.setBounds(0, 0, width > 0?width:0, height > 0?height:0);
            }

        }

        public Drawable getDrawable() {
            return this.mDrawable;
        }

        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            Drawable d = this.getCachedDrawable();
            Rect rect = d.getBounds();
            if(fm != null) {
                fm.ascent = -rect.bottom;
                fm.descent = 0;
                fm.top = fm.ascent;
                fm.bottom = 0;
            }

            return rect.right;
        }

        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            Drawable b = this.getCachedDrawable();
            canvas.save();
            int transY = bottom - b.getBounds().bottom;
            transY = (int)((float)transY - AndroidEmoji.density);
            canvas.translate(x, (float)transY);
            b.draw(canvas);
            canvas.restore();
        }

        private Drawable getCachedDrawable() {
            WeakReference wr = this.mDrawableRef;
            Drawable d = null;
            if(wr != null) {
                d = (Drawable)wr.get();
            }

            if(d == null) {
                d = this.getDrawable();
                this.mDrawableRef = new WeakReference(d);
            }

            return d;
        }
    }
}

