package com.golike.customviews.emoticon;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.golike.customviews.R;
import com.golike.customviews.utilities.ExtensionHistoryUtil;

/**
 * Created by admin on 2017/8/8.
 */

public class EmojiTab implements IEmoticonTab {
    private LayoutInflater mLayoutInflater;
    private LinearLayout mIndicator;
    private int selected = 0;
    private String mUserId;
    private IEmojiItemClickListener mOnItemClickListener;
    private int mEmojiCountPerPage;

    public EmojiTab() {
    }

    public void setOnItemClickListener(IEmojiItemClickListener clickListener) {
        this.mOnItemClickListener = clickListener;
    }

    public Drawable obtainTabDrawable(Context context) {
        return context.getResources().getDrawable(R.drawable.rc_tab_emoji);
    }

    public View obtainTabPager(Context context) {
        //this.mUserId = RongIMClient.getInstance().getCurrentUserId();
        return this.initView(context);
    }

    public void onTableSelected(int position) {
    }

    private View initView(final Context context) {
        int count = AndroidEmoji.getEmojiSize();

        try {
            this.mEmojiCountPerPage = context.getResources().getInteger(context.getResources().getIdentifier("rc_extension_emoji_count_per_page", "integer", context.getPackageName()));
        } catch (Exception var7) {
            this.mEmojiCountPerPage = 20;
        }

        int pages = count / this.mEmojiCountPerPage + (count % this.mEmojiCountPerPage != 0?1:0);
        View view = LayoutInflater.from(context).inflate(R.layout.ee_ext_emoji_pager, (ViewGroup)null);
        ViewPager viewPager = (ViewPager)view.findViewById(R.id.rc_view_pager);
        this.mIndicator = (LinearLayout)view.findViewById(R.id.rc_indicator);
        this.mLayoutInflater = LayoutInflater.from(context);
        viewPager.setAdapter(new EmojiTab.EmojiPagerAdapter(pages));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                ExtensionHistoryUtil.setEmojiPosition(context, EmojiTab.this.mUserId, position);
                EmojiTab.this.onIndicatorChanged(EmojiTab.this.selected, position);
                EmojiTab.this.selected = position;
            }

            public void onPageScrollStateChanged(int state) {
            }
        });
        viewPager.setOffscreenPageLimit(1);
        this.initIndicator(pages, this.mIndicator);
        int position = ExtensionHistoryUtil.getEmojiPosition(context, this.mUserId);
        viewPager.setCurrentItem(position);
        this.onIndicatorChanged(-1, position);
        return view;
    }

    private void initIndicator(int pages, LinearLayout indicator) {
        for(int i = 0; i < pages; ++i) {
            ImageView imageView = (ImageView)this.mLayoutInflater.inflate(R.layout.ee_ext_indicator, (ViewGroup)null);
            imageView.setImageResource(R.drawable.rc_ext_indicator);
            indicator.addView(imageView);
        }

    }

    private void onIndicatorChanged(int pre, int cur) {
        int count = this.mIndicator.getChildCount();
        if(count > 0 && pre < count && cur < count) {
            ImageView curView;
            if(pre >= 0) {
                curView = (ImageView)this.mIndicator.getChildAt(pre);
                curView.setImageResource(R.drawable.rc_ext_indicator);
            }

            if(cur >= 0) {
                curView = (ImageView)this.mIndicator.getChildAt(cur);
                curView.setImageResource(R.drawable.rc_ext_indicator_hover);
            }
        }

    }

    private class ViewHolder {
        ImageView emojiIV;

        private ViewHolder() {
        }
    }

    private class EmojiAdapter extends BaseAdapter {
        int count;
        int index;

        public EmojiAdapter(int index, int count) {
            this.count = Math.min(EmojiTab.this.mEmojiCountPerPage, count - index);
            this.index = index;
        }

        public int getCount() {
            return this.count + 1;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0L;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            EmojiTab.ViewHolder viewHolder;
            if(convertView == null) {
                viewHolder = EmojiTab.this.new ViewHolder();
                convertView = EmojiTab.this.mLayoutInflater.inflate(R.layout.ee_ext_emoji_item, (ViewGroup)null);
                viewHolder.emojiIV = (ImageView)convertView.findViewById(R.id.rc_ext_emoji_item);
                convertView.setTag(viewHolder);
            }

            viewHolder = (EmojiTab.ViewHolder)convertView.getTag();
            if(position != EmojiTab.this.mEmojiCountPerPage && position + this.index != AndroidEmoji.getEmojiSize()) {
                viewHolder.emojiIV.setImageDrawable(AndroidEmoji.getEmojiDrawable(parent.getContext(), this.index + position));
            } else {
                viewHolder.emojiIV.setImageResource(R.drawable.rc_icon_emoji_delete);
            }

            return convertView;
        }
    }

    private class EmojiPagerAdapter extends PagerAdapter {
        int count;

        public EmojiPagerAdapter(int count) {
            this.count = count;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            GridView gridView = (GridView)EmojiTab.this.mLayoutInflater.inflate(R.layout.ee_ext_emoji_grid_view, (ViewGroup)null);
            gridView.setAdapter(EmojiTab.this.new EmojiAdapter(position * EmojiTab.this.mEmojiCountPerPage, AndroidEmoji.getEmojiSize()));
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(EmojiTab.this.mOnItemClickListener != null) {
                        int index = position + EmojiTab.this.selected * EmojiTab.this.mEmojiCountPerPage;
                        if(position == EmojiTab.this.mEmojiCountPerPage) {
                            EmojiTab.this.mOnItemClickListener.onDeleteClick();
                        } else if(index >= AndroidEmoji.getEmojiSize()) {
                            EmojiTab.this.mOnItemClickListener.onDeleteClick();
                        } else {
                            int code = AndroidEmoji.getEmojiCode(index);
                            char[] chars = Character.toChars(code);
                            String key = Character.toString(chars[0]);

                            for(int i = 1; i < chars.length; ++i) {
                                key = key + Character.toString(chars[i]);
                            }

                            EmojiTab.this.mOnItemClickListener.onEmojiClick(key);
                        }
                    }

                }
            });
            container.addView(gridView);
            return gridView;
        }

        public int getItemPosition(Object object) {
            return -2;
        }

        public int getCount() {
            return this.count;
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            View layout = (View)object;
            container.removeView(layout);
        }
    }
}
