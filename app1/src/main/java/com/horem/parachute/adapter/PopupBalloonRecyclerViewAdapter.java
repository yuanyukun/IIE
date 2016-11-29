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
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.transcode.TranscoderRegistry;
import com.common.ApplicationConstant;
import com.common.HttpUrlConstant;
import com.horem.parachute.R;
import com.horem.parachute.balloon.BalloonItemInfoActivity;
import com.horem.parachute.balloon.Bean.BaseResultBean;
import com.horem.parachute.balloon.Bean.IsFollowBean;
import com.horem.parachute.balloon.SendBalloonActivity;
import com.horem.parachute.balloon.WatchingAliveActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.common.DataCleanManager;
import com.horem.parachute.customview.ActionSheetDialog;
import com.horem.parachute.customview.CustomHeadView;
import com.horem.parachute.login.Activity_Login;
import com.horem.parachute.main.ExitSystemHttpImpl;
import com.horem.parachute.main.bean.BalloonListSubBeanItem;
import com.horem.parachute.mine.AppUserInfo;
import com.horem.parachute.task.TaskInformationActivity;
import com.horem.parachute.task.TaskSendPhotoActivity;
import com.horem.parachute.task.TaskSendVideoActivity;
import com.horem.parachute.task.bean.BalloonBean;
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
 * Created by user on 2016/3/18.
 */
public class PopupBalloonRecyclerViewAdapter extends CommonAdapter<PopupBalloonRecyclerViewAdapter.ViewHolder>{


    private ArrayList<BalloonBean> balloonBeanList;
    String url="";
    public PopupBalloonRecyclerViewAdapter(Context mContext, ArrayList<BalloonBean> lists, Activity activity) {
        super(mContext);
        this.balloonBeanList = lists;
    }

