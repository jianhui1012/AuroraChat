package com.golike.customviews.photoview;

import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by admin on 2017/8/31.
 */

public class DefaultOnDoubleTapListener implements GestureDetector.OnDoubleTapListener {
    private PhotoViewAttacher photoViewAttacher;

    public DefaultOnDoubleTapListener(PhotoViewAttacher photoViewAttacher) {
        this.setPhotoViewAttacher(photoViewAttacher);
    }

    public void setPhotoViewAttacher(PhotoViewAttacher newPhotoViewAttacher) {
        this.photoViewAttacher = newPhotoViewAttacher;
    }

    public boolean onSingleTapConfirmed(MotionEvent e) {
        if(this.photoViewAttacher == null) {
            return false;
        } else {
            ImageView imageView = this.photoViewAttacher.getImageView();
            if(null != this.photoViewAttacher.getOnPhotoTapListener()) {
                RectF displayRect = this.photoViewAttacher.getDisplayRect();
                if(null != displayRect) {
                    float x = e.getX();
                    float y = e.getY();
                    if(displayRect.contains(x, y)) {
                        float xResult = (x - displayRect.left) / displayRect.width();
                        float yResult = (y - displayRect.top) / displayRect.height();
                        this.photoViewAttacher.getOnPhotoTapListener().onPhotoTap(imageView, xResult, yResult);
                        return true;
                    }

                    this.photoViewAttacher.getOnPhotoTapListener().onOutsidePhotoTap();
                }
            }

            if(null != this.photoViewAttacher.getOnViewTapListener()) {
                this.photoViewAttacher.getOnViewTapListener().onViewTap(imageView, e.getX(), e.getY());
            }

            return false;
        }
    }

    public boolean onDoubleTap(MotionEvent ev) {
        if(this.photoViewAttacher == null) {
            return false;
        } else {
            try {
                float e = this.photoViewAttacher.getScale();
                float x = ev.getX();
                float y = ev.getY();
                if(e < this.photoViewAttacher.getMediumScale()) {
                    this.photoViewAttacher.setScale(this.photoViewAttacher.getMediumScale(), x, y, true);
                } else if(e >= this.photoViewAttacher.getMediumScale() && e < this.photoViewAttacher.getMaximumScale()) {
                    this.photoViewAttacher.setScale(this.photoViewAttacher.getMaximumScale(), x, y, true);
                } else {
                    this.photoViewAttacher.setScale(this.photoViewAttacher.getMinimumScale(), x, y, true);
                }
            } catch (ArrayIndexOutOfBoundsException var5) {
                ;
            }

            return true;
        }
    }

    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }
}
