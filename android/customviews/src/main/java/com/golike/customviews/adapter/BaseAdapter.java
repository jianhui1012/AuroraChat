package com.golike.customviews.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by admin on 2017/8/14.
 */

public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {

    protected Context mContext;
    protected List<T> mList;

    public BaseAdapter() {
        this.mList = new ArrayList();
    }

    public BaseAdapter(Context context) {
        this.mContext = context;
        this.mList = new ArrayList();
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return this.mList == null ? null : (position >= this.mList.size() ? null : this.mList.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    protected  <T extends View> T findViewById(View view,int id){
        return (T) view.findViewById(id);
    }

    public int findPosition(T message) {
        int index = this.getCount();
        int position = -1;

        while(index-- > 0) {
            if(message.equals(this.getItem(index))) {
                position = index;
                break;
            }
        }

        return position;
    }

    public int findPosition(long id) {
        int index = this.getCount();
        int position = -1;

        while(index-- > 0) {
            if(this.getItemId(index) == id) {
                position = index;
                break;
            }
        }

        return position;
    }

    public void addCollection(Collection<T> collection) {
        this.mList.addAll(collection);
    }

    public void addCollection(T... collection) {
        Object[] arr$ = collection;
        int len$ = collection.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Object t = arr$[i$];
            this.mList.add((T) t);
        }

    }

    public void add(T t) {
        this.mList.add(t);
    }

    public void add(T t, int position) {
        this.mList.add(position, t);
    }

    public void remove(int position) {
        this.mList.remove(position);
    }

    public void removeAll() {
        this.mList.clear();
    }

    public void clear() {
        this.mList.clear();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            view = this.newView(mContext, position, parent);
        }
        this.bindView(view, position, this.getItem(position));
        return view;
    }

    protected abstract View newView(Context context, int pos, ViewGroup parent);

    protected abstract void bindView(View convertView, int pos, T t);
}
