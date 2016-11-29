package com.horem.parachute.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.common.HttpUrlConstant;
import com.horem.parachute.R;
import com.horem.parachute.balloon.BalloonItemInfoActivity;
import com.horem.parachute.balloon.Bean.FlowersToMeSubItem;
import com.horem.parachute.main.ExitSystemHttpImpl;
import com.horem.parachute.mine.bean.GiveLikeMeListItemBean;
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

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by yuanyukun on 2016/6/21.
 */
public class GiveLikeMeAdapter extends RecyclerView.Adapter<GiveLikeMeAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<GiveLikeMeListItemBean> lists;
    private int flag;
    private static final int HAVE_GIVE_LIKE = 0x10;//我赞过的
    private static final int GIVE_LIKE_ME = 0x11; //赞过我的

    public GiveLikeMeAdapter(Context context, List<GiveLikeMeListItemBean> lists,int Flag) {
        this.context = context;
        this.lists = lists;
        this.flag = Flag;
        inflater = LayoutInflater.from(context);
    }

    public void RefreshData(List<GiveLikeMeListItemBean> list){
        this.lists= list;
        notifyDataSetChanged();
    }
    @Override
    public GiveLikeMeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = inflater.inflate(R.layout.flowers_to_me_list, parent, false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(final GiveLikeMeAdapter.ViewHolder holder, final int position) {
         final GiveLikeMeListItemBean subItem = lists.get(position);

        holder.userName.setText(subItem.getUserName());
        Glide.with(context).load(Utils.getHeadeUrl(subItem.getUserHead()))
                .into(holder.customerUserHead);

        StringBuilder describe = new StringBuilder();
        describe.append(MyTimeUtil.formatTimeZhShort(subItem.getCreateTime()));
        switch (flag){
            case 0x10:
                describe.append("赞TA");

                break;
            case 0x11:
                describe.append("赞我");
                break;
        }
        holder.describe.setText(describe.toString());

        holder.balloonImage.setScaleType(ImageView.ScaleType.FIT_XY);
        if(subItem.getAttList() != null) {
            holder.container.setVisibility(View.VISIBLE);
            if (subItem.getAttList().size() > 0) {
                Glide.with(context).load(Utils.getHeadeUrl(subItem.getAttList().get(0).getName()))
                        .into(holder.balloonImage);
            }
            holder.balloonTime.setText(MyTimeUtil.formatTimeZhShort(subItem.getBalloonCreateTime()));
            holder.address.setText(subItem.getBalloonAddress());//气球地址
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, BalloonItemInfoActivity.class);
                    intent.putExtra("balloonId",subItem.getBalloonId());
                    context.startActivity(intent);
                }
            });



        }else{
            holder.container.setVisibility(View.GONE);
        }

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

                            }

                            @Override
                            public void onResponse(String response) {
                                Log.d("加关注",response);
                                try {
                                    JSONObject object = new JSONObject(response);
                                    if(!object.isNull("statusCode")){
                                        int errCode = object.optInt("statusCode");
                                        if(1 == errCode){
                                            lists.get(dataPosition).setFollow(false);
                                            holder.btnCare.setText("关注");
                                            notifyDataSetChanged();
                                        }else if(errCode == -999){
                                            HttpApi httpApi = new ExitSystemHttpImpl();
                                            httpApi.httpRequest(context, new IResponseApi() {
                                                @Override
                                                public void onSuccess(Object object) {

                                                }

                                                @Override
                                                public void onFailed(Exception e) {

                                                }
                                            },new HashMap<String, String>());
                                        }else
                                            ToastManager.show(context,"噢，网络不给力！");
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
                        }else if(errCode == -999){
                            HttpApi httpApi = new ExitSystemHttpImpl();
                            httpApi.httpRequest(context, new IResponseApi() {
                                @Override
                                public void onSuccess(Object object) {

                                }

                                @Override
                                public void onFailed(Exception e) {

                                }
                            },new HashMap<String, String>());
                        }else {
                            ToastManager.show(context,"噢！网络不给力");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },params);
    }

    private HashMap<String,String> buildRequestParams(GiveLikeMeListItemBean subItem) {
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
        private CircleImageView customerUserHead;
        private TextView btnCare;
        private ImageView balloonImage;
        private TextView balloonTime;
        private TextView describe;
        private TextView address;

        private LinearLayout container;
        public ViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.tv_send_flower_list_user_name);
            customerUserHead = (CircleImageView) itemView.findViewById(R.id.send_flowers_list_user_icon);
            btnCare = (TextView) itemView.findViewById(R.id.btn_send_flowers_list_care);

            balloonImage = (ImageView) itemView.findViewById(R.id.img_flowers_to_me_balloon_image);
            balloonTime = (TextView) itemView.findViewById(R.id.tv_flowers_to_me_balloon_time);
            describe = (TextView) itemView.findViewById(R.id.tv_send_flower_list_flower_num);
            address = (TextView) itemView.findViewById(R.id.tv_flowers_to_me_balloon_address);
            container = (LinearLayout) itemView.findViewById(R.id.ll_flowers_to_me_balloon_container);
        }
    }


}
