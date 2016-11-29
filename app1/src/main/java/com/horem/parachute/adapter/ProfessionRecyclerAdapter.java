package com.horem.parachute.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.horem.parachute.R;
import com.tencent.mm.sdk.modelmsg.GetMessageFromWX;

import java.util.List;

/**
 * Created by yuanyukun on 2016/7/12.
 */
public class ProfessionRecyclerAdapter extends CommonAdapter<ProfessionRecyclerAdapter.ViewHolder> {

    private List<String> professionLists;
    private Context context;
    private List<Boolean>  checkLists;

    public ProfessionRecyclerAdapter(Context context, List lists,List checkLists) {
        super(context, lists);
        this.professionLists = lists;
        this.context  = context;
        this.checkLists = checkLists;
    }

    public ProfessionRecyclerAdapter(Context mContext) {
        super(mContext);
    }

    public void refreshChecks(List<String> professionLists,List<Boolean> checkLists){
        this.professionLists = professionLists;
        this.checkLists = checkLists;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = View.inflate(mContext, R.layout.profession_list_view, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(checkLists.get(position)){
            holder.check.setVisibility(View.VISIBLE);
            holder.check.setImageResource(R.mipmap.check_48);
        }else{
            holder.check.setVisibility(View.INVISIBLE);
        }
        holder.names.setText(professionLists.get(position));

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
    private TextView names;
    private ImageView check;
    public ViewHolder(View itemView) {
        super(itemView);
        names = (TextView) itemView.findViewById(R.id.profession_names);
        check = (ImageView) itemView.findViewById(R.id.profession_check);
    }
}
}
