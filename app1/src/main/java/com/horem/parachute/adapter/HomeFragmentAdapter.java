package com.horem.parachute.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.common.HttpUrlConstant;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.balloon.BalloonItemInfoActivity;
import com.horem.parachute.balloon.SendFlowersActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.common.CustomLoading;
import com.horem.parachute.customview.CustomHeadView;
import com.horem.parachute.customview.CustomLoveBtn;
import com.horem.parachute.customview.CustomVideoView;
import com.horem.parachute.login.Activity_Login;
import com.horem.parachute.main.ExitSystemHttpImpl;
import com.horem.parachute.main.bean.BalloonListSubBeanItem;
import com.horem.parachute.main.bean.GetExtroInfoBean;
import com.horem.parachute.mine.ShowLocationActivity;
import com.horem.parachute.util.BitmapUtils;
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

/**
 * Created by yuanyukun on 2016/6/21.
 */
public class HomeFragmentAdapter extends RecyclerView.Adapter<HomeFragmentAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<BalloonListSubBeanItem> lists;

    private OnShareListener listener;
    private OnCameraListener cameraListener;
    public interface  OnShareListener{
        void onShareClicked(int position, Bitmap bitmap);
    }
    public interface OnCameraListener{
        void onCameraClicked(int position);
    }
    public void setOnShareListenner(OnShareListener listener){
        this.listener = listener;
    }
    public void  setOnCameraListener(OnCameraListener listener){
        this.cameraListener = listener;
    }


    public HomeFragmentAdapter(Context context, List<BalloonListSubBeanItem> lists) {
        this.context = context;
        this.lists = lists;
        inflater = LayoutInflater.from(context);
    }

    public void RefreshData(List<BalloonListSubBeanItem> list){
        this.lists= list;
        notifyDataSetChanged();
    }
    @Override
    public HomeFragmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = inflater.inflate(R.layout.fragement_home_item, parent, false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(final HomeFragmentAdapter.ViewHolder holder, final int position) {
       final BalloonListSubBeanItem subItem = lists.get(position);

        RefreshExtroInfo(subItem,holder);//重新刷新子项
        holder.userName.setText(subItem.getCreatePersonName());
        holder.depict.setText(subItem.getBalloonDesc());
        holder.address.setText(subItem.getBalloonAddress());
        holder.createTime.setText(MyTimeUtil.friendlyTime(MyTimeUtil.formatTimeWhole(subItem.getCreateTime())));
        holder.address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoLocationDetails(subItem);
            }
        });

        holder.userIcon.showHeadView(subItem.getCreatePersonHead(),subItem.
                getAuthType(),subItem.getCreatePersonId(),true);

        if(subItem.getCreatePersonId() == SharePreferencesUtils.getLong(context, SharePrefConstant.MEMBER_ID,(long)0)){
            holder.Watcher.setVisibility(View.INVISIBLE);
        }else{
            holder.Watcher.setVisibility(View.VISIBLE);
        }
        holder.Watcher.setClickable(true);
        holder.Watcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CustomApplication.getInstance().isLogin()){
                    HashMap<String,String> params = buildRequestParams(subItem);
                    if(!subItem.isFollow()){
                        addFollow(params,holder,position);
                    }else {
                        delFollow(params, holder, position);

                    }
                }else{
                    Intent intent = new Intent(context,Activity_Login.class);
                    context.startActivity(intent);
                }
            }
        });

        holder.container.setClickable(true);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(context,BalloonItemInfoActivity.class);
                intent.putExtra("balloonId",subItem.getBalloonId());
                context.startActivity(intent);
            }
        });

        holder.rlOnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraListener.onCameraClicked(position);
            }
        });
        holder.rlOnFlowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CustomApplication.getInstance().isLogin()){
                    Intent intent = new Intent(context, Activity_Login.class);
                    context.startActivity(intent);
                }else {
                    Intent intent = new Intent(context, SendFlowersActivity.class);
                    intent.putExtra("type","homeFragment");
                    intent.putExtra("balloonId", subItem.getBalloonId());
                    intent.putExtra("head", subItem.getCreatePersonHead());
                    context.startActivity(intent);
                }
            }
        });
        holder.customView.setData(subItem.getMediaType(),subItem.getAttList().get(0).getFileSize(),
                subItem.getAttList().get(0).getTimeLength(),subItem.getAttList().get(0).getName(),
                subItem.getAttList().get(0).getPreviewImg(), Utils.getRunningActivityName(context));

    }

    private void RefreshExtroInfo(final BalloonListSubBeanItem subItem, final ViewHolder holder) {
        HashMap<String,String> params = buildRequestParams(subItem);
        params.put("balloonId",subItem.getBalloonId());
        OkHttpClientManager.postAsyn(HttpUrlConstant.balloonExtroInfo, new OkHttpClientManager.ResultCallback<GetExtroInfoBean>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(GetExtroInfoBean response) {
//                Log.d("重新获取数据",new Gson().toJson(response));
                if(response.getStatusCode() == 1) {

                    holder.loveBtn.setInitData(subItem.getBalloonId(),response.getResult().isGiveLike(),
                            response.getResult().getGiveLikeNum());

                    if (response.getResult().isFollow()) {
                        holder.Watcher.setText("已关注");
                    } else {
                        holder.Watcher.setText("关注");
                    }
//                    holder.tvShareNum.setText(response.getResult().getShareNum() + "");
                    holder.tvCameraNum.setText(response.getResult().getRequestNum() + "");
                    holder.tvFlowerNum.setText(response.getResult().getFlowersNum() + "");
                }
            }
        },params);
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
                                            holder.Watcher.setText("关注");
                                            notifyDataSetChanged();
                                        }else if( -999 == errCode){
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
                                    }else{
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
                            holder.Watcher.setText("已关注");
                            notifyDataSetChanged();
                        } else if(errCode == -999){
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

    private HashMap<String,String> buildRequestParams(BalloonListSubBeanItem subItem) {
        HashMap<String,String> params = new HashMap<>();
        params.put("followUserId",subItem.getCreatePersonId()+"");
        params.put("memberId",SharePreferencesUtils.getLong(context,SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lat","");
        params.put("lng","");
        params.put("clientId",SharePreferencesUtils.getString(context,SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");
        return  params;
    }

    private void gotoLocationDetails(BalloonListSubBeanItem subItem) {
        Intent intent = new Intent(context, ShowLocationActivity.class);
        intent.putExtra("latlng",new LatLng(subItem.getBalloonLatitude(),subItem.getBalloonLongitude()));
        context.startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return lists.size();
//        return lists.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView userName;     //
//        private ImageView userType;
        private TextView Watcher;//
        private TextView depict;

        private TextView address;     //
        private TextView createTime;   //
        private CustomHeadView userIcon;

//        private ImageView previewImg;
//        private ImageView videoStart;
//        private TextView infoTips;
//        private LinearLayout rlOnShare;
        private LinearLayout rlOnCamera;
        private LinearLayout rlOnFlowers;
//        private TextView tvShareNum;
        private TextView tvCameraNum;
        private TextView tvFlowerNum;
        private LinearLayout container;
//        private TextView tvFileSize;
//        private TextView tvFileLength;
        private CustomVideoView customView;
        private CustomLoveBtn loveBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            userIcon = (CustomHeadView) itemView.findViewById(R.id.home_fragment_user_head);
            userName = (TextView) itemView.findViewById(R.id.home_fragment_user_name);
            Watcher = (TextView) itemView.findViewById(R.id.home_fragment_button);
            depict = (TextView) itemView.findViewById(R.id.home_fragment_balloon_describe);
            address = (TextView) itemView.findViewById(R.id.home_fragment_balloon_address);
            createTime = (TextView) itemView.findViewById(R.id.home_fragment_balloon_create_time);
//           rlOnShare = (LinearLayout) itemView.findViewById(R.id.home_fragment_on_share);
           rlOnCamera = (LinearLayout) itemView.findViewById(R.id.home_fragment_on_camera);
           rlOnFlowers  = (LinearLayout) itemView.findViewById(R.id.home_fragment_on_flowers);

//            tvShareNum = (TextView) itemView.findViewById(R.id.home_fragment_balloon_share_tv);
            tvCameraNum = (TextView) itemView.findViewById(R.id.home_fragment_balloon_camera_tv);
            tvFlowerNum = (TextView) itemView.findViewById(R.id.home_fragment_balloon_flower_tv);
            container = (LinearLayout) itemView.findViewById(R.id.home_fragment_container);

            customView = (CustomVideoView) itemView.findViewById(R.id.home_fragment_custom_video_view);
            loveBtn = (CustomLoveBtn) itemView.findViewById(R.id.home_fragment_balloon_custom_like_btn);
        }
    }

}
