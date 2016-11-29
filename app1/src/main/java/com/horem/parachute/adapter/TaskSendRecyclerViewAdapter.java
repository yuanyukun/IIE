package com.horem.parachute.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat.LayoutParams;
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
import android.widget.TextView;

import com.app.utils.Md5Encrypt;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.common.ApplicationConstant;
import com.horem.parachute.R;
import com.horem.parachute.autoupdate.internal.NetworkUtil;
import com.horem.parachute.balloon.BalloonItemInfoActivity;
import com.horem.parachute.common.CustomLoading;
import com.horem.parachute.customview.CustomVideoView;
import com.horem.parachute.menu.PopupPhotoView;
import com.horem.parachute.mine.ShowMessageInfoActivity;
import com.horem.parachute.mine.TaskSendConfirm;
import com.horem.parachute.mine.bean.MineSendTaskSubBean;
import com.horem.parachute.mine.bean.taskSendSubBean;
import com.horem.parachute.task.TaskPayActivity;
import com.horem.parachute.util.MyTimeUtil;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.horem.parachute.util.Utils;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by user on 2016/3/18.
 */
public class TaskSendRecyclerViewAdapter extends CommonAdapter<TaskSendRecyclerViewAdapter.ViewHolder>{

    private static final int TASK_CONFIRM = 1001;
    private  List<taskSendSubBean> lists;
    private Activity context;
    private LayoutInflater inflater;
    private CustomLoading customLoadingDialog;

    public TaskSendRecyclerViewAdapter(Activity context, List<taskSendSubBean> lists) {
        super(context,lists);
        this.lists = lists;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
//        return super.getItemCount();
        return lists.size();
    }
    public void RefreshSubTaskStatus(List<taskSendSubBean> lists){
        this.lists = lists;
        notifyDataSetChanged();
    }
    public void RrefreshData(List<taskSendSubBean> mLists) {
        this.lists = mLists;
        notifyDataSetChanged();
    }

