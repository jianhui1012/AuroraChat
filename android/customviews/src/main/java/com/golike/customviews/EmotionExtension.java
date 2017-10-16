package com.golike.customviews;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.golike.customviews.emoticon.AndroidEmoji;
import com.golike.customviews.emoticon.EmojiTab;
import com.golike.customviews.emoticon.EmoticonTabAdapter;
import com.golike.customviews.emoticon.IEmojiItemClickListener;
import com.golike.customviews.emoticon.IEmoticonClickListener;
import com.golike.customviews.emoticon.IEmoticonTab;
import com.golike.customviews.model.Conversation.ConversationType;
import com.golike.customviews.plugin.IPluginClickListener;
import com.golike.customviews.plugin.IPluginModule;
import com.golike.customviews.plugin.PluginAdapter;
import com.golike.customviews.utilities.ExtensionHistoryUtil;
import com.golike.customviews.utilities.ExtensionHistoryUtil.ExtensionBarState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by admin on 2017/8/8.
 */

public class EmotionExtension extends LinearLayout {
    private static final String TAG = "EmotionExtension";
    private Context mContext;
    private ViewGroup mExtensionBar;
    private EmoticonTabAdapter mEmotionTabAdapter;
    boolean isKeyBoardActive = false;
    private IEmojiItemClickListener mIEmojiItemClickListener;

    public EmotionExtension(Context context) {
        super(context);
        this.mContext=context;
        this.initView();
        this.initData();
    }

    public EmotionExtension(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext=context;
        this.initView();
        this.initData();
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

    public EmotionExtension(Context context, IEmojiItemClickListener iEmojiItemClickListener) {
        super(context);
        this.setIEmojiItemClickListener(iEmojiItemClickListener);
        this.mContext=context;
        this.initView();
        this.initData();
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        post(measureAndLayout);
    }


    public void setIEmojiItemClickListener(IEmojiItemClickListener iEmojiItemClickListener){
        this.mIEmojiItemClickListener=iEmojiItemClickListener;
    }


    public List<IEmoticonTab> getEmoticonTabs() {
        EmojiTab emojiTab = new EmojiTab();
        emojiTab.setOnItemClickListener(this.mIEmojiItemClickListener);
        ArrayList list = new ArrayList();
        list.add(emojiTab);
        return list;
    }

    private void initEmoticonTabs() {
        List tabs = this.getEmoticonTabs();
        this.mEmotionTabAdapter.initTabs(tabs, this.getClass().getCanonicalName());
    }


    public void refreshEmoticonTabIcon(IEmoticonTab tab, Drawable icon) {
        if (icon != null && this.mEmotionTabAdapter != null && tab != null) {
            this.mEmotionTabAdapter.refreshTabIcon(tab, icon);
        }

    }

    public boolean addEmoticonTab(int index, IEmoticonTab tab, String tag) {
        if (this.mEmotionTabAdapter != null && tab != null && !TextUtils.isEmpty(tag)) {
            return this.mEmotionTabAdapter.addTab(index, tab, tag);
        } else {
            Log.e("EditExtension", "addEmoticonTab Failure");
            return false;
        }
    }

    public void addEmoticonTab(IEmoticonTab tab, String tag) {
        if (this.mEmotionTabAdapter != null && tab != null && !TextUtils.isEmpty(tag)) {
            this.mEmotionTabAdapter.addTab(tab, tag);
        }
    }

    public List<IEmoticonTab> getEmoticonTabs(String tag) {
        return this.mEmotionTabAdapter != null && !TextUtils.isEmpty(tag) ? this.mEmotionTabAdapter.getTagTabs(tag) : null;
    }

    public int getEmoticonTabIndex(String tag) {
        return this.mEmotionTabAdapter != null && !TextUtils.isEmpty(tag) ? this.mEmotionTabAdapter.getTagTabIndex(tag) : -1;
    }

    public boolean removeEmoticonTab(IEmoticonTab tab, String tag) {
        boolean result = false;
        if (this.mEmotionTabAdapter != null && tab != null && !TextUtils.isEmpty(tag)) {
            result = this.mEmotionTabAdapter.removeTab(tab, tag);
        }

        return result;
    }

    public void setCurrentEmoticonTab(IEmoticonTab tab, String tag) {
        if (this.mEmotionTabAdapter != null && tab != null && !TextUtils.isEmpty(tag)) {
            this.mEmotionTabAdapter.setCurrentTab(tab, tag);
        }

    }

    public void setEmoticonTabBarEnable(boolean enable) {
        if (this.mEmotionTabAdapter != null) {
            this.mEmotionTabAdapter.setTabViewEnable(enable);
        }

    }

    public void setEmoticonTabBarAddEnable(boolean enable) {
        if (this.mEmotionTabAdapter != null) {
            this.mEmotionTabAdapter.setAddEnable(enable);
        }

    }

    public void setEmoticonTabBarAddClickListener(IEmoticonClickListener listener) {
        if (this.mEmotionTabAdapter != null) {
            this.mEmotionTabAdapter.setOnEmoticonClickListener(listener);
        }

    }

    private void initData() {
        this.mEmotionTabAdapter = new EmoticonTabAdapter();
        this.initEmoticonTabs();
    }

    private void initView() {
        this.setOrientation(VERTICAL);
        this.mExtensionBar = (ViewGroup) LayoutInflater.from(this.getContext()).inflate(R.layout.emotion_extension_bar,null);
        this.addView(this.mExtensionBar);
    }

    private void hideEmoticonBoard() {
        this.mEmotionTabAdapter.setVisibility(GONE);
    }

    public void setEmoticonBoard() {
        if (this.mEmotionTabAdapter.isInitialized()) {
            if (this.mEmotionTabAdapter.getVisibility() == VISIBLE) {
                this.mEmotionTabAdapter.setVisibility(GONE);
                this.showInputKeyBoard();
            } else {
                this.hideInputKeyBoard();
                this.mEmotionTabAdapter.setVisibility(VISIBLE);
            }
        } else {
            this.hideInputKeyBoard();
            this.mEmotionTabAdapter.bindView(this);
            this.mEmotionTabAdapter.setVisibility(VISIBLE);
        }
    }

    private void hideInputKeyBoard() {
        InputMethodManager imm = (InputMethodManager) this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.mExtensionBar.getWindowToken(), 0);
        this.isKeyBoardActive = false;
    }

    private void showInputKeyBoard() {
        InputMethodManager imm = (InputMethodManager) this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(this.mExtensionBar, 0);
        this.mExtensionBar.setSelected(false);
        this.isKeyBoardActive = true;
    }

    /**
     * 底部虚拟按键栏的高度
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    private boolean isSoftShowing() {
        //获取当前屏幕内容的高度
        int screenHeight = ((Activity)mContext).getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        ((Activity)mContext).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return screenHeight - rect.bottom -getSoftButtonsBarHeight()!= 0;
    }


}