    @Override
    public int getItemCount() {
        return balloonBeanList.size();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = View.inflate(mContext, R.layout.popup_balloon_item, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        super.onBindViewHolder(viewHolder,position);
        final BalloonBean subItem = balloonBeanList.get(position);

        viewHolder.userHeader.showHeadView(subItem.getCreatePersonHead(),subItem.getAuthType()
        ,subItem.getCreatePersonId(),true);

        viewHolder.tvAddress.setText(subItem.getBalloonAddress());
        if(position == balloonBeanList.size()-1){
            viewHolder.rightBlank.setVisibility(View.VISIBLE);
        }
        
        viewHolder.taskName.setText(subItem.getCreatePersonName());
        if(subItem.getCreatePersonId() == SharePreferencesUtils.getLong(mContext, SharePrefConstant.MEMBER_ID,(long)0)){
            viewHolder.careBtn.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.careBtn.setVisibility(View.VISIBLE);
        }
        //TODO
        RefreshFollow(subItem, viewHolder,position);
        //
        viewHolder.careBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CustomApplication.getInstance().isLogin()){
                    HashMap<String,String> params = buildRequestParams(subItem);
                    if(!subItem.isFollow()){
                        addFollow(params,viewHolder,position);
                    }else {
                        delFollow(params, viewHolder, position);
                    }
                }else{
                    Intent intent = new Intent(mContext,Activity_Login.class);
                    mContext.startActivity(intent);
                }

            }
        });


        //显示预览图
        viewHolder.previewImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        url = getBitmapUrl(balloonBeanList.get(position),viewHolder);
        Glide.with(mContext).load(url).into(viewHolder.previewImg);

        viewHolder.previewImg.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("点击获取的预览图",getBitmapUrl(balloonBeanList.get(position)));
                        Intent intent = new Intent(mContext, BalloonItemInfoActivity.class);
                        intent.putExtra("balloonId",subItem.getBalloonId());
                        mContext.startActivity(intent);
                    }
                }
        );
        viewHolder.taskInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                if(CustomApplication.getInstance().isLogin()) {
                     intent = new Intent(mContext, WatchingAliveActivity.class);
                    intent.putExtra("id", subItem.getBalloonId());
//                intent.putExtra("type",subItem.getMediaType());
                }else{
                    intent = new Intent(mContext,Activity_Login.class);
                }
                mContext.startActivity(intent);
            }
        });
    }

    private void RefreshFollow(final BalloonBean subItem, final ViewHolder viewHolder, final int position) {
        HashMap<String,String> params = new HashMap<>();
        params.put("followUserId",subItem.getCreatePersonId()+"");
        params.put("memberId",SharePreferencesUtils.getLong(mContext,SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lat","");
        params.put("lng","");
        params.put("deviceType","android");
        params.put("clientId",SharePreferencesUtils.getString(mContext,SharePrefConstant.INSTALL_CODE,""));
        OkHttpClientManager.postAsyn(HttpUrlConstant.isFollow, new OkHttpClientManager.ResultCallback<IsFollowBean>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse( IsFollowBean response) {
                if(response.getStatusCode() == 1){
                    if(response.isResult()){
                        viewHolder.careBtn.setText("已关注");
                    }else{
                        viewHolder.careBtn.setText("关注");
                    }
                }else if(response.getStatusCode() == -999){
                    HttpApi httpApi = new ExitSystemHttpImpl();
                    httpApi.httpRequest(mContext, new IResponseApi() {
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

    private HashMap<String,String> buildRequestParams(BalloonBean subItem) {
        HashMap<String,String> params = new HashMap<>();
        params.put("followUserId",subItem.getCreatePersonId()+"");
        params.put("memberId",SharePreferencesUtils.getLong(mContext,SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lat","");
        params.put("lng","");
        params.put("clientId",SharePreferencesUtils.getString(mContext,SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");
        return  params;
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
                           balloonBeanList.get(position).setFollow(true);
                            holder.careBtn.setText("已关注");
                            notifyDataSetChanged();
                        }else if(errCode== -999){
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




    private void delFollow(final HashMap<String, String> params, final ViewHolder holder, final int dataPosition) {

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
//                                        Log.d(getClass().getName(),response);
                                        try {
                                            JSONObject object = new JSONObject(response);
                                            if(!object.isNull("statusCode")){
                                                int errCode = object.optInt("statusCode");
                                                if(1 == errCode){
                                                    balloonBeanList.get(dataPosition).setFollow(false);
                                                    holder.careBtn.setText("关注");
                                                    notifyDataSetChanged();
                                                }else if( errCode == -999){

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

    @Override
    public int getItemViewType(int position) {
        return balloonBeanList.get(position).getMediaType();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private View rightBlank;
        private Button taskInfoBtn;
        private TextView taskName;
        private CustomHeadView userHeader;
        private TextView careBtn;
        private ImageView previewImg;
        private ImageView startImg;
        private TextView tvAddress;

        public ViewHolder(View itemView) {
            super(itemView);
            rightBlank = itemView.findViewById(R.id.right_blank);
            taskInfoBtn = (Button) itemView.findViewById(R.id.btn_popup_balloon_see_info);
            careBtn  = (TextView) itemView.findViewById(R.id.tv_popup_balloon_care_btn);

            taskName = (TextView) itemView.findViewById(R.id.tv_popup_balloon_user_name);
            userHeader = (CustomHeadView) itemView.findViewById(R.id.circle_image_popup_balloon_user_head);

            previewImg = (ImageView) itemView.findViewById(R.id.img_popup_balloon_original);
            startImg = (ImageView) itemView.findViewById(R.id.img_popup_balloon_start);
            tvAddress = (TextView) itemView.findViewById(R.id.tv_popup_balloon_address);
        }
    }
    private String getBitmapUrl(BalloonBean item,ViewHolder viewHolder) {
        String result = "";
        if(item.getAttList().size() > 0) {
            switch (item.getMediaType()) {
                case ApplicationConstant.MEDIA_TYPE_PHOTO:
                    viewHolder.startImg.setVisibility(View.INVISIBLE);
                    result = Utils.getSmalleImageUrl(item.getAttList().get(0).getName(), 220, 220, mContext);
                    break;
                case ApplicationConstant.MEDIA_TYPE_VIDEO:
                    viewHolder.startImg.setVisibility(View.VISIBLE);
                    result = Utils.getVideoPreviewImgUrl(item.getAttList().get(0).getPreviewImg());
                    break;
            }
        }
        return result;
    }
    private String getBitmapUrl(BalloonBean item) {
        String result = "";
        if(item.getAttList().size() > 0) {
            switch (item.getMediaType()) {
                case ApplicationConstant.MEDIA_TYPE_PHOTO:
                    result = Utils.getSmalleImageUrl(item.getAttList().get(0).getName(), 220, 220, mContext);
                    break;
                case ApplicationConstant.MEDIA_TYPE_VIDEO:
                    result = Utils.getVideoPreviewImgUrl(item.getAttList().get(0).getPreviewImg());
                    break;
            }
        }
        return result;
    }
}
