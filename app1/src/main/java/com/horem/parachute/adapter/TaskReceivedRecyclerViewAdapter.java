package com.horem.parachute.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.common.ApplicationConstant;
import com.common.HttpUrlConstant;
import com.horem.parachute.R;
import com.horem.parachute.autoupdate.internal.NetworkUtil;
import com.horem.parachute.balloon.BalloonItemInfoActivity;
import com.horem.parachute.common.CustomLoading;
import com.horem.parachute.customview.CustomHeadView;
import com.horem.parachute.customview.CustomVideoView;
import com.horem.parachute.main.ExitSystemHttpImpl;
import com.horem.parachute.menu.PopupPhotoView;
import com.horem.parachute.mine.AppUserInfo;
import com.horem.parachute.mine.ShowLocationActivity;
import com.horem.parachute.mine.bean.TaskReceivedSubBean;
import com.horem.parachute.task.TaskInformationActivity;
import com.horem.parachute.task.TaskSendPhotoActivity;
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

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by user on 2016/3/18.
 */
public class TaskReceivedRecyclerViewAdapter extends CommonAdapter<TaskReceivedRecyclerViewAdapter.ViewHolder>{

    private List<TaskReceivedSubBean> lists;
    private Context context;
    private LayoutInflater inflater;

    public TaskReceivedRecyclerViewAdapter(Context context, List<TaskReceivedSubBean> lists) {
        super(context,lists);
        this.context = context;
        this.lists = lists;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<TaskReceivedSubBean> mLists) {
        lists = mLists;
    }

    public List<TaskReceivedSubBean> getData() {
        return lists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = View.inflate(mContext, R.layout.task_received_item_view, null);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        TaskReceivedSubBean data = lists.get(position);

        dealWithPicShow(viewHolder, data);//视频或者照片的预览图
        dealWithTaskTypeShow(viewHolder,data);//气球还是伞
        dealWithBottomStatus(viewHolder, data);//底部栏不同状态
    }
    private void dealWithPicShow(ViewHolder viewHolder, TaskReceivedSubBean data) {
        viewHolder.taskReceivedContanier.removeAllViews();
        if(data.getBalloonDetail() != null && data.getOrderState() == 10){
            balloonViewShow(viewHolder,data);//气球
        }else {
            //接收到的图片缩略图或视频预览图
            picTaskShow(viewHolder, data);
        }
    }

    private void dealWithTaskTypeShow(ViewHolder viewHolder,TaskReceivedSubBean data) {
        viewHolder.takTypeInfoContanier.removeAllViews();
        if(data.getBalloonDetail() != null && data.getOrderState() == 10){
            AddBalloonInfoShow(viewHolder,data);
        }else{
            AddTaskInfoShow(viewHolder,data);
        }
    }

