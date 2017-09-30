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

    private final Runnable measureAndLayout = new Runnable() {
        @Override
        public void run() {
            measure(
                    MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
            layout(getLeft(), getTop(), getRight(), getBottom());
        }
    };

    @Override
    public void requestLayout() {
        super.requestLayout();

        // The spinner relies on a measure + layout pass happening after it calls requestLayout().
        // Without this, the widget never actually changes the selection and doesn't call the
        // appropriate listeners. Since we override onLayout in our ViewGroups, a layout pass never
        // happens after a call to requestLayout, so we simulate one here.
        post(measureAndLayout);
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
        this.refreshHeader = (ViewGroup) View.inflate(context, R.layout.ee_refresh_list_view, null);
        this.addHeaderView(this.refreshHeader, null, false);
    }

    private void initRefreshListener() {
        OnScrollListener listener = new OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //&& AutoRefreshListView.this.state == AutoRefreshListView.State.RESET
                if(scrollState == 0 ) {
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
            //第一个消息item
            View firstVisibleChild = this.getChildAt(this.getHeaderViewsCount());
            if(firstVisibleChild != null) {
                //获取其在ViewGroup距离顶部的高度
                this.offsetY = firstVisibleChild.getTop();
            }
            //开始，开始刷新，模式不是结束
            if(start && this.refreshableStart && this.mode != AutoRefreshListView.Mode.END) {
                //设置当前模式--开始
                this.currentMode = AutoRefreshListView.Mode.START;
                //设置当前状态--正在刷新
                this.state = AutoRefreshListView.State.REFRESHING;
                //调用外部接口方法
                this.refreshListener.onRefreshFromStart();
            } else
            //刷新结束，模式不是开始
            if(this.refreshableEnd && this.mode != AutoRefreshListView.Mode.START) {
                //设置当前模式--结束
                this.currentMode = AutoRefreshListView.Mode.END;
                //设置当前状态--正在刷新
                this.state = AutoRefreshListView.State.REFRESHING;
                //调用外部接口方法
                this.refreshListener.onRefreshFromEnd();
            }
            this.updateRefreshView();
        }

    }

    private void updateRefreshView() {
        switch(this.state) {
            case REFRESHING:
                this.getRefreshView().getChildAt(0).setVisibility(VISIBLE);
                break;
            case RESET:
                if(this.currentMode == AutoRefreshListView.Mode.START) {
                    this.refreshHeader.getChildAt(0).setVisibility(GONE);
                }
        }

    }

    private ViewGroup getRefreshView() {
        switch(this.currentMode) {
            case  START:
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
            case MotionEvent.ACTION_DOWN:
                this.onTouchBegin(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                this.onTouchEnd();
                break;
            case MotionEvent.ACTION_MOVE:
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
        //开始下拉刷新
        void onRefreshFromStart();
        //结束下拉刷新
        void onRefreshFromEnd();
    }

    public enum Mode {
        START,
        END,
        BOTH;

        Mode() {
        }
    }

    public enum State {
        REFRESHING,
        RESET;

        State() {
        }
    }
}

