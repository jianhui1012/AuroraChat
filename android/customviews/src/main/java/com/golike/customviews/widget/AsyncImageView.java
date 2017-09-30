package com.golike.customviews.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.golike.customviews.R;

import java.lang.ref.WeakReference;

/**
 * Created by admin on 2017/9/25.
 */

public class AsyncImageView extends ImageView {

    private static final String TAG = "AsyncImageView";
    private Context mContext;
    private boolean isCircle;
    private float minShortSideSize = 0.0F;
    private int mCornerRadius = 0;
    private static final int AVATAR_SIZE = 80;
    private Drawable mDefaultDrawable;
    private WeakReference<Bitmap> mWeakBitmap;
    private WeakReference<Bitmap> mShardWeakBitmap;
    private boolean mHasMask;

    public AsyncImageView(Context context) {
        super(context);
    }

    public AsyncImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext=context;
        if(!this.isInEditMode()) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AsyncImageView);
            int resId = a.getResourceId(R.styleable.AsyncImageView_RCDefDrawable, 0);
            this.isCircle = a.getInt(R.styleable.AsyncImageView_RCShape, 0) == 1;
            this.minShortSideSize = a.getDimension(R.styleable.AsyncImageView_RCMinShortSideSize, 0.0F);
            this.mCornerRadius = (int)a.getDimension(R.styleable.AsyncImageView_RCCornerRadius, 0.0F);
            this.mHasMask = a.getBoolean(R.styleable.AsyncImageView_RCMask, false);
            if(resId != 0) {
                this.mDefaultDrawable = this.getResources().getDrawable(resId);
            }
            a.recycle();
        }
    }

    protected void onDraw(Canvas canvas) {
        if(this.mHasMask) {
            Bitmap bitmap = this.mWeakBitmap == null?null: this.mWeakBitmap.get();
            Drawable drawable = this.getDrawable();
            RCMessageFrameLayout parent = (RCMessageFrameLayout)this.getParent();
            Drawable background = parent.getBackgroundDrawable();
            if(bitmap != null && !bitmap.isRecycled()) {
                canvas.drawBitmap(bitmap, 0.0F, 0.0F, null);
                this.getShardImage(background, bitmap, canvas);
            } else {
                int width = this.getWidth();
                int height = this.getHeight();
                if(width <= 0 || height <= 0) {
                    return;
                }

                try {
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                } catch (OutOfMemoryError var11) {
                    Log.e("AsyncImageView", "onDraw OutOfMemoryError");
                    var11.printStackTrace();
                    System.gc();
                }

                if(bitmap != null) {
                    Canvas rCanvas = new Canvas(bitmap);
                    if(drawable != null) {
                        drawable.setBounds(0, 0, width, height);
                        drawable.draw(rCanvas);
                        if(background != null && background instanceof NinePatchDrawable) {
                            NinePatchDrawable patchDrawable = (NinePatchDrawable)background;
                            patchDrawable.setBounds(0, 0, width, height);
                            Paint maskPaint = patchDrawable.getPaint();
                            maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                            patchDrawable.draw(rCanvas);
                        }

                        this.mWeakBitmap = new WeakReference(bitmap);
                    }

                    canvas.drawBitmap(bitmap, 0.0F, 0.0F, null);
                    this.getShardImage(background, bitmap, canvas);
                }
            }
        } else {
            super.onDraw(canvas);
        }

    }

    private void getShardImage(Drawable drawable_bg, Bitmap bp, Canvas canvas) {
        int width = bp.getWidth();
        int height = bp.getHeight();
        Bitmap bitmap = this.mShardWeakBitmap == null?null:(Bitmap)this.mShardWeakBitmap.get();
        if(width > 0 && height > 0) {
            if(bitmap != null && !bitmap.isRecycled()) {
                canvas.drawBitmap(bitmap, 0.0F, 0.0F, (Paint)null);
            } else {
                try {
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                } catch (OutOfMemoryError var14) {
                    Log.e("AsyncImageView", "getShardImage OutOfMemoryError");
                    var14.printStackTrace();
                    System.gc();
                }

                if(bitmap != null) {
                    Canvas rCanvas = new Canvas(bitmap);
                    Paint paint = new Paint();
                    paint.setAntiAlias(true);
                    Rect rect = new Rect(0, 0, width, height);
                    Rect rectF = new Rect(1, 1, width - 1, height - 1);
                    BitmapDrawable drawable_in = new BitmapDrawable(bp);
                    drawable_in.setBounds(rectF);
                    drawable_in.draw(rCanvas);
                    if(drawable_bg instanceof NinePatchDrawable) {
                        NinePatchDrawable patchDrawable = (NinePatchDrawable)drawable_bg;
                        patchDrawable.setBounds(rect);
                        Paint maskPaint = patchDrawable.getPaint();
                        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
                        patchDrawable.draw(rCanvas);
                    }

                    this.mShardWeakBitmap = new WeakReference(bitmap);
                    canvas.drawBitmap(bitmap, 0.0F, 0.0F, paint);
                }
            }

        }
    }

    protected void onDetachedFromWindow() {
        Bitmap bitmap;
        if(this.mWeakBitmap != null) {
            bitmap =this.mWeakBitmap.get();
            if(bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }

            this.mWeakBitmap = null;
        }

        if(this.mShardWeakBitmap != null) {
            bitmap = (Bitmap)this.mShardWeakBitmap.get();
            if(bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }

            this.mShardWeakBitmap = null;
        }

        super.onDetachedFromWindow();
    }

    public void invalidate() {
        Bitmap bitmap;
        if(this.mWeakBitmap != null) {
            bitmap = this.mWeakBitmap.get();
            if(bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }

            this.mWeakBitmap = null;
        }

        if(this.mShardWeakBitmap != null) {
            bitmap =  this.mShardWeakBitmap.get();
            if(bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }

            this.mShardWeakBitmap = null;
        }
        super.invalidate();
    }

    public void setCircle(boolean circle) {
        this.isCircle = circle;
    }

    public void setResource(Uri imageUri) {
        if(imageUri != null) {
            //this.setLayoutParam(bitmap1);
            Glide.with(mContext).load(imageUri).into(this);
        }
    }

    public int getCornerRadius() {
        return this.mCornerRadius;
    }

    public void setCornerRadius(int mCornerRadius) {
        this.mCornerRadius = mCornerRadius;
    }

    private void setLayoutParam(Bitmap bitmap) {
        float width = (float)bitmap.getWidth();
        float height = (float)bitmap.getHeight();
        byte minSize = 100;
        if(this.minShortSideSize > 0.0F) {
            if(width > this.minShortSideSize && height > this.minShortSideSize) {
                ViewGroup.LayoutParams params2 = this.getLayoutParams();
                params2.height = (int)height;
                params2.width = (int)width;
                this.setLayoutParams(params2);
            } else {
                float params = width / height;
                int finalWidth;
                int finalHeight;
                if(params > 1.0F) {
                    finalHeight = (int)(this.minShortSideSize / params);
                    if(finalHeight < minSize) {
                        finalHeight = minSize;
                    }

                    finalWidth = (int)this.minShortSideSize;
                } else {
                    finalHeight = (int)this.minShortSideSize;
                    finalWidth = (int)(this.minShortSideSize * params);
                    if(finalWidth < minSize) {
                        finalWidth = minSize;
                    }
                }

                ViewGroup.LayoutParams params1 = this.getLayoutParams();
                params1.height = finalHeight;
                params1.width = finalWidth;
                this.setLayoutParams(params1);
            }
        }

    }

}