    public  List<taskSendSubBean> getData() {
        return lists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = View.inflate(mContext, R.layout.recycler_view_item_task_send, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        taskSendSubBean bean = lists.get(position);

        viewHolder.taskId.setText(bean.getNo());//伞号
        viewHolder.FeeStr.setText(bean.getSymbol()+Utils.getTwoLastNumber(bean.getTotal_fee()));//总费用
        viewHolder.depict.setText(bean.getOtherRequirements());//描述
        viewHolder.address.setText(bean.getPlace());//地址
        viewHolder.createTime.setTextColor(ContextCompat.getColor(context,R.color.text_grey));
        viewHolder.createTime.setText(MyTimeUtil.friendlyTime(MyTimeUtil.formatTimeWhole(bean.getCreateTime())));//订单创建时间
        if(bean.isPrivate()){
            viewHolder.isPrivate.setVisibility(View.VISIBLE);
        }else{
            viewHolder.isPrivate.setVisibility(View.INVISIBLE);
        }
        //气球栏
        dealWithBalloonDetail(viewHolder,bean);
        //处理底部状态栏 和 订单状态类型显示
        dealWithBottomStatus(viewHolder, bean,bean.getSubTaskId(),position);
        //发布任务带有图片或视频的显示
        dealWithAttlistShow(viewHolder, bean);


    }

    private void dealWithBalloonDetail(ViewHolder viewHolder, final taskSendSubBean bean) {
        if(bean.getBalloonDetail() != null){
            View view = inflater.inflate(R.layout.task_send_balloon_detail,null);
            ImageView startBtn = (ImageView) view.findViewById(R.id.tv_task_send_balloon_detail_start);
            ImageView previewImg = (ImageView) view.findViewById(R.id.tv_task_send_balloon_detail_preview);
            TextView    tvPlace = (TextView) view.findViewById(R.id.tv_task_send_balloon_detail_place);
            TextView    tvTime = (TextView) view.findViewById(R.id.tv_task_send_balloon_detail_time);
            viewHolder.balloonContainer.removeAllViews();
            viewHolder.balloonContainer.addView(view);
            tvPlace.setText(bean.getPlace());
            tvTime.setText(MyTimeUtil.friendlyTime(MyTimeUtil.formatTimeWhole(bean.getCreateTime())));

            switch (bean.getMediaType()){
                case ApplicationConstant.MEDIA_TYPE_PHOTO:
                    startBtn.setVisibility(View.INVISIBLE);
                    Glide.with(context).load(Utils.getSmalleImageUrl(bean.getBalloonDetail().getAttList().get(0).getName(),60,60,context))
                            .into(previewImg);
                    break;
                case ApplicationConstant.MEDIA_TYPE_VIDEO:
                    startBtn.setVisibility(View.VISIBLE);
                    Glide.with(context).load(Utils.getVideoPreviewImgUrl(bean.getBalloonDetail().getAttList().get(0).getPreviewImg()))
                            .into(previewImg);
                    break;
            }
            viewHolder.balloonContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, BalloonItemInfoActivity.class);
                    intent.putExtra("balloonId",bean.getBalloonDetail().getBalloonId());
                    context.startActivity(intent);
                }
            });
        }else{
            viewHolder.balloonContainer.removeAllViews();
        }
    }

    private void dealWithAttlistShow(ViewHolder viewHolder, final taskSendSubBean bean) {
        if(bean.getAttList().size()> 0){
            switch (bean.getMediaType()){
                case ApplicationConstant.MEDIA_TYPE_PHOTO:
                    ImageView imageView = new ImageView(context);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setBackgroundColor(ContextCompat.getColor(context, R.color.gray));
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Utils.dp2px(context,200),
                            Utils.dp2px(context,200));
                    imageView.setLayoutParams(lp);
                    viewHolder.imgContainer.removeAllViews();
                    viewHolder.imgContainer.addView(imageView);
                    Glide.with(context).load(Utils.getSmalleImageUrl(bean.getAttList().get(0).getName(),200,200,context,
                        bean.getSubTaskId(),bean.getTaskState()))
                            .into(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            List<String> list = new ArrayList<String>();
                            list.add(Utils.getBigImageUrl(SharePreferencesUtils.getLong(context,SharePrefConstant.MEMBER_ID,(long)0),
                                    bean.getAttList().get(0).getName(), bean.getSubTaskId(),bean.getTaskState()));
                            PopupPhotoView popupPhotoView = new PopupPhotoView(context,list,0);
                            popupPhotoView.showAtLocation(v, Gravity.NO_GRAVITY,0,0);
                        }
                    });
                    break;
                case ApplicationConstant.MEDIA_TYPE_VIDEO:
                    viewHolder.imgContainer.removeAllViews();
                    View view = inflater.inflate(R.layout.preview_video_item,null);
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(Utils.dp2px(context,200),
                            Utils.dp2px(context,200));
                    view.setLayoutParams(lp1);
                    viewHolder.imgContainer.removeAllViews();
                    viewHolder.imgContainer.addView(view);

                    CustomVideoView originImage = (CustomVideoView) view.findViewById(R.id.task_send_video_view);
                    originImage.setData(bean.getMediaType(),0,0,bean.getAttList().get(0).getName()
                            ,bean.getAttList().get(0).getPreviewImg(),Utils.getRunningActivityName(context));
                    break;
            }
        }else{
                    viewHolder.imgContainer.removeAllViews();
        }
    }

    private void dealWithBottomStatus(ViewHolder viewHolder, taskSendSubBean bean, final String taskId, final int position) {
        final taskSendSubBean innerBean = bean;
        switch (bean.getTaskState()){
            case -2://已关闭
                viewHolder.viewGroup.removeAllViews();
                viewHolder.taskStatus.setVisibility(View.VISIBLE);
                viewHolder.taskStatus.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.task_close));
                View view = inflater.inflate(R.layout.task_send_confirm_item,null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        MATCH_PARENT);
                view.setLayoutParams(params);
                viewHolder.viewGroup.addView(view);
                TextView closeReason = (TextView) view.findViewById(R.id.sub_item_confirm);
                closeReason.setText(bean.getCloseReason());
                closeReason.setTextColor(ContextCompat.getColor(context,R.color.color_e64));

                break;
            case -1://已关闭
                viewHolder.taskStatus.setVisibility(View.VISIBLE);
                viewHolder.taskStatus.setImageDrawable(ContextCompat.getDrawable(context,R.mipmap.task_close));
                viewHolder.viewGroup.removeAllViews();
                View view1 = inflater.inflate(R.layout.task_send_confirm_item,null);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        MATCH_PARENT);
                view1.setLayoutParams(params1);
                viewHolder.viewGroup.addView(view1);
                TextView closeReason1 = (TextView) view1.findViewById(R.id.sub_item_confirm);
                closeReason1.setText(bean.getCloseReason());
                closeReason1.setTextColor(ContextCompat.getColor(context,R.color.color_e64));
                break;
            case 10://待付款
                viewHolder.taskStatus.setVisibility(View.VISIBLE);
                viewHolder.taskStatus.setImageDrawable(ContextCompat.getDrawable(context,R.mipmap.task_nopay));
                viewHolder.viewGroup.removeAllViews();

                View view2 = inflater.inflate(R.layout.task_send_confirm_item,null);
                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        MATCH_PARENT);
                view2.setLayoutParams(params2);
                viewHolder.viewGroup.addView(view2);
                TextView message = (TextView) view2.findViewById(R.id.sub_item_take_picture);
                final TextView confirmBtn = (TextView) view2.findViewById(R.id.sub_item_confirm);
                message.setText("");
                confirmBtn.setBackgroundColor(ContextCompat.getColor(context,R.color.gold));
                confirmBtn.setText("付款("+bean.getTimeOutStr()+")");
                confirmBtn.setTextColor(ContextCompat.getColor(context,R.color.white));
                confirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, TaskPayActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("taskId",taskId);
                        bundle.putString("intentType","MineTaskSend");
                        intent.putExtra("bundle",bundle);
                        context.startActivity(intent);
                    }
                });
                break;
            case 20://待接收
                viewHolder.taskStatus.setVisibility(View.INVISIBLE);
                viewHolder.message.setVisibility(View.INVISIBLE);
                viewHolder.viewGroup.removeAllViews();
                View view3 = inflater.inflate(R.layout.task_send_confirm_item,null);
                LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        MATCH_PARENT);
                view3.setLayoutParams(params3);
                viewHolder.viewGroup.addView(view3);
                TextView message1 = (TextView) view3.findViewById(R.id.sub_item_take_picture);
                TextView confirmBtn1 = (TextView) view3.findViewById(R.id.sub_item_confirm);
                message1.setText("拍摄：0");
                confirmBtn1.setTextColor(ContextCompat.getColor(context,R.color.color_e64));
                confirmBtn1.setText("暂无拍摄");
                break;
            case 30://待确认
                viewHolder.taskStatus.setVisibility(View.INVISIBLE);
                viewHolder.viewGroup.removeAllViews();
                View view4 = inflater.inflate(R.layout.task_send_confirm_item,null);
                LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        MATCH_PARENT);
                view4.setLayoutParams(params4);
                viewHolder.viewGroup.addView(view4);

                final TextView confirmBtn2 = (TextView) view4.findViewById(R.id.sub_item_confirm);
                TextView message2 = (TextView) view4.findViewById(R.id.sub_item_take_picture);
                message2.setText("拍摄："+bean.getDeliveryOrderNum());
                confirmBtn2.setBackgroundColor(ContextCompat.getColor(context,R.color.color_green));
                if(bean.getTimeOutStr() != null )
                    confirmBtn2.setText("确认"+"("+bean.getTimeOutStr()+")");
                else
                    confirmBtn2.setText("确认"+"( )");
                confirmBtn2.setTextColor(ContextCompat.getColor(context,R.color.white));

                confirmBtn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, TaskSendConfirm.class);
                        intent.putExtra("value",innerBean);
                        intent.putExtra("position",position);
                        context.startActivityForResult(intent,TASK_CONFIRM);
                    }
                });
                break;
            case 40://已经采用
                viewHolder.taskStatus.setImageDrawable(ContextCompat.getDrawable(context,R.mipmap.task_use));
                viewHolder.viewGroup.removeAllViews();
                viewHolder.message.setVisibility(View.VISIBLE);
                viewHolder.viewGroup.addView(viewHolder.message);

                switch (bean.getMediaType()){
                    case 10:
                        viewHolder.message.setText("收到"+bean.getDeliveryOrderNum()+"份照片");

                        break;
                    case 30:
                        viewHolder.message.setText("收到"+bean.getDeliveryOrderNum()+"段频伞");
                        break;
                }
                viewHolder.message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(context,ShowMessageInfoActivity.class);
                        intent.putExtra("value",innerBean);
                        context.startActivity(intent);
                    }
                });
                break;
            case 50://未采用
                viewHolder.taskStatus.setImageDrawable(ContextCompat.getDrawable(context,R.mipmap.task_nouse));
                viewHolder.viewGroup.removeAllViews();
                viewHolder.message.setVisibility(View.VISIBLE);
                viewHolder.viewGroup.addView(viewHolder.message);
                switch (bean.getMediaType()){
                    case 10:
                        viewHolder.message.setText("收到"+bean.getDeliveryOrderNum()+"份照片");
                        break;
                    case 30:
                        viewHolder.message.setText("收到"+bean.getDeliveryOrderNum()+"段频伞");
                        break;
                }
                viewHolder.message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ShowMessageInfoActivity.class);
                        intent.putExtra("value",innerBean);
                        context.startActivity(intent);
                    }
                });
                break;
        }
    }
    /**
     * 进度加载动画
     */

    protected void startLoading(){
        if(customLoadingDialog == null){
            customLoadingDialog = CustomLoading.CreateLoadingDialog(context);
            customLoadingDialog.setMessage("正在下载...");
            customLoadingDialog.setCanceledOnTouchOutside(false);//不允许点击取消

            Window wd = customLoadingDialog.getWindow();
            WindowManager.LayoutParams lp = wd.getAttributes();
            lp.alpha = 0.5f;
            wd.setAttributes(lp);
        }
        customLoadingDialog.show();
    }  /**
     * 进度加载动画
     */
    protected void startLoading(String msg){
        if(customLoadingDialog == null){
            customLoadingDialog = CustomLoading.CreateLoadingDialog(context);
            customLoadingDialog.setMessage(msg);
            customLoadingDialog.setCanceledOnTouchOutside(false);//不允许点击取消

            Window wd = customLoadingDialog.getWindow();
            WindowManager.LayoutParams lp = wd.getAttributes();
            lp.alpha = 0.5f;
            wd.setAttributes(lp);
        }
        customLoadingDialog.show();
    }
    /**
     * 关闭加载动画
     */
    protected void stopLoading() {
        if (customLoadingDialog != null) {
            customLoadingDialog.dismiss();
            customLoadingDialog.dismiss();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView taskId;    //伞号
        private TextView isPrivate;
        private TextView FeeStr;    //金钱
        private TextView depict;    //描述
        private TextView address;   //地址
        private TextView createTime;//时间
        private ImageView taskStatus;//任务状态
        private TextView message;    //任务收到信息
        private ViewGroup viewGroup;
        private LinearLayout imgContainer;
        private LinearLayout balloonContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            taskId = (TextView) itemView.findViewById(R.id.task_send_task_id);
            isPrivate = (TextView) itemView.findViewById(R.id.task_send_is_private);
            FeeStr = (TextView) itemView.findViewById(R.id.task_send_money);
            depict = (TextView) itemView.findViewById(R.id.send_task_depict);
            address = (TextView) itemView.findViewById(R.id.send_task_address);
            createTime = (TextView) itemView.findViewById(R.id.send_task_create_time);
            message = (TextView) itemView.findViewById(R.id.task_send_message);
            taskStatus = (ImageView) itemView.findViewById(R.id.send_task_image);
            viewGroup = (ViewGroup) itemView.findViewById(R.id.task_send_container);
            imgContainer = (LinearLayout) itemView.findViewById(R.id.send_task_imglist_container);
            balloonContainer = (LinearLayout) itemView.findViewById(R.id.balloon_container);
        }
    }
}
