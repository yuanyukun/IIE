package com.horem.parachute.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.horem.parachute.R;
import com.horem.parachute.mine.bean.MoneyDetailSubBean;

import java.util.ArrayList;

/**
 * Created by user on 2016/4/18.
 */
public class MoneyDetailAdapter extends RecyclerView.Adapter<MoneyDetailAdapter.ViewHolder> {

    private ArrayList<MoneyDetailSubBean> mlists;
    private Context mContext;
    public MoneyDetailAdapter(Context context, ArrayList<MoneyDetailSubBean> lists) {
        this.mlists = lists;
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        return mlists.size();
    }

    public void setData(ArrayList<MoneyDetailSubBean> lists){

        this.mlists = lists;
    }


    @Override
    public MoneyDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.recycler_view_money_details, null);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        switch (getItemViewType(position)){
            case 10:
                holder.taskResume.setTextColor(ContextCompat.getColor(mContext,R.color.color_green));

                break;
            case 20:
                holder.taskResume.setText(mlists.get(position).getTotalFeeStr());
                break;
        }


        holder.taskResume.setText(mlists.get(position).getTotalFeeStr());
        holder.taskCreateTime.setText(getTimeStr(position));
        holder.moneyIntro.setText(mlists.get(position).getDepict());
    }

    private String getTimeStr(int position){
        String[] value = mlists.get(position).getCreateTime().split("T");
        String[] value1 = value[1].split(":");
        return value[0] + "  " + value1[0]+":"+value1[1];
    }
    @Override
    public int getItemViewType(int position) {
        Log.i(getClass().getName(),mlists.get(position).getS_paymentType()+"");
       return  mlists.get(position).getS_paymentType();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView moneyIntro;
        private TextView taskCreateTime;
        private TextView taskResume;

        public ViewHolder(View itemView) {
            super(itemView);
            moneyIntro = (TextView) itemView.findViewById(R.id.money_introduce);
            taskCreateTime = (TextView) itemView.findViewById(R.id.task_createtime);
            taskResume = (TextView) itemView.findViewById(R.id.money_costs);
        }
    }
}
