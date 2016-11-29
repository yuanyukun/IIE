package com.horem.parachute.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class HorizontalSlideDeleteListView extends ListView {
	
	private HorizontalSlideAdapter mAdapter;

	public HorizontalSlideDeleteListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(mAdapter.mLockOnTouch){
			if (ev.getAction() == MotionEvent.ACTION_DOWN
					|| ev.getAction() == MotionEvent.ACTION_MOVE) {
				if (mAdapter.mScrollView != null) {
					mAdapter.scrollView(mAdapter.mScrollView,
							HorizontalScrollView.FOCUS_LEFT);
					mAdapter.mScrollView = null;
				}
				return true;
			}
			if (ev.getAction() == MotionEvent.ACTION_UP) {
				mAdapter.mLockOnTouch = false;
			}
		}
		return super.dispatchTouchEvent(ev);
	}
	
	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		mAdapter = (HorizontalSlideAdapter) adapter;
	}
}
