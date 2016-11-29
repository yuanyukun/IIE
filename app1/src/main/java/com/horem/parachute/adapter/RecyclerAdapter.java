package com.horem.parachute.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.common.HttpUrlConstant;
import com.horem.parachute.R;
import com.horem.parachute.balloon.Bean.BaseResultBean;
import com.horem.parachute.customview.HorizontalSlideAdapter;
import com.horem.parachute.main.ExitSystemHttpImpl;
import com.horem.parachute.mine.SettingBlackListActivity;
import com.horem.parachute.mine.bean.BlackListBean;
import com.horem.parachute.mine.bean.BlackListSubBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.horem.parachute.util.Utils;
import com.http.request.HttpApi;
import com.http.request.IResponseApi;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Chau Thai on 4/8/16.
 */
public class RecyclerAdapter extends RecyclerView.Adapter {
    private List<BlackListSubBean> mDataSet = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context;

    private OnItemDelete onItemDelete;
    public interface OnItemDelete{
        void OnDelete(int positon);
    }
    public void setOnItemDelete(OnItemDelete onItemDelete){
        this.onItemDelete = onItemDelete;
    }



    public RecyclerAdapter(Context context, List<BlackListSubBean> dataSet) {
        mDataSet = dataSet;
        mInflater = LayoutInflater.from(context);
        this.context = context;
        // uncomment if you want to open only one row at a time
        // binderHelper.setOpenOnlyOne(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
        final ViewHolder holder = (ViewHolder) h;

        if (mDataSet != null && 0 <= position && position < mDataSet.size()) {
            final BlackListSubBean data = mDataSet.get(position);

            // Use ViewBindHelper to restore and save the open/close state of the SwipeRevealView
            // put an unique string id as value, can be any string which uniquely define the data
//            binderHelper.bind(holder.swipeLayout, data);

            // Bind your data here
            holder.bind(data,position);
        }
    }
    public void setData(List<BlackListSubBean> lists){
        this.mDataSet = lists;
    }

    @Override
    public int getItemCount() {
        if (mDataSet == null)
            return 0;
        return mDataSet.size();
    }

  /*  *//**
     * Only if you need to restore open/close state when the orientation is changed.
     * Call this method in {@link android.app.Activity#onSaveInstanceState(Bundle)}
     *//*
    public void saveStates(Bundle outState) {
        binderHelper.saveStates(outState);
    }

    *//**
     * Only if you need to restore open/close state when the orientation is changed.
     * Call this method in {@link android.app.Activity#onRestoreInstanceState(Bundle)}
     *//*
    public void restoreStates(Bundle inState) {
        binderHelper.restoreStates(inState);
    }*/

    private class ViewHolder extends RecyclerView.ViewHolder {
        private SwipeRevealLayout swipeLayout;
        private View deleteLayout;
        private CircleImageView userHeader;
        private TextView userName;

        public ViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout);
            deleteLayout = itemView.findViewById(R.id.delete_layout);
            userName = (TextView) itemView.findViewById(R.id.black_list_user_name);
            userHeader = (CircleImageView) itemView.findViewById(R.id.black_list_user_icon);
        }

        public void bind(final BlackListSubBean data, final int position) {
            deleteLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteRequest(position,data);
                }
            });

            userName.setText(data.getPersonalName());
            Glide.with(context).load(Utils.getHeadeUrl(data.getPersonalHead())).into(userHeader).onLoadFailed(new Exception("图片加载失败"), ContextCompat.getDrawable(context,R.mipmap.dark_icon));
        }
    }

    /**
     * 删除黑名单
     */
    private void deleteRequest(final int position,BlackListSubBean data) {
        HashMap<String,String> params = new HashMap<>();
        params.put("memberId",String.valueOf(SharePreferencesUtils.getLong(context, SharePrefConstant.MEMBER_ID,(long)-1)));
        params.put("personalType",data.getPersonalType()+"");
        params.put("personalId",data.getPersonalId()+"");

        OkHttpClientManager.getAsyn(HttpUrlConstant.messageDeleteBlackLists, new OkHttpClientManager.ResultCallback<BaseResultBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(context,"噢！网络不给力");
            }

            @Override
            public void onResponse(BaseResultBean response) {
//                Log.d(getClass().getName(),response);
                if(response.getStatusCode() == 1) {
                    onItemDelete.OnDelete(position);
                }else if(response.getStatusCode() == -999){
                    HttpApi httpApi = new ExitSystemHttpImpl();
                    httpApi.httpRequest(context, new IResponseApi() {
                        @Override
                        public void onSuccess(Object object) {

                        }

                        @Override
                        public void onFailed(Exception e) {

                        }
                    },new HashMap<String, String>());
                }
            }
        },params);
    }
}