    private void AddTaskInfoShow(ViewHolder viewHolder, final TaskReceivedSubBean data) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_receive_task_info_show,null);
        CustomHeadView headView = (CustomHeadView) view.findViewById(R.id.custom_task_container_head_view);
        TextView tvUserName = (TextView) view.findViewById(R.id.tv_task_container_address);
        TextView tvPlace = (TextView) view.findViewById(R.id.tv_task_container_create_time);
        TextView tvMoney = (TextView) view.findViewById(R.id.tv_task_container_money);


        headView.showHeadView(data.getCreatePersonHead(),data.getAuthType(),data.getCreatePersonId(),true);
        tvPlace.setText(data.getPlace());
        tvUserName.setText(data.getCreatePersonName());
        tvMoney.setText(data.getSymbol()+ Utils.getTwoLastNumber(data.getTotalFee()));
        Log.d("总费用:  ",data.getTotalFee()+"");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TaskInformationActivity.class);
                intent.putExtra("subTaskId",data.getSubTaskId());
                context.startActivity(intent);
            }
        });
        viewHolder.takTypeInfoContanier.addView(view);
    }

    private void AddBalloonInfoShow(ViewHolder viewHolder, final TaskReceivedSubBean data) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_received_balloon_detail_show,null);
        ImageView ImgPreview = (ImageView) view.findViewById(R.id.image_view_balloon_container_preview);
        ImgPreview.setScaleType(ImageView.ScaleType.FIT_XY);
        ImageView ImgStart = (ImageView) view.findViewById(R.id.image_view_balloon_container_start);
        TextView tvPlace = (TextView) view.findViewById(R.id.tv_balloon_container_address);
        TextView tvCreateTime = (TextView) view.findViewById(R.id.tv_balloon_container_create_time);
        if(data.getBalloonDetail().getAttList().size() > 0) {
            switch (data.getBalloonDetail().getAttList().get(0).getMediaType()) {
                case ApplicationConstant.MEDIA_TYPE_PHOTO:
                    ImgStart.setVisibility(View.INVISIBLE);
                    Glide.with(context).load(Utils.getSmalleImageUrl(data.getBalloonDetail().getAttList().get(0).getName()
                            , 60, 60, context)).into(ImgPreview);
                    break;
                case ApplicationConstant.MEDIA_TYPE_VIDEO:
                    ImgPreview.setVisibility(View.VISIBLE);
                    ImgStart.setVisibility(View.VISIBLE);
                        Glide.with(context).load(Utils.getVideoPreviewImgUrl(data.getBalloonDetail().getAttList().get(0).getPreviewImg()
                        )).into(ImgPreview);
                    break;
            }
        }
        tvPlace.setText(data.getPlace());

        tvCreateTime.setText(MyTimeUtil.friendlyTime(MyTimeUtil.formatTimeWhole(data.getBalloonDetail().getCreateTime())));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BalloonItemInfoActivity.class);
                intent.putExtra("balloonId",data.getBalloonDetail().getBalloonId());
                context.startActivity(intent);
            }
        });
        viewHolder.takTypeInfoContanier.addView(view);
    }


    private void balloonViewShow(final ViewHolder viewHolder, final TaskReceivedSubBean data) {
        View balloonView = LayoutInflater.from(context).inflate(R.layout.task_received_balloon_view_show,null);
        LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        balloonView.setLayoutParams(lllp);
        CircleImageView userIcon = (CircleImageView) balloonView.findViewById(R.id.task_received_balloon_view_header_view);
        TextView userName = (TextView) balloonView.findViewById(R.id.task_received_balloon_view_user_name);
        TextView balloonCreateTime = (TextView) balloonView.findViewById(R.id.task_received_balloon_view_create_time);
        TextView balloonDepict = (TextView) balloonView.findViewById(R.id.task_received_balloon_view_depict);
        final TextView  careBtn = (TextView) balloonView.findViewById(R.id.task_received_balloon_view_care_button);
        careBtn.setClickable(true);
        if(data.getCreatePersonId() == SharePreferencesUtils.getLong(context,SharePrefConstant.MEMBER_ID,(long)0))
            careBtn.setVisibility(View.INVISIBLE);
        else {

            if (data.isFollow()) {
                careBtn.setText("已关注");
            } else {
                careBtn.setText("关注");
            }

            careBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.isFollow() && data.getCreatePersonId() != SharePreferencesUtils.getLong(context, SharePrefConstant.MEMBER_ID, (long) 0)) {
                        delFollow(buildRequestParams(data.getCreatePersonId()), careBtn, data);
                    } else {
                        addFollow(buildRequestParams(data.getCreatePersonId()), careBtn, data);
                    }
                }
            });
        }

        Glide.with(context).load(Utils.getSmalleImageUrl(data.getCreatePersonHead(), 48, 48, context))
                    .into(userIcon);
        userName.setText(data.getCreatePersonName());
        balloonCreateTime.setText(MyTimeUtil.friendlyTime(MyTimeUtil.formatTimeWhole(data.getCreateTime())));

