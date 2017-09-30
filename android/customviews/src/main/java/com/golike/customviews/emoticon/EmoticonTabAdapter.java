package com.golike.customviews.emoticon;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.golike.customviews.R;
import com.golike.customviews.utilities.RongUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by admin on 2017/8/8.
 */

public class EmoticonTabAdapter {
    private View mContainer;
    private IEmoticonTab mCurrentTab;
    private ViewPager mViewPager;
    private EmoticonTabAdapter.TabPagerAdapter mAdapter;
    private ViewGroup mScrollTab;
    private int selected = 0;
    private View mTabAdd;
    private boolean mTabBarEnabled = true;
    private boolean mInitialized;
    private boolean mAddEnabled = false;
    private IEmoticonClickListener mEmoticonClickListener;
    private LinkedHashMap<String, List<IEmoticonTab>> mEmotionTabs = new LinkedHashMap();
    private View.OnClickListener tabClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            int count = EmoticonTabAdapter.this.mScrollTab.getChildCount();
            if(count > 0) {
                for(int i = 0; i < count; ++i) {
                    if(v.equals(EmoticonTabAdapter.this.mScrollTab.getChildAt(i))) {
                        EmoticonTabAdapter.this.mViewPager.setCurrentItem(i);
                        break;
                    }
                }
            }

        }
    };

    public EmoticonTabAdapter() {
    }

    public boolean isInitialized() {
        return this.mInitialized;
    }

    public void setOnEmoticonClickListener(IEmoticonClickListener listener) {
        this.mEmoticonClickListener = listener;
    }

    public void setCurrentTab(IEmoticonTab tab, String tag) {
        if(this.mEmotionTabs.containsKey(tag)) {
            this.mCurrentTab = tab;
            if(this.mAdapter != null && this.mViewPager != null) {
                int index = this.getIndex(tab);
                if(index >= 0) {
                    this.mViewPager.setCurrentItem(index);
                    this.mCurrentTab = null;
                }
            }
        }

    }

    public void bindView(ViewGroup viewGroup) {
        this.mInitialized = true;
        this.mContainer = this.initView(viewGroup.getContext(), viewGroup);
    }

    public void initTabs(List<IEmoticonTab> tabs, String tag) {
        this.mEmotionTabs.put(tag, tabs);
    }

    public void refreshTabIcon(IEmoticonTab tab, Drawable drawable) {
        int index = this.getIndex(tab);
        if(index >= 0) {
            View child = this.mScrollTab.getChildAt(index);
            ImageView iv = (ImageView)child.findViewById(R.id.rc_emoticon_tab_iv);
            iv.setImageDrawable(drawable);
        }

    }

    public boolean addTab(int index, IEmoticonTab tab, String tag) {
        List tabs = (List)this.mEmotionTabs.get(tag);
        int idx;
        if(tabs == null) {
            ArrayList tabs1 = new ArrayList();
            tabs1.add(tab);
            this.mEmotionTabs.put(tag, tabs1);
        } else {
            idx = tabs.size();
            if(index > idx) {
                return false;
            }

            tabs.add(index, tab);
        }

        idx = this.getIndex(tab);
        if(this.mAdapter != null && this.mViewPager != null) {
            View view = this.getTabIcon(this.mViewPager.getContext(), tab);
            this.mScrollTab.addView(view, idx);
            this.mAdapter.notifyDataSetChanged();
            this.mViewPager.setCurrentItem(idx <= this.selected?this.selected + 1:this.selected);
        }

        return true;
    }

    public void addTab(IEmoticonTab tab, String tag) {
        List tabs = (List)this.mEmotionTabs.get(tag);
        if(tabs == null) {
            ArrayList tabs1 = new ArrayList();
            tabs1.add(tab);
            this.mEmotionTabs.put(tag, tabs1);
        } else {
            tabs.add(tab);
        }

        int idx = this.getIndex(tab);
        if(this.mAdapter != null && this.mViewPager != null) {
            View view = this.getTabIcon(this.mViewPager.getContext(), tab);
            this.mScrollTab.addView(view, idx);
            this.mAdapter.notifyDataSetChanged();
            this.mViewPager.setCurrentItem(idx <= this.selected?this.selected + 1:this.selected);
        }

    }

    public List<IEmoticonTab> getTagTabs(String tag) {
        return (List)this.mEmotionTabs.get(tag);
    }

    public int getTagTabIndex(String tag) {
        Set keys = this.mEmotionTabs.keySet();
        ArrayList list = new ArrayList();
        list.addAll(keys);
        return list.indexOf(tag);
    }

    private int getIndex(IEmoticonTab tab) {
        return this.getAllTabs().indexOf(tab);
    }

    private List<IEmoticonTab> getAllTabs() {
        Collection c = this.mEmotionTabs.values();
        ArrayList list = new ArrayList();
        Iterator i$ = c.iterator();

        while(i$.hasNext()) {
            List tabs = (List)i$.next();

            for(int i = 0; tabs != null && i < tabs.size(); ++i) {
                list.add(tabs.get(i));
            }
        }

        return list;
    }

    private IEmoticonTab getTab(int index) {
        return (IEmoticonTab)this.getAllTabs().get(index);
    }

    public boolean removeTab(IEmoticonTab tab, String tag) {
        if(!this.mEmotionTabs.containsKey(tag)) {
            return false;
        } else {
            boolean result = false;
            List list = (List)this.mEmotionTabs.get(tag);
            int index = this.getIndex(tab);
            if(list.remove(tab)) {
                this.mScrollTab.removeViewAt(index);
                this.mAdapter.notifyDataSetChanged();
                result = true;
                if(this.selected == index) {
                    this.mViewPager.setCurrentItem(this.selected);
                    this.onPageChanged(-1, this.selected);
                }
            }

            return result;
        }
    }

    public void setVisibility(int visibility) {
        if(this.mContainer != null) {
            if(visibility == View.VISIBLE) {
                this.mContainer.setVisibility(View.VISIBLE);
            } else {
                this.mContainer.setVisibility(View.GONE);
            }
        }

    }

    public int getVisibility() {
        return this.mContainer != null?this.mContainer.getVisibility():8;
    }

    public void setTabViewEnable(boolean enable) {
        this.mTabBarEnabled = enable;
    }

    public void setAddEnable(boolean enable) {
        this.mAddEnabled = enable;
        if(this.mTabAdd != null) {
            this.mTabAdd.setVisibility(enable? View.VISIBLE:View.GONE);
        }

    }

    private View initView(Context context, ViewGroup parent) {
        View container = LayoutInflater.from(context).inflate(R.layout.ee_ext_emoticon_tab_container, (ViewGroup)null);
        Integer height = Integer.valueOf((int)context.getResources().getDimension(R.dimen.rc_extension_board_height));
        container.setLayoutParams(new RelativeLayout.LayoutParams(-1, height.intValue()));
        this.mViewPager = (ViewPager)container.findViewById(R.id.rc_view_pager);
        this.mScrollTab = (ViewGroup)container.findViewById(R.id.rc_emotion_scroll_tab);
        View tabBar = container.findViewById(R.id.rc_emotion_tab_bar);
        if(this.mTabBarEnabled) {
            tabBar.setVisibility(View.VISIBLE);
        } else {
            tabBar.setVisibility(View.GONE);
        }

        this.mTabAdd = container.findViewById(R.id.rc_emoticon_tab_add);
        this.mTabAdd.setVisibility(this.mAddEnabled? View.VISIBLE:View.GONE);
        this.mTabAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(EmoticonTabAdapter.this.mEmoticonClickListener != null) {
                    EmoticonTabAdapter.this.mEmoticonClickListener.onAddClick(v);
                }

            }
        });
        Iterator index = this.getAllTabs().iterator();

        while(index.hasNext()) {
            IEmoticonTab tab = (IEmoticonTab)index.next();
            View view = this.getTabIcon(context, tab);
            this.mScrollTab.addView(view);
        }

        this.mAdapter = new EmoticonTabAdapter.TabPagerAdapter();
        this.mViewPager.setAdapter(this.mAdapter);
        this.mViewPager.setOffscreenPageLimit(6);
        this.mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                EmoticonTabAdapter.this.onPageChanged(EmoticonTabAdapter.this.selected, position);
                EmoticonTabAdapter.this.selected = position;
            }

            public void onPageScrollStateChanged(int state) {
            }
        });
        int index1;
        if(this.mCurrentTab != null && (index1 = this.getIndex(this.mCurrentTab)) >= 0) {
            this.mCurrentTab = null;
            this.onPageChanged(-1, index1);
            this.mViewPager.setCurrentItem(index1);
        } else {
            this.onPageChanged(-1, 0);
        }

        parent.addView(container);
        return container;
    }

    private View getTabIcon(Context context, IEmoticonTab tab) {
        Drawable drawable = tab.obtainTabDrawable(context);
        View item = LayoutInflater.from(context).inflate(R.layout.ee_ext_emoticon_tab_item, (ViewGroup)null);
        item.setLayoutParams(new RelativeLayout.LayoutParams(RongUtils.dip2px(60.0F), RongUtils.dip2px(36.0F)));
        ImageView iv = (ImageView)item.findViewById(R.id.rc_emoticon_tab_iv);
        iv.setImageDrawable(drawable);
        item.setOnClickListener(this.tabClickListener);
        return item;
    }

    private void onPageChanged(int pre, int cur) {
        int count = this.mScrollTab.getChildCount();
        if(count > 0 && cur < count) {
            ViewGroup curTab;
            if(pre >= 0 && pre < count) {
                curTab = (ViewGroup)this.mScrollTab.getChildAt(pre);
                curTab.setBackgroundColor(0);
            }

            if(cur >= 0) {
                curTab = (ViewGroup)this.mScrollTab.getChildAt(cur);
                curTab.setBackgroundColor(Color.rgb(215, 215, 215));
                int w = curTab.getMeasuredWidth();
                if(w != 0) {
                    int screenW = RongUtils.getScreenWidth();
                    if(this.mAddEnabled) {
                        int scrollView = this.mTabAdd.getMeasuredWidth();
                        screenW -= scrollView;
                    }

                    HorizontalScrollView scrollView1 = (HorizontalScrollView)this.mScrollTab.getParent();
                    int scrollX = scrollView1.getScrollX();
                    int offset = scrollX - scrollX / w * w;
                    if(cur * w < scrollX) {
                        scrollView1.smoothScrollBy(offset == 0?-w:-offset, 0);
                    } else if(cur * w - scrollX > screenW - w) {
                        scrollView1.smoothScrollBy(w - offset, 0);
                    }
                }
            }
        }

        if(cur >= 0 && cur < count) {
            IEmoticonTab curTab1 = this.getTab(cur);
            if(curTab1 != null) {
                curTab1.onTableSelected(cur);
            }
        }

    }

    private class TabPagerAdapter extends PagerAdapter {
        private TabPagerAdapter() {
        }

        public int getCount() {
            return EmoticonTabAdapter.this.getAllTabs().size();
        }

        public View instantiateItem(ViewGroup container, int position) {
            IEmoticonTab tab = EmoticonTabAdapter.this.getTab(position);
            View view = tab.obtainTabPager(container.getContext());
            if(view.getParent() == null) {
                container.addView(view);
            }

            return view;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            View layout = (View)object;
            container.removeView(layout);
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public int getItemPosition(Object object) {
            return -2;
        }
    }
}
