package com.horem.parachute.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.common.ApplicationConstant;
import com.common.HttpUrlConstant;
import com.horem.parachute.R;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.customview.ActionSheetDialog;
import com.horem.parachute.login.Activity_Login;
import com.horem.parachute.login.bean.SubTaskBean;
import com.horem.parachute.main.ExitSystemHttpImpl;
import com.horem.parachute.main.bean.BalloonListSubBeanItem;
import com.horem.parachute.mine.AppUserInfo;
import com.horem.parachute.task.TaskInformationActivity;
import com.horem.parachute.task.TaskSendPhotoActivity;
import com.horem.parachute.task.TaskSendVideoActivity;
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
 * Created by user on 2016/3/18.
 */
public class PopupTaskRecyclerViewAdapter extends CommonAdapter<PopupTaskRecyclerViewAdapter.ViewHolder>{

    private SubTaskBean[] taskBeanList;
    private CustomApplication application;
    private  SubTaskBean subItem;

    public PopupTaskRecyclerViewAdapter(Context mContext, SubTaskBean[] lists, Activity activity) {
        super(mContext);
        this.taskBeanList = lists;
        application = (CustomApplication) activity.getApplication();
    }

    @Override
    public int getItemCount() {
        return taskBeanList.length;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = View.inflate(mContext, R.layout.task_option_item, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        super.onBindViewHolder(viewHolder,position);
        subItem = taskBeanList[position];
        switch (getItemViewType(position)){
            case ApplicationConstant.MEDIA_TYPE_PHOTO:
                viewHolder.taskTypeBtn.setText("拍照片");

                break;
            case ApplicationConstant.MEDIA_TYPE_VIDEO:
                viewHolder.taskTypeBtn.setText("拍视频");
                break;
        }
        Glide.with(mContext).load(Utils.getHeadeUrl(taskBeanList[position].getCreatePersonHead()))
                .placeholder(R.mipmap.circle_dark)
                .into(viewHolder.userHeader);
        viewHolder.userHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, AppUserInfo.class);
                intent.putExtra("memberId",taskBeanList[position].getCreatePersonId());
                mContext.startActivity(intent);
            }
        });
        viewHolder.userName.setText(taskBeanList[position].getCreatePersonName());
        if(taskBeanList[position].isFollow()){
            viewHolder.careBtn.setText("已关注");
        }else{
            viewHolder.careBtn.setText("关注");
        }
        viewHolder.careBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CustomApplication.getInstance().isLogin()){
                    HashMap<String,String> params = buildRequestParams(subItem);
                    if(!subItem.isFollow()){
                        addFollow(params,viewHolder,position);
                    }else {
                        delFollow(params, viewHolder, position,v);

                    }
                }else{
                    Intent intent = new Intent(mContext,Activity_Login.class);
                    mContext.startActivity(intent);
                }
            }
        });

        if(position == taskBeanList.length-1){
            viewHolder.rightBlank.setVisibility(View.VISIBLE);
        }
        viewHolder.taskFee.setText(taskBeanList[position].getSymbol()+taskBeanList[position].getTotal_fee());
        if(subItem.getAttList() != null && subItem.getAttList().size() != 0)
        {
            viewHolder.taskDescribe.setText("（图）"+taskBeanList[position].getOtherRequirements());
        }else {
            viewHolder.taskDescribe.setText("（图）"+taskBeanList[position].getOtherRequirements());
        }

        viewHolder.taskDistance.setText(taskBeanList[position].getPlace());
        viewHolder.taskOverTime.setText(taskBeanList[position].getTimeOutStr());

        viewHolder.clickContainer.setClickable(true);
        viewHolder.clickContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,TaskInformationActivity.class);
                intent.putExtra("subTaskId",taskBeanList[position].getSubTaskId());
                mContext.startActivity(intent);
            }
        });

        viewHolder.taskTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(application.isLogin()){
                    switch (getItemViewType(position)){
                        case ApplicationConstant.MEDIA_TYPE_PHOTO:
                            Intent intent = new Intent(mContext,TaskSendPhotoActivity.class);
                            intent.putExtra("subTaskId",taskBeanList[position].getSubTaskId());
                            intent.putExtra("lat",taskBeanList[position].getLatitude());
                            intent.putExtra("lng",taskBeanList[position].getLongitude());
                            mContext.startActivity(intent);
                            break;
                        case ApplicationConstant.MEDIA_TYPE_VIDEO:
                            Intent intent1 = new Intent(mContext,TaskSendVideoActivity.class);
                            intent1.putExtra("subTaskId",taskBeanList[position].getSubTaskId());
                            intent1.putExtra("lat",taskBeanList[position].getLatitude());
                            intent1.putExtra("lng",taskBeanList[position].getLongitude());
                            mContext.startActivity(intent1);
                            break;
                    }
                }else{
                    Intent intent = new Intent(mContext, Activity_Login.class);
                    mContext.startActivity(intent);
                }

            }
        });
    }
    private void delFollow(final HashMap<String, String> params, final ViewHolder holder, final int dataPosition,View view) {
        new ActionSheetDialog(mContext)
                .builder()
                .setTitle("确定取消关注吗?")
                .setCancelable(true)
                .setCanceledOnTouchOutside(false)
                .addSheetItem("取消关注", ActionSheetDialog.SheetItemColor.Red,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                OkHttpClientManager.postAsyn(HttpUrlConstant.balloonFollowCancel, new OkHttpClientManager.ResultCallback<String>() {
                                    @Override
                                    public void onError(Request request, Exception e) {
                                        ToastManager.show(mContext,"噢！网络不给力");
                                    }

                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject object = new JSONObject(response);
                                            if(!object.isNull("statusCode")){
                                                int errCode = object.optInt("statusCode");
                                                if(1 == errCode){
                                                    taskBeanList[dataPosition].setFollow(false);
                                                    holder.careBtn.setText("关注");
                                                    notifyDataSetChanged();
                                                }else if(errCode == -999){
                                                    HttpApi httpApi = new ExitSystemHttpImpl();
                                                    httpApi.httpRequest(mContext, new IResponseApi() {
                                                        @Override
                                                        public void onSuccess(Object object) {

                                                        }

                                                        @Override
                                                        public void onFailed(Exception e) {

                                                        }
                                                    },new HashMap<String, String>());
                                                }else{
                                                    ToastManager.show(mContext,"噢！网络不给力");
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },params);
                            }
                        }).show();
    }

    private void addFollow(HashMap<String, String> params, final ViewHolder holder, final int position) {
        OkHttpClientManager.postAsyn(HttpUrlConstant.balloonFollowAdd, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(mContext,"噢！网络不给力");
            }

            @Override
            public void onResponse(String response) {
//                Log.d("加关注",response);
                try {
                    JSONObject object = new JSONObject(response);
                    if(!object.isNull("statusCode")){
                        int errCode = object.optInt("statusCode");
                        if(1 == errCode){
                            taskBeanList[position].setFollow(true);
                            holder.careBtn.setText("已关注");
                            notifyDataSetChanged();
                        }else if(errCode == -999){
                            HttpApi httpApi = new ExitSystemHttpImpl();
                            httpApi.httpRequest(mContext, new IResponseApi() {
                                @Override
                                public void onSuccess(Object object) {

                                }

                                @Override
                                public void onFailed(Exception e) {

                                }
                            },new HashMap<String, String>());

                        }else{
                            ToastManager.show(mContext,"噢！网络不给力");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },params);
    }

    private HashMap<String,String> buildRequestParams(SubTaskBean subItem) {
        HashMap<String,String> params = new HashMap<>();
        params.put("followUserId",subItem.getCreatePersonId()+"");
        params.put("memberId", SharePreferencesUtils.getLong(mContext, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lat","");
        params.put("lng","");
        params.put("clientId",SharePreferencesUtils.getString(mContext,SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");
        return  params;
    }
    @Override
    public int getItemViewType(int position) {
        return taskBeanList[position].getMediaType();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private View rightBlank;
//        private Button taskInfoBtn;
        private TextView taskTypeBtn;
//        private TextView taskName;
        private TextView taskFee;
        private TextView taskDescribe;
        private TextView taskDistance;
        private TextView taskOverTime;
        private CircleImageView userHeader;
        private TextView userName;
        private TextView careBtn;
//        private ImageView imageView;
        private LinearLayout clickContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            rightBlank = itemView.findViewById(R.id.right_blank);
//            taskInfoBtn = (Button) itemView.findViewById(R.id.info_button);
            taskTypeBtn = (TextView) itemView.findViewById(R.id.task_type);
//            imageView  = (ImageView) itemView.findViewById(R.id.tv_app_user_info_detail);
//            taskName = (TextView) itemView.findViewById(R.id.tv_task_name);
            taskFee= (TextView) itemView.findViewById(R.id.tv_user_info_price);
            taskDescribe = (TextView) itemView.findViewById(R.id.tv_task_describe);
            taskDistance = (TextView) itemView.findViewById(R.id.tv_show_distance);
            taskOverTime = (TextView) itemView.findViewById(R.id.tv_task_over_time);
            userHeader = (CircleImageView) itemView.findViewById(R.id.task_info_head_icon);
            userName = (TextView) itemView.findViewById(R.id.tv_task_info_user_name);
            careBtn = (TextView) itemView.findViewById(R.id.tv_task_info_care_btn);
            clickContainer = (LinearLayout) itemView.findViewById(R.id.click_container);
        }
    }
}
