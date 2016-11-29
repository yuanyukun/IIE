package com.horem.parachute.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.horem.parachute.R;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;

import java.util.ArrayList;

/**
 * Created by user on 2015/9/11.
 */
public class GuideActivity  extends Activity {
    private ViewPager mviewPager;
    public ArrayList<View> pageViews;
    private ImageView imageView;
    private ImageView[] imageViews;
    private ViewGroup viewPics;
    private ViewGroup viewPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater mInflater = getLayoutInflater();
        viewPics = (ViewGroup) mInflater.inflate(R.layout.activity_guide, null);
        viewPoints = (ViewGroup) viewPics.findViewById(R.id.viewGroup);
        mviewPager = (ViewPager) viewPics.findViewById(R.id.guidePages);

        View view1 = mInflater.inflate(R.layout.guid_viewpage_pager1, null);
        View view2 = mInflater.inflate(R.layout.guid_viewpage_pager3, null);

        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mviewPager.setCurrentItem(1);
                setCurrentFocus(1);
            }
        });
        view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SharePreferencesUtils.setBoolean(getApplicationContext(), SharePrefConstant.KEY_GUIDE_ACTIVITY,false);
                boolean isFalse = SharePreferencesUtils.getBoolean(getApplicationContext(), SharePrefConstant.KEY_GUIDE_ACTIVITY,false);

                Intent mIntent = new Intent();
                mIntent.setClass(GuideActivity.this, AppMainActivity.class);
                GuideActivity.this.startActivity(mIntent);
                GuideActivity.this.finish();
            }
        });


        pageViews = new ArrayList<>();
        pageViews.add(view1);
        pageViews.add(view2);
        imageViews = new ImageView[pageViews.size()];
//      pageViews.add(mInflater.inflate(R.layout.guid_viewpage_pager2, null));
        
        if (pageViews.size() > 1) {
            for (int i = 0; i < pageViews.size(); i++) {
                imageView = new ImageView(GuideActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(30,30);
                lp.leftMargin = 5;
                lp.rightMargin = 5;
                imageView.setLayoutParams(lp);

                imageViews[i] = imageView;
                viewPoints.addView(imageViews[i]);
            }
        }

        setCurrentFocus(0);
        setContentView(viewPics);
        mviewPager.setAdapter(new GuidePageAdapter());

        mviewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
				for (int i = 0; i < imageViews.length; i++) {
					imageViews[arg0]
							.setBackgroundResource(R.drawable.dot_focused);
					if (i != arg0) {
						imageViews[i]
								.setBackgroundResource(R.drawable.dot_normal);
					}
				}
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

    }
    protected void setCurrentFocus(int position){
        resetImageView();
        imageViews[position].setBackgroundResource(R.drawable.dot_focused);
    }
    protected void resetImageView(){
        for (int i = 0; i < imageViews.length; i++) {
            imageViews[i].setBackgroundResource(R.drawable.dot_normal);
        }
    }
    class GuidePageAdapter extends PagerAdapter {
        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(pageViews.get(position));
        }

        @Override
        public void finishUpdate(View container) {
        }

        @Override
        public int getCount() {
            return pageViews.size();
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(pageViews.get(position));
            if (position == pageViews.size() - 1) {
                Button btnButton = (Button) findViewById(R.id.pager3_btn);
                btnButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {

                        Intent mIntent = new Intent();
                        mIntent.setClass(GuideActivity.this, AppMainActivity.class);
                        GuideActivity.this.startActivity(mIntent);
                        GuideActivity.this.finish();
//
                    }
                });
            }
            return pageViews.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void startUpdate(View arg0) {

        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }

    @Override
    protected void onStop() {
        if(imageView!=null){
            releaseImageView(imageView);
        }
        super.onStop();
    }
    @SuppressWarnings("deprecation")
    private void releaseImageView(ImageView imageView) {
        Drawable d = imageView.getDrawable();
        if (d != null)
            d.setCallback(null);
        imageView.setImageDrawable(null);
        imageView.setBackgroundDrawable(null);
    }

}
