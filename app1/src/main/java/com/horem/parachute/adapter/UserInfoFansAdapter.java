package com.horem.parachute.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.common.HttpUrlConstant;
import com.horem.parachute.R;
import com.horem.parachute.balloon.Bean.FansResultSubItem;
import com.horem.parachute.balloon.Bean.FlowersHaveSendSubItem;
import com.horem.parachute.customview.CustomHeadView;
import com.horem.parachute.main.ExitSystemHttpImpl;
import com.horem.parachute.mine.AppUserInfo;
import com.horem.parachute.util.MyTimeUtil;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.horem.parachute.util.Utils;
import com.http.request.HttpApi;
import com.http.request.IResponseApi;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by yuanyukun on 2016/6/21.
 */
public class UserInfoFansAdapter extends RecyclerView.Adapter<UserInfoFansAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<FansResultSubItem> lists;

    public UserInfoFansAdapter(Context context, ArrayList<FansResultSubItem> lists) {
        this.context = context;
        this.lists = lists;
        inflater = LayoutInflater.from(context);
    }

    public void RefreshData(List<FansResultSubItem> list){
        this.lists= list;
        notifyDataSetChanged();
    }
    @Override
    public UserInfoFansAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = inflater.inflate(R.layout.user_info_fans_list_subitem, parent, false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(final UserInfoFansAdapter.ViewHolder holder, final int position) {
         final FansResultSubItem subItem = lists.get(position);
        holder.userName.setText(subItem.getUserName());
        holder.userIcon.showHeadView(subItem.getUserHead(),subItem.getAuthType()
                ,subItem.getUserId(),true);
        holder.userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AppUserInfo.class);
                intent.putExtra("memberId",subItem.getUserId());
                context.startActivity(intent);
            }
        });

        if(subItem.getUserId() == SharePreferencesUtils.getLong(context, SharePrefConstant.MEMBER_ID,(long)0)){
            holder.btnCare.setVisibility(View.INVISIBLE);
        }else{
            holder.btnCare.setVisibility(View.VISIBLE);
            if(subItem.isFollow()){
                holder.btnCare.setText("已关注");
            }else{
                holder.btnCare.setText("关注");
            }
        }
        holder.btnCare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,String> params = buildRequestParams(subItem);
                if(!subItem.isFollow()){
                    addFollow(params,holder,position);
                }else{
                    delFollow(params,holder,position);
                }
            }
        });
    }
    private void delFollow(final HashMap<String, String> params, final ViewHolder holder, final int dataPosition) {

        new AlertView("确定取消关注吗？", null, "取消", new String[]{"取消关注"},null, context, AlertView.Style.ActionSheet,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, final int position) {
                        OkHttpClientManager.postAsyn(HttpUrlConstant.balloonFollowCancel, new OkHttpClientManager.ResultCallback<String>() {
                            @Override
                            public void onError(Request request, Exception e) {
                                ToastManager.show(context,"噢！网络不给力");
                            }

                            @Override
                            public void onResponse(String response) {
//                                Log.d("加关注",response);
                                try {
                                    JSONObject object = new JSONObject(response);
                                    if(!object.isNull("statusCode")){
                                        int errCode = object.optInt("statusCode");
                                        if(1 == errCode){
                                            lists.get(dataPosition).setFollow(false);
                                            holder.btnCare.setText("关注");
                                            notifyDataSetChanged();
                                        }else if(-999 == errCode){
                                            HttpApi httpApi = new ExitSystemHttpImpl();
                                            httpApi.httpRequest(context, new IResponseApi() {
                                                @Override
                                                public void onSuccess(Object object) {

                                                }

                                                @Override
                                                public void onFailed(Exception e) {

                                                }
                                            },new HashMap<String, String>());
                                        }{
                                            ToastManager.show(context,"噢！网络不给力");
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },params);
                    }
                }).setCancelable(true).show();

    }

    private void addFollow(HashMap<String, String> params, final ViewHolder holder, final int position) {
        OkHttpClientManager.postAsyn(HttpUrlConstant.balloonFollowAdd, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(context,"噢！网络不给力");
            }

            @Override
            public void onResponse(String response) {
//                Log.d("加关注",response);
                try {
                    JSONObject object = new JSONObject(response);
                    if(!object.isNull("statusCode")){
                        int errCode = object.optInt("statusCode");
                        if(1 == errCode){
                            lists.get(position).setFollow(true);
                            holder.btnCare.setText("已关注");
                            notifyDataSetChanged();
                        }else if(-999 == errCode){
                            HttpApi httpApi = new ExitSystemHttpImpl();
                            httpApi.httpRequest(context, new IResponseApi() {
                                @Override
                                public void onSuccess(Object object) {

                                }

                                @Override
                                public void onFailed(Exception e) {

                                }
                            },new HashMap<String, String>());
                        }else{
                            ToastManager.show(context,"噢！网络不给力");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },params);
    }

    private HashMap<String,String> buildRequestParams(FansResultSubItem subItem) {
        HashMap<String,String> params = new HashMap<>();
        params.put("followUserId",subItem.getUserId()+"");
        params.put("memberId",SharePreferencesUtils.getLong(context,SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lat","");
        params.put("lng","");
        params.put("clientId",SharePreferencesUtils.getString(context,SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");
        return  params;
    }
    @Override
    public int getItemCount() {
        return lists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView userName;
        private CustomHeadView userIcon;
        private TextView btnCare;
        public ViewHolder(View itemView) {
            super(itemView);
            userIcon = (CustomHeadView) itemView.findViewById(R.id.circle_image_user_info_fans_user_icon);
            userName  = (TextView) itemView.findViewById(R.id.tv_user_info_fans_user_name);
            btnCare = (TextView) itemView.findViewById(R.id.tv_user_info_fans_care);
        }
    }
}
