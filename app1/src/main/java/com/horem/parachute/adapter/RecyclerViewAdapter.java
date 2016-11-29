package com.horem.parachute.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.horem.parachute.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 2016/3/18.
 */
public class RecyclerViewAdapter extends CommonAdapter<RecyclerViewAdapter.ViewHolder>{





    public RecyclerViewAdapter(Context context, List<HashMap<String,Object>> lists) {
        super(context,lists);
    }

    public void setData(List<HashMap<String,Object>> mLists) {
        lists = mLists;
    }

    public List<HashMap<String,Object>> getData() {
        return lists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = View.inflate(mContext, R.layout.recycler_view_item_task_see_me, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);
//        viewHolder.mTitle.setText("I'm a " + i + " item");
    /*Glide.with(mContext)
            .load(DRAW_IDS[i%5])
            .fitCenter()
            .into(viewHolder.mLogo);*/
        // 用这种方式会卡
//        viewHolder.mLogo.setImageResource(DRAW_IDS[i%5]);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
//        private CircleImageView mLogo;
//        private TextView mTitle;

        public ViewHolder(View itemView) {
            super(itemView);
//            mLogo = (CircleImageView) itemView.findViewById(R.id.logo);
//            mTitle = (TextView) itemView.findViewById(R.id.text);
        }
    }
}
