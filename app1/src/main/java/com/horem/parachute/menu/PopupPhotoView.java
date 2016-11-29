package com.horem.parachute.menu;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.horem.parachute.R;
import com.horem.parachute.adapter.HackyViewPagerAdapter;
import com.horem.parachute.adapter.ShowMessageAdapter;
import com.horem.parachute.adapter.ShowMessagePopupAdapter;
import com.horem.parachute.customview.HackyViewPager;
import com.horem.parachute.util.ScreenBean;

import java.util.HashMap;
import java.util.List;

/**
 * Created by yuanyukun on 2016/5/26.
 */
public class PopupPhotoView extends PopupWindow {

    private Context context;
    private List<String> lists;
    private int position;
    public PopupPhotoView(Context context, List<String> lists,int position) {
        super(context);
        this.lists = lists;
        this.context = context;
        this.position = position;
        init();
    }

    private void init() {

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.show_message_popview,null);
        HackyViewPager viewPager = (HackyViewPager) view.findViewById(R.id.viewpager_show_message);
        ShowMessagePopupAdapter adapter = new ShowMessagePopupAdapter(lists,context);
        viewPager.setAdapter(adapter);
//        viewPager.setCurrentItem(positon);
        viewPager.setCurrentItem(position,true);

        setContentView(view);
        setWidth(ScreenBean.getScreenWidth());
        setHeight(ScreenBean.getScreenHeight());
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchable(true);
        setOutsideTouchable(true);
        setAnimationStyle(R.style.PopDownMenu);

    }
}
