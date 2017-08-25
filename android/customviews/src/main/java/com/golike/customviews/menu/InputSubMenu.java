package com.golike.customviews.menu;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.golike.customviews.R;

import java.util.List;

/**
 * Created by admin on 2017/8/9.
 */

public class InputSubMenu {
    PopupWindow mPopupWindow;
    ViewGroup container;
    LayoutInflater mInflater;
    ISubMenuItemClickListener mOnClickListener;

    public InputSubMenu(Context context, List<String> menus) {
        this.mInflater = LayoutInflater.from(context);
        this.container = (ViewGroup)this.mInflater.inflate(R.layout.rc_ext_sub_menu_container, (ViewGroup)null);
        this.mPopupWindow = new PopupWindow(this.container, -2, -2);
        this.setupSubMenus(this.container, menus);
    }

    public void showAtLocation(View parent) {
        this.mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        this.container.measure(0, 0);
        int[] location = new int[2];
        int w = this.container.getMeasuredWidth();
        parent.getLocationOnScreen(location);
        int x = location[0] + (parent.getWidth() - w) / 2;
        int y = parent.getHeight() + 10;
        this.mPopupWindow.showAtLocation(parent, 8388691, x, y);
        this.mPopupWindow.setOutsideTouchable(true);
        this.mPopupWindow.setFocusable(true);
        this.mPopupWindow.update();
    }

    public void setOnItemClickListener(ISubMenuItemClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    private void setupSubMenus(ViewGroup viewGroup, List<String> menus) {
        for(int i = 0; i < menus.size(); ++i) {
            View view = this.mInflater.inflate(R.layout.rc_ext_sub_menu_item, (ViewGroup)null);
            TextView tv = (TextView)view.findViewById(R.id.rc_sub_menu_title);
            View divider = view.findViewById(R.id.rc_sub_menu_divider_line);
            String title = (String)menus.get(i);
            tv.setText(title);
            if(i < menus.size() - 1) {
                divider.setVisibility(0);
            }

            view.setTag(Integer.valueOf(i));
            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int index = ((Integer)v.getTag()).intValue();
                    InputSubMenu.this.mOnClickListener.onClick(index);
                    InputSubMenu.this.mPopupWindow.dismiss();
                }
            });
            viewGroup.addView(view);
        }

    }
}