package com.horem.parachute.adapter;

import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by user on 2016/3/25.
 */
public class HackyViewPagerAdapter extends PagerAdapter {

    private List mLists;


    public HackyViewPagerAdapter(List lists) {
        this.mLists = lists;
    }

    @Override
    public int getCount() {
            return mLists.size() - 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(container.getContext());
        photoView.setScaleType(ImageView.ScaleType.FIT_XY);
        photoView.setImageDrawable((Drawable) mLists.get(position));
        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.i("PoPupViewPager","长按事件触发");
                return true;
            }
        });

        // Now just add PhotoView to ViewPager and return it
        container.addView(photoView, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);

        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        container.removeView((View) object);
    }
}