//        balloonCreateTime.setText(MyTimeUtil.formatTimeZhShort(data.getCreateTime()));
        balloonDepict.setText(data.getOtherRequirements());
        viewHolder.taskReceivedContanier.addView(balloonView);
    }

    private void picTaskShow(ViewHolder viewHolder, TaskReceivedSubBean data) {

        View viewParent = LayoutInflater.from(context).inflate(R.layout.task_receive_pic_show_item,null);
        LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        viewParent.setLayoutParams(lllp);
        TextView tvDepict = (TextView) viewParent.findViewById(R.id.pic_show_item_depict);
        TextView tvDistance = (TextView) viewParent.findViewById(R.id.pic_show_item_distance);
        TextView tvCreatetime = (TextView) viewParent.findViewById(R.id.pic_show_item_createtime);
        ImageView taskStatusImg = (ImageView) viewParent.findViewById(R.id.pic_show_item_img_status);
        LinearLayout taskReceivedContainer = (LinearLayout) viewParent.findViewById(R.id.pic_show_item_container);
        taskReceivedContainer.removeAllViews();
        tvDistance.setText("距离伞的位置"+Utils.getDistance(data.getP_distance()));
        tvDepict.setText(data.getPicDepict());
//        tvCreatetime.setText(MyTimeUtil.formatTimeZhShort(data.getReceiveTime()));
        tvCreatetime.setText(MyTimeUtil.friendlyTime(MyTimeUtil.formatTimeWhole(data.getReceiveTime())));

        switch (data.getOrderState()){
            case 10:
                taskStatusImg.setVisibility(View.INVISIBLE);
                break;
            case 20://待确认
                taskStatusImg.setVisibility(View.INVISIBLE);
                break;
            case 30://已经采用
                taskStatusImg.setVisibility(View.VISIBLE);
                taskStatusImg.setImageDrawable(ContextCompat.getDrawable(context,R.mipmap.task_use));
                break;
            case 40://未采用
                taskStatusImg.setVisibility(View.VISIBLE);
                taskStatusImg.setImageDrawable(ContextCompat.getDrawable(context,R.mipmap.task_nouse));
                break;
        }
        final List<String> urlLists = new ArrayList<>();
        switch (data.getMediaType()){
            case ApplicationConstant.MEDIA_TYPE_PHOTO:
                //获取大图的url数组
                urlLists.clear();
                for(int i = 0;i < data.getImgList().size();i++){
                    String url = Utils.getBigImageUrl(SharePreferencesUtils.getLong(context, SharePrefConstant.MEMBER_ID,(long)0)
                    ,data.getImgList().get(i).getName(),
                    data.getImgList().get(i).getTaskOrderId(),
                    data.getImgList().get(i).getTaskOrderState());
                    urlLists.add(url);
                }

                int[] resIds = {R.id.glide_tag,R.id.glide_tag1,R.id.glide_tag2};
                for(int i = 0;i<data.getImgList().size();i++){
                    ImageView imageView = new ImageView(context);
                    imageView.setId(resIds[i]);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(Utils.dp2px(context,100f),Utils.dp2px(context,100f));
                    params1.setMargins(0,0,10,0);
                    imageView.setLayoutParams(params1);

                    String url = HttpUrlConstant.getSmallImageUrl+"?imgName="+data.getImgList().get(i).getName()
                            +"&width="+Utils.dp2px(context,100f)+"&height="+Utils.dp2px(context,100f);
                    Glide.with(context).load(url).into(imageView);
                    taskReceivedContainer.addView(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupPhotoView showPhoto = null;
                            switch (v.getId()){
                                case R.id.glide_tag:
                                    showPhoto = new PopupPhotoView(context,urlLists, 0);
                                    break;
                                case R.id.glide_tag1:
                                    showPhoto = new PopupPhotoView(context,urlLists, 1);
                                    break;
                                case R.id.glide_tag2:
                                    showPhoto = new PopupPhotoView(context,urlLists, 2);
                                    break;
                            }
                            showPhoto.showAtLocation(v, Gravity.NO_GRAVITY,0,0);
                        }
                    });
                }
                break;
            case ApplicationConstant.MEDIA_TYPE_VIDEO:
                View view = LayoutInflater.from(context).inflate(R.layout.viedeo_preview_item,null);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Utils.dp2px(context,200f),Utils.dp2px(context,200f));
                view.setLayoutParams(lp);
