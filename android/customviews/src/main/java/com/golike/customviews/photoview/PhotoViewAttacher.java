package com.golike.customviews.photoview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.ImageView;
import com.golike.customviews.photoview.gestures.OnGestureListener;
import com.golike.customviews.photoview.gestures.VersionedGestureDetector;
import com.golike.customviews.photoview.log.LogManager;
import com.golike.customviews.photoview.scrollerproxy.ScrollerProxy;

import java.lang.ref.WeakReference;

/**
 * Created by admin on 2017/8/31.
 */

public class PhotoViewAttacher implements IPhotoView, View.OnTouchListener, OnGestureListener, ViewTreeObserver.OnGlobalLayoutListener {
    private static final String LOG_TAG = "PhotoViewAttacher";
    private static final boolean DEBUG = Log.isLoggable("PhotoViewAttacher", 3);
    static final Interpolator sInterpolator = new AccelerateDecelerateInterpolator();
    int ZOOM_DURATION;
    static final int EDGE_NONE = -1;
    static final int EDGE_LEFT = 0;
    static final int EDGE_RIGHT = 1;
    static final int EDGE_BOTH = 2;
    static int SINGLE_TOUCH = 1;
    private float mMinScale;
    private float mMidScale;
    private float mMaxScale;
    private boolean mAllowParentInterceptOnEdge;
    private boolean mBlockParentIntercept;
    private WeakReference<ImageView> mImageView;
    private GestureDetector mGestureDetector;
    private com.golike.customviews.photoview.gestures.GestureDetector mScaleDragDetector;
    private final Matrix mBaseMatrix;
    private final Matrix mDrawMatrix;
    private final Matrix mSuppMatrix;
    private final RectF mDisplayRect;
    private final float[] mMatrixValues;
    private PhotoViewAttacher.OnMatrixChangedListener mMatrixChangeListener;
    private PhotoViewAttacher.OnPhotoTapListener mPhotoTapListener;
    private PhotoViewAttacher.OnViewTapListener mViewTapListener;
    private View.OnLongClickListener mLongClickListener;
    private PhotoViewAttacher.OnScaleChangeListener mScaleChangeListener;
    private PhotoViewAttacher.OnSingleFlingListener mSingleFlingListener;
    private int mIvTop;
    private int mIvRight;
    private int mIvBottom;
    private int mIvLeft;
    private PhotoViewAttacher.FlingRunnable mCurrentFlingRunnable;
    private int mScrollEdge;
    private float mBaseRotation;
    private boolean mZoomEnabled;
    private ImageView.ScaleType mScaleType;

    private static void checkZoomLevels(float minZoom, float midZoom, float maxZoom) {
        if(minZoom >= midZoom) {
            throw new IllegalArgumentException("MinZoom has to be less than MidZoom");
        } else if(midZoom >= maxZoom) {
            throw new IllegalArgumentException("MidZoom has to be less than MaxZoom");
        }
    }

    private static boolean hasDrawable(ImageView imageView) {
        return null != imageView && null != imageView.getDrawable();
    }

    private static boolean isSupportedScaleType(ImageView.ScaleType scaleType) {
        if(null == scaleType) {
            return false;
        } else {
            switch(ImageView.ScaleType.values()[scaleType.ordinal()]) {
                case FIT_XY:
                    throw new IllegalArgumentException(scaleType.name() + " is not supported in PhotoView");
                default:
                    return true;
            }
        }
    }

    private static void setImageViewScaleTypeMatrix(ImageView imageView) {
        if(null != imageView && !(imageView instanceof IPhotoView) && !ImageView.ScaleType.MATRIX.equals(imageView.getScaleType())) {
            imageView.setScaleType(ImageView.ScaleType.MATRIX);
        }

    }

    public PhotoViewAttacher(ImageView imageView) {
        this(imageView, true);
    }

