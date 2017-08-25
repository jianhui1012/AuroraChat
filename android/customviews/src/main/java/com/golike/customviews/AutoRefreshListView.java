package com.golike.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by admin on 2017/8/8.
 */
public class AutoRefreshListView extends ListView {
    private AutoRefreshListView.OnRefreshListener refreshListener;
    private List<OnScrollListener> scrollListeners = new ArrayList();
    private AutoRefreshListView.State state;
    private AutoRefreshListView.Mode mode;
    private AutoRefreshListView.Mode currentMode;
    private boolean refreshableStart;
    private boolean refreshableEnd;
    private ViewGroup refreshHeader;
    private ViewGroup refreshFooter;
    private int offsetY;
    private boolean isOnMeasure;
    private boolean isBeingDragged;
    private int startY;

    public AutoRefreshListView(Context context) {
        super(context);
        this.state = AutoRefreshListView.State.RESET;
        this.mode = AutoRefreshListView.Mode.START;
        this.currentMode = AutoRefreshListView.Mode.START;
        this.refreshableStart = true;
        this.refreshableEnd = true;
        this.isOnMeasure = true;
        this.isBeingDragged = false;
        this.startY = 0;
        this.init(context);
    }

    public AutoRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.state = AutoRefreshListView.State.RESET;
        this.mode = AutoRefreshListView.Mode.START;
        this.currentMode = AutoRefreshListView.Mode.START;
        this.refreshableStart = true;
        this.refreshableEnd = true;
        this.isOnMeasure = true;
        this.isBeingDragged = false;
        this.startY = 0;
        this.init(context);
    }

    public AutoRefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.state = AutoRefreshListView.State.RESET;
        this.mode = AutoRefreshListView.Mode.START;
        this.currentMode = AutoRefreshListView.Mode.START;
        this.refreshableStart = true;
        this.refreshableEnd = true;
        this.isOnMeasure = true;
        this.isBeingDragged = false;
        this.startY = 0;
        this.init(context);
    }

    public void setMode(AutoRefreshListView.Mode mode) {
        this.mode = mode;
    }

    public void setOnRefreshListener(AutoRefreshListView.OnRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    public void setOnScrollListener(OnScrollListener l) {
        throw new UnsupportedOperationException("Use addOnScrollListener instead!");
    }

    public void addOnScrollListener(OnScrollListener l) {
        this.scrollListeners.add(l);
    }

    public void removeOnScrollListener(OnScrollListener l) {
        this.scrollListeners.remove(l);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.isOnMeasure = true;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        this.isOnMeasure = false;
        super.onLayout(changed, l, t, r, b);
    }

    public boolean isOnMeasure() {
        return this.isOnMeasure;
    }

    private void init(Context context) {
        this.addRefreshView(context);
        super.setOnScrollListener(new OnScrollListener() {
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Iterator i$ = AutoRefreshListView.this.scrollListeners.iterator();

                while(i$.hasNext()) {
                    OnScrollListener listener = (OnScrollListener)i$.next();
                    listener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }

            }

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Iterator i$ = AutoRefreshListView.this.scrollListeners.iterator();

                while(i$.hasNext()) {
                    OnScrollListener listener = (OnScrollListener)i$.next();
                    listener.onScrollStateChanged(view, scrollState);
                }

            }
        });
        this.initRefreshListener();
        this.state = AutoRefreshListView.State.RESET;
    }

    private void addRefreshView(Context context) {
        this.refreshHeader = (ViewGroup) View.inflate(context, R.layout.rc_refresh_list_view, (ViewGroup)null);
        this.addHeaderView(this.refreshHeader, (Object)null, false);
    }

    private void initRefreshListener() {
        OnScrollListener listener = new OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == 0 && AutoRefreshListView.this.state == AutoRefreshListView.State.RESET) {
                    boolean reachTop = AutoRefreshListView.this.getFirstVisiblePosition() < AutoRefreshListView.this.getHeaderViewsCount() && AutoRefreshListView.this.getCount() > AutoRefreshListView.this.getHeaderViewsCount();
                    if(reachTop) {
                        AutoRefreshListView.this.onRefresh(true);
                    } else {
                        boolean reachBottom = AutoRefreshListView.this.getLastVisiblePosition() >= AutoRefreshListView.this.getCount() - 1;
                        if(reachBottom) {
                            AutoRefreshListView.this.onRefresh(false);
                        }
                    }
                }

            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        };
        this.addOnScrollListener(listener);
    }

    private void onRefresh(boolean start) {
        if(this.refreshListener != null) {
            View firstVisibleChild = this.getChildAt(this.getHeaderViewsCount());
            if(firstVisibleChild != null) {
                this.offsetY = firstVisibleChild.getTop();
            }

            if(start && this.refreshableStart && this.mode != AutoRefreshListView.Mode.END) {
                this.currentMode = AutoRefreshListView.Mode.START;
                this.state = AutoRefreshListView.State.REFRESHING;
                this.refreshListener.onRefreshFromStart();
            } else if(this.refreshableEnd && this.mode != AutoRefreshListView.Mode.START) {
                this.currentMode = AutoRefreshListView.Mode.END;
                this.state = AutoRefreshListView.State.REFRESHING;
                this.refreshListener.onRefreshFromEnd();
            }

            this.updateRefreshView();
        }

    }

    private void updateRefreshView() {

        switch(AutoRefreshListView.State.values()[this.state.ordinal()].ordinal()) {
            case 1:
                this.getRefreshView().getChildAt(0).setVisibility(VISIBLE);
                break;
            case 2:
                if(this.currentMode == AutoRefreshListView.Mode.START) {
                    this.refreshHeader.getChildAt(0).setVisibility(GONE);
                }
        }

    }

    private ViewGroup getRefreshView() {
        switch(AutoRefreshListView.Mode.values()[this.currentMode.ordinal()].ordinal()) {
            case 1:
            default:
                return this.refreshHeader;
        }
    }

    public AutoRefreshListView.State getRefreshState() {
        return this.state;
    }

    public void onRefreshStart(AutoRefreshListView.Mode mode) {
        this.state = AutoRefreshListView.State.REFRESHING;
        this.currentMode = mode;
    }

    public void onRefreshComplete(int count, int requestCount, boolean needOffset) {
        this.state = AutoRefreshListView.State.RESET;
        this.resetRefreshView(count, requestCount);
    }

    private void resetRefreshView(int count, int requestCount) {
        if(this.currentMode == AutoRefreshListView.Mode.START) {
            if(this.getCount() == count + this.getHeaderViewsCount() + this.getFooterViewsCount()) {
                this.refreshableStart = count == requestCount;
            } else {
                this.refreshableStart = count > 0;
            }
        } else {
            this.refreshableEnd = count > 0;
        }

        this.updateRefreshView();
    }

    public boolean onTouchEvent(MotionEvent event) {
        try {
            return this.onTouchEventInternal(event);
        } catch (ArrayIndexOutOfBoundsException var3) {
            var3.printStackTrace();
            return false;
        }
    }

    private boolean onTouchEventInternal(MotionEvent event) {
        switch(event.getAction()) {
            case 0:
                this.onTouchBegin(event);
                break;
            case 1:
            case 3:
                this.onTouchEnd();
                break;
            case 2:
                this.onTouchMove(event);
        }

        return super.onTouchEvent(event);
    }

    private void onTouchBegin(MotionEvent event) {
        int firstItemIndex = this.getFirstVisiblePosition();
        if(!this.refreshableStart && firstItemIndex <= this.getHeaderViewsCount() && !this.isBeingDragged) {
            this.isBeingDragged = true;
            this.startY = (int)event.getY();
        }

    }

    private void onTouchMove(MotionEvent event) {
        this.onTouchBegin(event);
        if(this.isBeingDragged) {
            int offsetY = (int)(event.getY() - (float)this.startY);
            offsetY = Math.max(offsetY, 0) / 2;
            this.refreshHeader.setPadding(0, offsetY, 0, 0);
        }
    }

    private void onTouchEnd() {
        if(this.isBeingDragged) {
            this.refreshHeader.setPadding(0, 0, 0, 0);
        }

        this.isBeingDragged = false;
    }

    public interface OnRefreshListener {
        void onRefreshFromStart();

        void onRefreshFromEnd();
    }

    public static enum Mode {
        START,
        END,
        BOTH;

        private Mode() {
        }
    }

    public static enum State {
        REFRESHING,
        RESET;

        private State() {
        }
    }
}

