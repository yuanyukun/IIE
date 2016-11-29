package com.horem.parachute.adapter;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.horem.parachute.R;
import com.horem.parachute.mine.bean.AddressSubBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 2016/3/18.
 */
public class HomeTipsRecyclerViewAdapter extends CommonAdapter<HomeTipsRecyclerViewAdapter.ViewHolder>{

    private List<AddressSubBean> lists = new ArrayList<>();


    private OnItemDelete onItemDelete;
    public interface OnItemDelete{
        void OnDelete(int positon);
    }
    public void setOnItemDelete(OnItemDelete onItemDelete){
        this.onItemDelete = onItemDelete;
    }


    public HomeTipsRecyclerViewAdapter(Context context,List<AddressSubBean>  lists) {
            super(context,lists);
        this.lists = lists;
    }

    public void setData(List<AddressSubBean> mLists) {
        this.lists = mLists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = View.inflate(mContext, R.layout.recycler_view_item_home_tips, null);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return lists.size();

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        super.onBindViewHolder(viewHolder, i);

        viewHolder.deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemDelete.OnDelete(i);
            }
        });
        viewHolder.location.setText(lists.get(i).getAddressName());

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
            private SwipeRevealLayout swipeLayout;
            private View deleteLayout;
            private TextView location;
        public ViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeRevealLayout) itemView.findViewById(R.id.home_tips_swipe_layout);
            deleteLayout = itemView.findViewById(R.id.home_tips_delete_layout);
            location = (TextView) itemView.findViewById(R.id.home_tips_location);
        }
    }
}
