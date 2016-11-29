package com.horem.parachute.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.horem.parachute.R;
import com.horem.parachute.task.bean.LocationConfirmBean;
import java.util.List;

public class LocationConfirmAdapter extends RecyclerView.Adapter{

	private Context context;
	private List<LocationConfirmBean> lists;
	private LayoutInflater mInflater;

	public LocationConfirmAdapter(Context context, List<LocationConfirmBean> lists) {
		this.context = context;
		this.lists = lists;
		mInflater = LayoutInflater.from(context);
	}

	public void SetData(List<LocationConfirmBean> lists){
		this.lists =lists ;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = mInflater.inflate(R.layout.location_slide_delete_item_view, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		final ViewHolder mholder = (ViewHolder) holder;

		if (lists != null && 0 <= position && position < lists.size()) {
			final LocationConfirmBean data = lists.get(position);

			// Use ViewBindHelper to restore and save the open/close state of the SwipeRevealView
			// put an unique string id as value, can be any string which uniquely define the data
//            binderHelper.bind(holder.swipeLayout, data);

			// Bind your data here
			mholder.bind(data);
		}
	}

	@Override
	public int getItemCount() {
		return lists.size();
	}


	private class ViewHolder extends RecyclerView.ViewHolder {
		private SwipeRevealLayout swipeLayout;
		private View deleteLayout;
		private RelativeLayout container;
		private TextView locationDescribe;
		private TextView taskTotalFee;
		private TextView taskOrderFee;

		public ViewHolder(View itemView) {
			super(itemView);
			swipeLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout);
			deleteLayout = itemView.findViewById(R.id.delete_layout);
			locationDescribe = (TextView) itemView.findViewById(R.id.task_location_describe);
			taskTotalFee = (TextView) itemView.findViewById(R.id.task_location_fee);
			taskOrderFee = (TextView) itemView.findViewById(R.id.task_location_task_fee);
		}

		public void bind(LocationConfirmBean data) {

			deleteLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					lists.remove(getAdapterPosition());
					notifyItemRemoved(getAdapterPosition());
				}
			});

			locationDescribe.setText(data.getLocationDescribe());
			taskOrderFee.setText("手续费: ￥"+data.getTaskOrderFee());
			taskTotalFee.setText(data.getTaskFee());
		}
	}
}