    public PhotoViewAttacher(ImageView imageView, boolean zoomable) {
        this.ZOOM_DURATION = 200;
        this.mMinScale = 1.0F;
        this.mMidScale = 1.75F;
        this.mMaxScale = 3.0F;
        this.mAllowParentInterceptOnEdge = true;
        this.mBlockParentIntercept = false;
        this.mBaseMatrix = new Matrix();
        this.mDrawMatrix = new Matrix();
        this.mSuppMatrix = new Matrix();
        this.mDisplayRect = new RectF();
        this.mMatrixValues = new float[9];
        this.mScrollEdge = 2;
        this.mScaleType = ImageView.ScaleType.FIT_CENTER;
        this.mImageView = new WeakReference(imageView);
        imageView.setDrawingCacheEnabled(true);
        imageView.setOnTouchListener(this);
        ViewTreeObserver observer = imageView.getViewTreeObserver();
        if(null != observer) {
            observer.addOnGlobalLayoutListener(this);
        }

        setImageViewScaleTypeMatrix(imageView);
        if(!imageView.isInEditMode()) {
            this.mScaleDragDetector = VersionedGestureDetector.newInstance(imageView.getContext(), this);
            this.mGestureDetector = new GestureDetector(imageView.getContext(), new  SimpleOnGestureListener() {
                public void onLongPress(MotionEvent e) {
                    if(null != PhotoViewAttacher.this.mLongClickListener) {
                        PhotoViewAttacher.this.mLongClickListener.onLongClick(PhotoViewAttacher.this.getImageView());
                    }

                }

                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    return PhotoViewAttacher.this.mSingleFlingListener != null?(PhotoViewAttacher.this.getScale() > 1.0F?false:(MotionEventCompat.getPointerCount(e1) <= PhotoViewAttacher.SINGLE_TOUCH && MotionEventCompat.getPointerCount(e2) <= PhotoViewAttacher.SINGLE_TOUCH?PhotoViewAttacher.this.mSingleFlingListener.onFling(e1, e2, velocityX, velocityY):false)):false;
                }
            });
            this.mGestureDetector.setOnDoubleTapListener(new DefaultOnDoubleTapListener(this));
            this.mBaseRotation = 0.0F;
            this.setZoomable(zoomable);
        }
    }

    public void setOnDoubleTapListener(OnDoubleTapListener newOnDoubleTapListener) {
        if(newOnDoubleTapListener != null) {
            this.mGestureDetector.setOnDoubleTapListener(newOnDoubleTapListener);
        } else {
            this.mGestureDetector.setOnDoubleTapListener(new DefaultOnDoubleTapListener(this));
        }

    }

    public void setOnScaleChangeListener(PhotoViewAttacher.OnScaleChangeListener onScaleChangeListener) {
        this.mScaleChangeListener = onScaleChangeListener;
    }

    public void setOnSingleFlingListener(PhotoViewAttacher.OnSingleFlingListener onSingleFlingListener) {
        this.mSingleFlingListener = onSingleFlingListener;
    }

    public boolean canZoom() {
        return this.mZoomEnabled;
    }

    public void cleanup() {
        if(null != this.mImageView) {
            ImageView imageView = (ImageView)this.mImageView.get();
            if(null != imageView) {
                ViewTreeObserver observer = imageView.getViewTreeObserver();
                if(null != observer && observer.isAlive()) {
                    observer.removeGlobalOnLayoutListener(this);
                }

                imageView.setOnTouchListener((View.OnTouchListener)null);
                this.cancelFling();
            }

            if(null != this.mGestureDetector) {
                this.mGestureDetector.setOnDoubleTapListener((GestureDetector.OnDoubleTapListener)null);
            }

            this.mMatrixChangeListener = null;
            this.mPhotoTapListener = null;
            this.mViewTapListener = null;
            this.mImageView = null;
        }
    }

    public RectF getDisplayRect() {
        this.checkMatrixBounds();
        return this.getDisplayRect(this.getDrawMatrix());
    }

    public boolean setDisplayMatrix(Matrix finalMatrix) {
        if(finalMatrix == null) {
            throw new IllegalArgumentException("Matrix cannot be null");
        } else {
            ImageView imageView = this.getImageView();
            if(null == imageView) {
                return false;
            } else if(null == imageView.getDrawable()) {
                return false;
            } else {
                this.mSuppMatrix.set(finalMatrix);
                this.setImageViewMatrix(this.getDrawMatrix());
                this.checkMatrixBounds();
                return true;
            }
        }
    }

    public void setBaseRotation(float degrees) {
        this.mBaseRotation = degrees % 360.0F;
        this.update();
        this.setRotationBy(this.mBaseRotation);
        this.checkAndDisplayMatrix();
    }

    /** @deprecated */
    public void setPhotoViewRotation(float degrees) {
        this.mSuppMatrix.setRotate(degrees % 360.0F);
        this.checkAndDisplayMatrix();
    }

    public void setRotationTo(float degrees) {
        this.mSuppMatrix.setRotate(degrees % 360.0F);
        this.checkAndDisplayMatrix();
    }

    public void setRotationBy(float degrees) {
        this.mSuppMatrix.postRotate(degrees % 360.0F);
        this.checkAndDisplayMatrix();
    }

    public ImageView getImageView() {
        ImageView imageView = null;
        if(null != this.mImageView) {
            imageView = (ImageView)this.mImageView.get();
        }

        if(null == imageView) {
            this.cleanup();
            LogManager.getLogger().i("PhotoViewAttacher", "ImageView no longer exists. You should not use this PhotoViewAttacher any more.");
        }

        return imageView;
    }

    /** @deprecated */
    @Deprecated
    public float getMinScale() {
        return this.getMinimumScale();
    }

    public float getMinimumScale() {
        return this.mMinScale;
    }

    /** @deprecated */
    @Deprecated
    public float getMidScale() {
        return this.getMediumScale();
    }

    public float getMediumScale() {
        return this.mMidScale;
    }

    /** @deprecated */
    @Deprecated
    public float getMaxScale() {
        return this.getMaximumScale();
    }

    public float getMaximumScale() {
        return this.mMaxScale;
    }

    public float getScale() {
        return (float)Math.sqrt((double)((float)Math.pow((double)this.getValue(this.mSuppMatrix, 0), 2.0D) + (float)Math.pow((double)this.getValue(this.mSuppMatrix, 3), 2.0D)));
    }

    public ImageView.ScaleType getScaleType() {
        return this.mScaleType;
    }

    public void onDrag(float dx, float dy) {
        if(!this.mScaleDragDetector.isScaling()) {
            if(DEBUG) {
                LogManager.getLogger().d("PhotoViewAttacher", String.format("onDrag: dx: %.2f. dy: %.2f", new Object[]{Float.valueOf(dx), Float.valueOf(dy)}));
            }

            ImageView imageView = this.getImageView();
            this.mSuppMatrix.postTranslate(dx, dy);
            this.checkAndDisplayMatrix();
            ViewParent parent = imageView.getParent();
            if(this.mAllowParentInterceptOnEdge && !this.mScaleDragDetector.isScaling() && !this.mBlockParentIntercept) {
                if((this.mScrollEdge == 2 || this.mScrollEdge == 0 && dx >= 1.0F || this.mScrollEdge == 1 && dx <= -1.0F) && null != parent) {
                    parent.requestDisallowInterceptTouchEvent(false);
                }
            } else if(null != parent) {
                parent.requestDisallowInterceptTouchEvent(true);
            }

        }
    }

    public void onFling(float startX, float startY, float velocityX, float velocityY) {
        if(DEBUG) {
            LogManager.getLogger().d("PhotoViewAttacher", "onFling. sX: " + startX + " sY: " + startY + " Vx: " + velocityX + " Vy: " + velocityY);
        }

        ImageView imageView = this.getImageView();
        this.mCurrentFlingRunnable = new PhotoViewAttacher.FlingRunnable(imageView.getContext());
        this.mCurrentFlingRunnable.fling(this.getImageViewWidth(imageView), this.getImageViewHeight(imageView), (int)velocityX, (int)velocityY);
        imageView.post(this.mCurrentFlingRunnable);
    }

    public void onGlobalLayout() {
        ImageView imageView = this.getImageView();
        if(null != imageView) {
            if(this.mZoomEnabled) {
                int top = imageView.getTop();
                int right = imageView.getRight();
                int bottom = imageView.getBottom();
                int left = imageView.getLeft();
                if(top != this.mIvTop || bottom != this.mIvBottom || left != this.mIvLeft || right != this.mIvRight) {
                    this.updateBaseMatrix(imageView.getDrawable());
                    this.mIvTop = top;
                    this.mIvRight = right;
                    this.mIvBottom = bottom;
                    this.mIvLeft = left;
                }
            } else {
                this.updateBaseMatrix(imageView.getDrawable());
            }
        }

    }

    public void onScale(float scaleFactor, float focusX, float focusY) {
        if(DEBUG) {
            LogManager.getLogger().d("PhotoViewAttacher", String.format("onScale: scale: %.2f. fX: %.2f. fY: %.2f", new Object[]{Float.valueOf(scaleFactor), Float.valueOf(focusX), Float.valueOf(focusY)}));
        }

        if((this.getScale() < this.mMaxScale || scaleFactor < 1.0F) && (this.getScale() > this.mMinScale || scaleFactor > 1.0F)) {
            if(null != this.mScaleChangeListener) {
                this.mScaleChangeListener.onScaleChange(scaleFactor, focusX, focusY);
            }

            this.mSuppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
            this.checkAndDisplayMatrix();
        }

    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouch(View v, MotionEvent ev) {
        boolean handled = false;
        if(this.mZoomEnabled && hasDrawable((ImageView)v)) {
            ViewParent parent = v.getParent();
            switch(ev.getAction()) {
                case 0:
                    if(null != parent) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    } else {
                        LogManager.getLogger().i("PhotoViewAttacher", "onTouch getParent() returned null");
                    }

                    this.cancelFling();
                    break;
                case 1:
                case 3:
                    if(this.getScale() < this.mMinScale) {
                        RectF wasScaling = this.getDisplayRect();
                        if(null != wasScaling) {
                            v.post(new PhotoViewAttacher.AnimatedZoomRunnable(this.getScale(), this.mMinScale, wasScaling.centerX(), wasScaling.centerY()));
                            handled = true;
                        }
                    }
                case 2:
            }

            if(null != this.mScaleDragDetector) {
                boolean wasScaling1 = this.mScaleDragDetector.isScaling();
                boolean wasDragging = this.mScaleDragDetector.isDragging();
                handled = this.mScaleDragDetector.onTouchEvent(ev);
                boolean didntScale = !wasScaling1 && !this.mScaleDragDetector.isScaling();
                boolean didntDrag = !wasDragging && !this.mScaleDragDetector.isDragging();
                this.mBlockParentIntercept = didntScale && didntDrag;
            }

            if(null != this.mGestureDetector && this.mGestureDetector.onTouchEvent(ev)) {
                handled = true;
            }
        }

        return handled;
    }

    public void setAllowParentInterceptOnEdge(boolean allow) {
        this.mAllowParentInterceptOnEdge = allow;
    }

    /** @deprecated */
    @Deprecated
    public void setMinScale(float minScale) {
        this.setMinimumScale(minScale);
    }

    public void setMinimumScale(float minimumScale) {
        checkZoomLevels(minimumScale, this.mMidScale, this.mMaxScale);
        this.mMinScale = minimumScale;
    }

    /** @deprecated */
    @Deprecated
    public void setMidScale(float midScale) {
        this.setMediumScale(midScale);
    }

    public void setMediumScale(float mediumScale) {
        checkZoomLevels(this.mMinScale, mediumScale, this.mMaxScale);
        this.mMidScale = mediumScale;
    }

    /** @deprecated */
    @Deprecated
    public void setMaxScale(float maxScale) {
        this.setMaximumScale(maxScale);
    }

    public void setMaximumScale(float maximumScale) {
        checkZoomLevels(this.mMinScale, this.mMidScale, maximumScale);
        this.mMaxScale = maximumScale;
    }

    public void setScaleLevels(float minimumScale, float mediumScale, float maximumScale) {
        checkZoomLevels(minimumScale, mediumScale, maximumScale);
        this.mMinScale = minimumScale;
        this.mMidScale = mediumScale;
        this.mMaxScale = maximumScale;
    }

    public void setOnLongClickListener(View.OnLongClickListener listener) {
        this.mLongClickListener = listener;
    }

    public void setOnMatrixChangeListener(PhotoViewAttacher.OnMatrixChangedListener listener) {
        this.mMatrixChangeListener = listener;
    }

    public void setOnPhotoTapListener(PhotoViewAttacher.OnPhotoTapListener listener) {
        this.mPhotoTapListener = listener;
    }

    public PhotoViewAttacher.OnPhotoTapListener getOnPhotoTapListener() {
        return this.mPhotoTapListener;
    }

    public void setOnViewTapListener(PhotoViewAttacher.OnViewTapListener listener) {
        this.mViewTapListener = listener;
    }

    public PhotoViewAttacher.OnViewTapListener getOnViewTapListener() {
        return this.mViewTapListener;
    }

    public void setScale(float scale) {
        this.setScale(scale, false);
    }

    public void setScale(float scale, boolean animate) {
        ImageView imageView = this.getImageView();
        if(null != imageView) {
            this.setScale(scale, (float)(imageView.getRight() / 2), (float)(imageView.getBottom() / 2), animate);
        }

    }

    public void setScale(float scale, float focalX, float focalY, boolean animate) {
        ImageView imageView = this.getImageView();
        if(null != imageView) {
            if(scale < this.mMinScale || scale > this.mMaxScale) {
                LogManager.getLogger().i("PhotoViewAttacher", "Scale must be within the range of minScale and maxScale");
                return;
            }

            if(animate) {
                imageView.post(new PhotoViewAttacher.AnimatedZoomRunnable(this.getScale(), scale, focalX, focalY));
            } else {
                this.mSuppMatrix.setScale(scale, scale, focalX, focalY);
                this.checkAndDisplayMatrix();
            }
        }

    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        if(isSupportedScaleType(scaleType) && scaleType != this.mScaleType) {
            this.mScaleType = scaleType;
            this.update();
        }

    }

    public void setZoomable(boolean zoomable) {
        this.mZoomEnabled = zoomable;
        this.update();
    }

    public void update() {
        ImageView imageView = this.getImageView();
        if(null != imageView) {
            if(this.mZoomEnabled) {
                setImageViewScaleTypeMatrix(imageView);
                this.updateBaseMatrix(imageView.getDrawable());
            } else {
                this.resetMatrix();
            }
        }

    }

    public Matrix getDisplayMatrix() {
        return new Matrix(this.getDrawMatrix());
    }

    public void getDisplayMatrix(Matrix matrix) {
        matrix.set(this.getDrawMatrix());
    }

    public Matrix getDrawMatrix() {
        this.mDrawMatrix.set(this.mBaseMatrix);
        this.mDrawMatrix.postConcat(this.mSuppMatrix);
        return this.mDrawMatrix;
    }

    private void cancelFling() {
        if(null != this.mCurrentFlingRunnable) {
            this.mCurrentFlingRunnable.cancelFling();
            this.mCurrentFlingRunnable = null;
        }

    }

    private void checkAndDisplayMatrix() {
        if(this.checkMatrixBounds()) {
            this.setImageViewMatrix(this.getDrawMatrix());
        }

    }

    private void checkImageViewScaleType() {
        ImageView imageView = this.getImageView();
        if(null != imageView && !(imageView instanceof IPhotoView) && !ImageView.ScaleType.MATRIX.equals(imageView.getScaleType())) {
            throw new IllegalStateException("The ImageView\'s ScaleType has been changed since attaching a PhotoViewAttacher");
        }
    }

    private boolean checkMatrixBounds() {
        ImageView imageView = this.getImageView();
        if(null == imageView) {
            return false;
        } else {
            RectF rect = this.getDisplayRect(this.getDrawMatrix());
            if(null == rect) {
                return false;
            } else {
                float height = rect.height();
                float width = rect.width();
                float deltaX = 0.0F;
                float deltaY = 0.0F;
                int viewHeight = this.getImageViewHeight(imageView);
                if(height <= (float)viewHeight) {
                    switch(ImageView.ScaleType.values()[this.mScaleType.ordinal()]) {
                        case FIT_START:
                            deltaY = -rect.top;
                            break;
                        case FIT_CENTER:
                            deltaY = (float)viewHeight - height - rect.top;
                            break;
                        default:
                            deltaY = ((float)viewHeight - height) / 2.0F - rect.top;
                    }
                } else if(rect.top > 0.0F) {
                    deltaY = -rect.top;
                } else if(rect.bottom < (float)viewHeight) {
                    deltaY = (float)viewHeight - rect.bottom;
                }

                int viewWidth = this.getImageViewWidth(imageView);
                if(width <= (float)viewWidth) {
                    switch(ImageView.ScaleType.values()[this.mScaleType.ordinal()]) {
                        case FIT_START:
                            deltaX = -rect.left;
                            break;
                        case FIT_CENTER:
                            deltaX = (float)viewWidth - width - rect.left;
                            break;
                        default:
                            deltaX = ((float)viewWidth - width) / 2.0F - rect.left;
                    }

                    this.mScrollEdge = 2;
                } else if(rect.left > 0.0F) {
                    this.mScrollEdge = 0;
                    deltaX = -rect.left;
                } else if(rect.right < (float)viewWidth) {
                    deltaX = (float)viewWidth - rect.right;
                    this.mScrollEdge = 1;
                } else {
                    this.mScrollEdge = -1;
                }

                this.mSuppMatrix.postTranslate(deltaX, deltaY);
                return true;
            }
        }
    }

    private RectF getDisplayRect(Matrix matrix) {
        ImageView imageView = this.getImageView();
        if(null != imageView) {
            Drawable d = imageView.getDrawable();
            if(null != d) {
                this.mDisplayRect.set(0.0F, 0.0F, (float)d.getIntrinsicWidth(), (float)d.getIntrinsicHeight());
                matrix.mapRect(this.mDisplayRect);
                return this.mDisplayRect;
            }
        }

        return null;
    }

    public Bitmap getVisibleRectangleBitmap() {
        ImageView imageView = this.getImageView();
        return imageView == null?null:imageView.getDrawingCache();
    }

    public void setZoomTransitionDuration(int milliseconds) {
        if(milliseconds < 0) {
            milliseconds = 200;
        }

        this.ZOOM_DURATION = milliseconds;
    }

    public IPhotoView getIPhotoViewImplementation() {
        return this;
    }

    private float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(this.mMatrixValues);
        return this.mMatrixValues[whichValue];
    }

    private void resetMatrix() {
        this.mSuppMatrix.reset();
        this.setRotationBy(this.mBaseRotation);
        this.setImageViewMatrix(this.getDrawMatrix());
        this.checkMatrixBounds();
    }

    private void setImageViewMatrix(Matrix matrix) {
        ImageView imageView = this.getImageView();
        if(null != imageView) {
            this.checkImageViewScaleType();
            imageView.setImageMatrix(matrix);
            if(null != this.mMatrixChangeListener) {
                RectF displayRect = this.getDisplayRect(matrix);
                if(null != displayRect) {
                    this.mMatrixChangeListener.onMatrixChanged(displayRect);
                }
            }
        }

    }

    private void updateBaseMatrix(Drawable d) {
        ImageView imageView = this.getImageView();
        if(null != imageView && null != d) {
            float viewWidth = (float)this.getImageViewWidth(imageView);
            float viewHeight = (float)this.getImageViewHeight(imageView);
            int drawableWidth = d.getIntrinsicWidth();
            int drawableHeight = d.getIntrinsicHeight();
            this.mBaseMatrix.reset();
            float widthScale = viewWidth / (float)drawableWidth;
            float heightScale = viewHeight / (float)drawableHeight;
            if(this.mScaleType == ImageView.ScaleType.CENTER) {
                this.mBaseMatrix.postTranslate((viewWidth - (float)drawableWidth) / 2.0F, (viewHeight - (float)drawableHeight) / 2.0F);
            } else {
                float mTempSrc;
                if(this.mScaleType == ImageView.ScaleType.CENTER_CROP) {
                    mTempSrc = Math.max(widthScale, heightScale);
                    this.mBaseMatrix.postScale(mTempSrc, mTempSrc);
                    this.mBaseMatrix.postTranslate((viewWidth - (float)drawableWidth * mTempSrc) / 2.0F, (viewHeight - (float)drawableHeight * mTempSrc) / 2.0F);
                } else if(this.mScaleType == ImageView.ScaleType.CENTER_INSIDE) {
                    mTempSrc = Math.min(1.0F, Math.min(widthScale, heightScale));
                    this.mBaseMatrix.postScale(mTempSrc, mTempSrc);
                    this.mBaseMatrix.postTranslate((viewWidth - (float)drawableWidth * mTempSrc) / 2.0F, (viewHeight - (float)drawableHeight * mTempSrc) / 2.0F);
                } else {
                    RectF mTempSrc1 = new RectF(0.0F, 0.0F, (float)drawableWidth, (float)drawableHeight);
                    RectF mTempDst = new RectF(0.0F, 0.0F, viewWidth, viewHeight);
                    if((int)this.mBaseRotation % 180 != 0) {
                        mTempSrc1 = new RectF(0.0F, 0.0F, (float)drawableHeight, (float)drawableWidth);
                    }

                    switch(ImageView.ScaleType.values()[this.mScaleType.ordinal()]) {
                        case FIT_START:
                            this.mBaseMatrix.setRectToRect(mTempSrc1, mTempDst, Matrix.ScaleToFit.START);
                            break;
                        case FIT_CENTER:
                            this.mBaseMatrix.setRectToRect(mTempSrc1, mTempDst, Matrix.ScaleToFit.END);
                            break;
                        case FIT_END:
                            this.mBaseMatrix.setRectToRect(mTempSrc1, mTempDst, Matrix.ScaleToFit.CENTER);
                            break;
                        case CENTER:
                            this.mBaseMatrix.setRectToRect(mTempSrc1, mTempDst, Matrix.ScaleToFit.FILL);
                    }
                }
            }

            this.resetMatrix();
        }
    }

    private int getImageViewWidth(ImageView imageView) {
        return null == imageView?0:imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
    }

    private int getImageViewHeight(ImageView imageView) {
        return null == imageView?0:imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
    }

    private class FlingRunnable implements Runnable {
        private final ScrollerProxy mScroller;
        private int mCurrentX;
        private int mCurrentY;

        public FlingRunnable(Context context) {
            this.mScroller = ScrollerProxy.getScroller(context);
        }

        public void cancelFling() {
            if(PhotoViewAttacher.DEBUG) {
                LogManager.getLogger().d("PhotoViewAttacher", "Cancel Fling");
            }

            this.mScroller.forceFinished(true);
        }

        public void fling(int viewWidth, int viewHeight, int velocityX, int velocityY) {
            RectF rect = PhotoViewAttacher.this.getDisplayRect();
            if(null != rect) {
                int startX = Math.round(-rect.left);
                int minX;
                int maxX;
                if((float)viewWidth < rect.width()) {
                    minX = 0;
                    maxX = Math.round(rect.width() - (float)viewWidth);
                } else {
                    maxX = startX;
                    minX = startX;
                }

                int startY = Math.round(-rect.top);
                int minY;
                int maxY;
                if((float)viewHeight < rect.height()) {
                    minY = 0;
                    maxY = Math.round(rect.height() - (float)viewHeight);
                } else {
                    maxY = startY;
                    minY = startY;
                }

                this.mCurrentX = startX;
                this.mCurrentY = startY;
                if(PhotoViewAttacher.DEBUG) {
                    LogManager.getLogger().d("PhotoViewAttacher", "fling. StartX:" + startX + " StartY:" + startY + " MaxX:" + maxX + " MaxY:" + maxY);
                }

                if(startX != maxX || startY != maxY) {
                    this.mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, 0, 0);
                }

            }
        }

        public void run() {
            if(!this.mScroller.isFinished()) {
                ImageView imageView = PhotoViewAttacher.this.getImageView();
                if(null != imageView && this.mScroller.computeScrollOffset()) {
                    int newX = this.mScroller.getCurrX();
                    int newY = this.mScroller.getCurrY();
                    if(PhotoViewAttacher.DEBUG) {
                        LogManager.getLogger().d("PhotoViewAttacher", "fling run(). CurrentX:" + this.mCurrentX + " CurrentY:" + this.mCurrentY + " NewX:" + newX + " NewY:" + newY);
                    }

                    PhotoViewAttacher.this.mSuppMatrix.postTranslate((float)(this.mCurrentX - newX), (float)(this.mCurrentY - newY));
                    PhotoViewAttacher.this.setImageViewMatrix(PhotoViewAttacher.this.getDrawMatrix());
                    this.mCurrentX = newX;
                    this.mCurrentY = newY;
                    Compat.postOnAnimation(imageView, this);
                }

            }
        }
    }

    private class AnimatedZoomRunnable implements Runnable {
        private final float mFocalX;
        private final float mFocalY;
        private final long mStartTime;
        private final float mZoomStart;
        private final float mZoomEnd;

        public AnimatedZoomRunnable(float currentZoom, float targetZoom, float focalX, float focalY) {
            this.mFocalX = focalX;
            this.mFocalY = focalY;
            this.mStartTime = System.currentTimeMillis();
            this.mZoomStart = currentZoom;
            this.mZoomEnd = targetZoom;
        }

        public void run() {
            ImageView imageView = PhotoViewAttacher.this.getImageView();
            if(imageView != null) {
                float t = this.interpolate();
                float scale = this.mZoomStart + t * (this.mZoomEnd - this.mZoomStart);
                float deltaScale = scale / PhotoViewAttacher.this.getScale();
                PhotoViewAttacher.this.onScale(deltaScale, this.mFocalX, this.mFocalY);
                if(t < 1.0F) {
                    Compat.postOnAnimation(imageView, this);
                }

            }
        }

        private float interpolate() {
            float t = 1.0F * (float)(System.currentTimeMillis() - this.mStartTime) / (float)PhotoViewAttacher.this.ZOOM_DURATION;
            t = Math.min(1.0F, t);
            t = PhotoViewAttacher.sInterpolator.getInterpolation(t);
            return t;
        }
    }

    public interface OnSingleFlingListener {
        boolean onFling(MotionEvent var1, MotionEvent var2, float var3, float var4);
    }

    public interface OnViewTapListener {
        void onViewTap(View var1, float var2, float var3);
    }

    public interface OnPhotoTapListener {
        void onPhotoTap(View var1, float var2, float var3);

        void onOutsidePhotoTap();
    }

    public interface OnScaleChangeListener {
        void onScaleChange(float var1, float var2, float var3);
    }

    public interface OnMatrixChangedListener {
        void onMatrixChanged(RectF var1);
    }
}
