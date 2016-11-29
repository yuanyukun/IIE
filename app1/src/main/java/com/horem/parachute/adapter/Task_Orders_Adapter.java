package com.horem.parachute.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by user on 2015/9/2.
 */
public class Task_Orders_Adapter extends PagerAdapter {
    Context context;
    ArrayList<View> list = new ArrayList<View>();
    int count;

    public Task_Orders_Adapter(Context context, ArrayList<View> list) {
        super();
        this.context = context;
        this.list = list;
        count=list == null ? 0 : list.size();
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view=list.get(position);
        container.removeView(view);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = list.get(position);
        container.addView(view);
        return view;
    }
}
