package com.horem.parachute.customview;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class MyRecommendedScrollView extends ScrollView {

	public MyRecommendedScrollView(Context context) {
		super(context);
	}

	public MyRecommendedScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyRecommendedScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy) {	
	   super.onScrollChanged(x, y, oldx, oldy);
	}

}
