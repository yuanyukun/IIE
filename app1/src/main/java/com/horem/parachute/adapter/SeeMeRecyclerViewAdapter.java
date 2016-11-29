package com.horem.parachute.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.horem.parachute.R;
import com.horem.parachute.mine.bean.MineSeeMeSubBean;
import com.horem.parachute.util.MyTimeUtil;
import com.horem.parachute.util.Utils;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 2016/3/18.
 */
public class SeeMeRecyclerViewAdapter extends CommonAdapter<SeeMeRecyclerViewAdapter.ViewHolder>{

    private List<MineSeeMeSubBean> lists;
    private Context context;
    public SeeMeRecyclerViewAdapter(Context context, List<MineSeeMeSubBean> lists) {
        super(context,lists);
        this.lists = lists;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public void setData(List<MineSeeMeSubBean> mLists) {
        lists = mLists;
    }

    public List<MineSeeMeSubBean> getData() {
        return lists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = View.inflate(mContext, R.layout.recycler_view_item_task_see_me, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        MineSeeMeSubBean bean  = lists.get(position);
        viewHolder.userName.setText(bean.getNickName());
        viewHolder.createTime.setText(MyTimeUtil.friendlyTime(
                MyTimeUtil.toDateStr(bean.getCreateTime())));
        Glide.with(context).load(Utils.getHeadeUrl(bean.getHeadImg())).into(viewHolder.userHeadIcon);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView userHeadIcon;
        private TextView userName;
        private TextView createTime;

        public ViewHolder(View itemView) {
            super(itemView);
            userHeadIcon = (CircleImageView) itemView.findViewById(R.id.see_me_user_img);
            userName = (TextView) itemView.findViewById(R.id.see_me_user_name);
            createTime = (TextView) itemView.findViewById(R.id.see_me_time);
        }
    }
}
