package com.horem.parachute.customview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.WorkerThread;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.horem.parachute.R;
import com.horem.parachute.mine.bean.HomeChatMessageBean;
import com.horem.parachute.util.MyTimeUtil;
import com.horem.parachute.util.Utils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HorizontalSlideAdapter extends BaseAdapter{

	private int mScreenWidth;

	private DeleteButtonOnclickImpl mDelOnclickImpl;
	private ScrollViewScrollImpl mScrollImpl;

	private LinearLayout.LayoutParams mParams;

	public HorizontalScrollView mScrollView;

	public boolean mLockOnTouch = false;
	private Context context;
	private ArrayList<HomeChatMessageBean> lists;

	private OnItemDelete itemDelete;
	public void setOnItemDelete(OnItemDelete itemDelete){
		this.itemDelete = itemDelete;
	}
	public interface OnItemDelete{
		void itemClicked(int position,View view);
	}
	private OnWholeItemClicked wholeItemClicked;
	public void setOnWholeItemClicked(OnWholeItemClicked wholeItemClicked){
		this.wholeItemClicked = wholeItemClicked;
	}
	public interface OnWholeItemClicked{
		void wholeItemClicked(int position);
	}

	public HorizontalSlideAdapter(Context context, List<HomeChatMessageBean> objects) {

		this.context = context;
		this.lists = (ArrayList<HomeChatMessageBean>) objects;

		Display defaultDisplay = ((Activity) context).getWindowManager()
				.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		defaultDisplay.getMetrics(metrics);
		mScreenWidth = metrics.widthPixels;
		mParams = new LinearLayout.LayoutParams(mScreenWidth,LinearLayout.LayoutParams.MATCH_PARENT);
		mDelOnclickImpl = new DeleteButtonOnclickImpl();
		mScrollImpl = new ScrollViewScrollImpl();
	}

	public void SetData(ArrayList<HomeChatMessageBean> lists){
		this.lists =lists ;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return lists.size();
	}

	@Override
	public Object getItem(int position) {
		return lists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		HomeChatMessageBean bean = (HomeChatMessageBean) getItem(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context,
					R.layout.item_horizontal_slide_listview, null);
			holder.scrollView = (HorizontalScrollView) convertView;
			holder.scrollView.setOnTouchListener(mScrollImpl);
			holder.scrollView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					wholeItemClicked.wholeItemClicked(position);
				}
			});

			holder.redDot  = (ImageView) convertView.findViewById(R.id.conversation_red_dot);
			holder.container = (RelativeLayout) convertView
					.findViewById(R.id.home_chat_container);
			holder.container.setLayoutParams(mParams);

			holder.userHeaderIcon = (CircleImageView) convertView
					.findViewById(R.id.home_chat_user_icon);

			holder.userName = (TextView) convertView
					.findViewById(R.id.home_chat_user_name);
			holder.chatMessageContent = (TextView) convertView
					.findViewById(R.id.home_chat_content_message);
			holder.lastConversationTime = (TextView) convertView
					.findViewById(R.id.home_chat_time);

			switch (bean.getPersonalType()){
				case 10:
					holder.userName.setTextColor(ContextCompat.getColor(context,R.color.material_blue));
					break;
				case 20:
					break;
			}
			Glide.with(context).load(Utils.getHeadeUrl(bean.getPersonalHead()))
					.into(holder.userHeaderIcon);

			if(bean.isHasNewMessage()){
				holder.redDot.setVisibility(View.VISIBLE);
			}else{
				holder.redDot.setVisibility(View.INVISIBLE);
			}
			holder.userName.setText(bean.getPersonalName());
			holder.chatMessageContent.setText(bean.getMessageContent());
			holder.chatMessageContent.setTextColor(ContextCompat.getColor(context,R.color.text_grey));

			holder.lastConversationTime.setText(MyTimeUtil.friendlyTime(MyTimeUtil.formatTimeWhole(bean.getCreateTime())));
			holder.lastConversationTime.setTextColor(ContextCompat.getColor(context,R.color.text_grey));

			holder.deleteButton = (Button) convertView
					.findViewById(R.id.item_delete);
			holder.deleteButton.setOnClickListener(mDelOnclickImpl);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.position = position;
		holder.deleteButton.setTag(holder);
//		holder.infoTextView.setText(getItem(position));
		holder.scrollView.scrollTo(0, 0);
		return convertView;
	}

public	static class ViewHolder {
		public HorizontalScrollView scrollView;
		private RelativeLayout container;
		private CircleImageView userHeaderIcon;
		private TextView userName;
		private TextView chatMessageContent;
		private TextView lastConversationTime;
		private Button deleteButton;
		private int position;
		private ImageView redDot;
	}

	private class ScrollViewScrollImpl implements OnTouchListener {
		private int startX = 0;
		private float xDwon = 0.0f;
		private float xUp = 0.0f;

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
		 	ViewHolder holder = (ViewHolder) v.getTag();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				Log.d(context+"","ActionDown");
				if (mScrollView != null) {
					scrollView(mScrollView, HorizontalScrollView.FOCUS_LEFT);
					mScrollView = null;
					mLockOnTouch = true;
					return true;
				}
				startX = (int)event.getX();
				break;
			case MotionEvent.ACTION_UP:
				Log.d(context+"","ActionUp");
				HorizontalScrollView view = (HorizontalScrollView) v;
				if (startX > event.getX() + 50) {
					startX = 0;
					scrollView(view, HorizontalScrollView.FOCUS_RIGHT);
					mScrollView = view;
				} else if(startX < event.getX()-50){
					scrollView(view, HorizontalScrollView.FOCUS_LEFT);
				}else{
					wholeItemClicked.wholeItemClicked(holder.position);
				}
				break;
			}
			return false;
		}
	}

	public void scrollView(final HorizontalScrollView view, final int parameter) {
		view.post(new Runnable() {
			@Override
			public void run() {
				view.pageScroll(parameter);
			}
		});
	}

	private class DeleteButtonOnclickImpl implements OnClickListener {
		@Override
		public void onClick(View v) {
			final ViewHolder holder = (ViewHolder) v.getTag();
			itemDelete.itemClicked(holder.position,v);

		}
	}
}