//                ImageView originPic = (ImageView) view.findViewById(R.id.video_preview_origin);
                CustomVideoView customVideoView = (CustomVideoView) view.findViewById(R.id.custom_view);

                if(data.getImgList().size() > 0) {

                    customVideoView.setData(data.getMediaType(), data.getImgList().get(0).getFileSize(),
                            data.getImgList().get(0).getTimeLength(), data.getImgList().get(0).getName(),
                            data.getImgList().get(0).getPreviewImg(),getClass().getName());
                }
                taskReceivedContainer.addView(view);
                break;
        }
        viewHolder.taskReceivedContanier.addView(viewParent);
    }

    private void dealWithBottomStatus(ViewHolder viewHolder, final TaskReceivedSubBean data) {
        viewHolder.taskInfoContainer.removeAllViews();
        View view1 = inflater.inflate(R.layout.task_send_confirm_item,null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                MATCH_PARENT);
        view1.setLayoutParams(params);
        viewHolder.taskInfoContainer.addView(view1);

        final TextView confirmBtn = (TextView) view1.findViewById(R.id.sub_item_confirm);
        TextView message = (TextView) view1.findViewById(R.id.sub_item_take_picture);
        confirmBtn.setBackgroundColor(ContextCompat.getColor(context,R.color.white));

        switch (data.getOrderState()){
            case 10://停止拍摄
                message.setText(data.getSymbol()+Utils.getTwoLastNumber(data.getTotalFee()));
                message.setTextColor(ContextCompat.getColor(context,R.color.gold));
                confirmBtn.setBackgroundColor(ContextCompat.getColor(context,R.color.green));
                confirmBtn.setText("拍摄("+data.getTimeOutStr()+")");
                confirmBtn.setTextColor(ContextCompat.getColor(context,R.color.white));
                confirmBtn.setClickable(true);
                confirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, TaskSendPhotoActivity.class);
                        intent.putExtra("subTaskId",data.getSubTaskId());
                        intent.putExtra("lat",data.getLatitude());
                        intent.putExtra("lng",data.getLongitude());
                        context.startActivity(intent);
                    }
                });
                break;
            case 20://待确认
                confirmBtn.setClickable(false);
                message.setText("拍摄: "+data.getDeliveryOrderNum());
                confirmBtn.setBackgroundColor(ContextCompat.getColor(context,R.color.white));
                confirmBtn.setText("等待伞主确认("+data.getTimeOutStr()+")");
                confirmBtn.setTextColor(ContextCompat.getColor(context,R.color.color_e64));
                break;
            case 30://已经采用
                confirmBtn.setClickable(false);
                message.setText("拍摄: "+data.getDeliveryOrderNum());
                confirmBtn.setBackgroundColor(ContextCompat.getColor(context,R.color.white));
                confirmBtn.setText("采用: "+data.getCompleteOrderNum());
                confirmBtn.setTextColor(ContextCompat.getColor(context,R.color.text_grey));

                break;
            case 40://未采用
                confirmBtn.setClickable(false);
                message.setText("拍摄: "+data.getDeliveryOrderNum());
                confirmBtn.setBackgroundColor(ContextCompat.getColor(context,R.color.white));
                confirmBtn.setText("采用: "+data.getCompleteOrderNum());
                confirmBtn.setTextColor(ContextCompat.getColor(context,R.color.text_grey));
                break;
        }
    }
    private void delFollow(final HashMap<String, String> params, final TextView viewHolder, final TaskReceivedSubBean data) {

        new AlertView("确定取消关注吗？", null, "取消", new String[]{"取消关注"}, null, context, AlertView.Style.ActionSheet,
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
//                                Log.d("加关注", response);
                                try {
                                    JSONObject object = new JSONObject(response);
                                    if (!object.isNull("statusCode")) {
                                        int errCode = object.optInt("statusCode");
                                        if (1 == errCode) {
                                            data.setFollow(false);
                                            viewHolder.setText("关注");
                                        }else if(-999 == errCode){
                                            HttpApi api = new ExitSystemHttpImpl();
                                            api.httpRequest(context, new IResponseApi() {
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
                        }, params);
                    }
                }).setCancelable(true).show();

    }

    private void addFollow(HashMap<String, String> params, final TextView viewHolder, final TaskReceivedSubBean data) {
        OkHttpClientManager.postAsyn(HttpUrlConstant.balloonFollowAdd, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(context,"噢！网络不给力");
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (!object.isNull("statusCode")) {
                        int errCode = object.optInt("statusCode");
                        if (1 == errCode) {
                            data.setFollow(true);
                            viewHolder.setText("已关注");
                        }else if(-999 ==errCode){
                            HttpApi api = new ExitSystemHttpImpl();
                            api.httpRequest(context, new IResponseApi() {
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
        }, params);
    }

    private HashMap<String, String> buildRequestParams(long personId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("followUserId", personId + "");
        params.put("memberId", SharePreferencesUtils.getLong(context, SharePrefConstant.MEMBER_ID, (long) 0) + "");
        params.put("lat", "");
        params.put("lng", "");
        params.put("clientId", SharePreferencesUtils.getString(context, SharePrefConstant.INSTALL_CODE, ""));
        params.put("deviceType", "android");
        return params;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout taskReceivedContanier;//接受到的图片（可能有多张）或视频
        private LinearLayout takTypeInfoContanier;//用户信息或者气球信息
        private LinearLayout taskInfoContainer;//底部七种状态

        public ViewHolder(View itemView) {
            super(itemView);

//            taskStatusImg = (ImageView) itemView.findViewById(R.id.task_received_status_image);
            taskReceivedContanier = (LinearLayout) itemView.findViewById(R.id.task_received_imglist_container_mine);
            takTypeInfoContanier = (LinearLayout) itemView.findViewById(R.id.task_type_info_container);
            taskInfoContainer = (LinearLayout) itemView.findViewById(R.id.task_received_status_container);


        }
    }
}
