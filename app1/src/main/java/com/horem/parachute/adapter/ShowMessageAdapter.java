package com.horem.parachute.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
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
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.common.ApplicationConstant;
import com.common.HttpUrlConstant;
import com.horem.parachute.R;
import com.horem.parachute.autoupdate.internal.NetworkUtil;
import com.horem.parachute.common.CustomLoading;
import com.horem.parachute.customview.CustomVideoView;
import com.horem.parachute.customview.HackyViewPager;
import com.horem.parachute.menu.PopupPhotoView;
import com.horem.parachute.mine.AppUserInfo;
import com.horem.parachute.mine.bean.IsCheckedBean;
import com.horem.parachute.mine.bean.OrderListBean;
import com.horem.parachute.mine.bean.ShowMessageSubBean;
import com.horem.parachute.util.MyTimeUtil;
import com.horem.parachute.util.ScreenBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.horem.parachute.util.Utils;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentNavigableMap;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by yuanyukun on 2016/5/26.
 */

public class ShowMessageAdapter extends  RecyclerView.Adapter<ShowMessageAdapter.ViewHolder> implements
View.OnClickListener{

    private List<ShowMessageSubBean> lists;
    private Context context;
    private int clickedPosition;
    private  List<String> urlLists;
    private CustomLoading customLoadingDialog;

    public ShowMessageAdapter(Context context, List<ShowMessageSubBean> lists) {
        this.lists = lists;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return  lists.size();
    }

    public void setData(List<ShowMessageSubBean> mLists) {
        this.lists = mLists;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View layout = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.show_message_adapter_view, viewGroup, false);
        return new ViewHolder(layout);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        
        final ShowMessageSubBean bean = lists.get(position);
         urlLists = new ArrayList<>();
        for(int i = 0;i < bean.getPhotoList().size();i++){
            String url = Utils.getBigImageUrl(SharePreferencesUtils.getLong(context,SharePrefConstant.MEMBER_ID,(long)0)
                    ,bean.getPhotoList().get(i).getName(),
                    bean.getPhotoList().get(i).getTaskOrderId(),
                    bean.getPhotoList().get(i).getTaskOrderState());
            urlLists.add(url);
        }
        int OrderStatue = bean.getOrderState();
        switch (OrderStatue){
            //已采用
            case 30:
                viewHolder.taskOrderStatus.setVisibility(View.VISIBLE);
                viewHolder.taskOrderStatus.setImageDrawable(ContextCompat.getDrawable(context,R.mipmap.task_use));
                break;
            //未采用
            case 40:
                viewHolder.taskOrderStatus.setVisibility(View.VISIBLE);
                viewHolder.taskOrderStatus.setImageDrawable(ContextCompat.getDrawable(context,R.mipmap.task_nouse));
                break;
        }

        switch (bean.getMediaType()){
            case ApplicationConstant.MEDIA_TYPE_PHOTO:
                viewHolder.viewGroup.removeAllViews();
                int[] resIds = {R.id.glide_tag,R.id.glide_tag1,R.id.glide_tag2};
                for(int i = 0;i<bean.getPhotoList().size();i++){
                    ImageView Img = new ImageView(context);
                    Img.setId(resIds[i]);
                    Img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Glide.with(context).load(urlLists.get(i)).into(Img);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dp2px(context,100f),Utils.dp2px(context,100f));
                    params.setMargins(0,0,10,0);
                    Img.setLayoutParams(params);

                    viewHolder.viewGroup.addView(Img);
                    Img.setOnClickListener(this);
                }
                break;

            case ApplicationConstant.MEDIA_TYPE_VIDEO:
                viewHolder.viewGroup.removeAllViews();

                View view = LayoutInflater.from(context).inflate(R.layout.viedeo_preview_item,null);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Utils.dp2px(context,200f),Utils.dp2px(context,200f));
                view.setLayoutParams(params);
                CustomVideoView customVideoView = (CustomVideoView) view.findViewById(R.id.custom_view);
                customVideoView.setData(bean.getMediaType(),bean.getPhotoList().get(0).getFileSize()
                ,bean.getPhotoList().get(0).getTimeLength(),bean.getPhotoList().get(0).getName(),
                        bean.getPhotoList().get(0).getPreviewImg(),getClass().getName());
                break;
        }
        viewHolder.distance.setText("距伞的位置"+Math.round(bean.getP_distance()*1000+0.5)+"m");
        viewHolder.depict.setText(bean.getPicDepict());
        viewHolder.userName.setText(bean.getReceivePersonName());
        viewHolder.createTime.setText(MyTimeUtil.friendlyTime(MyTimeUtil.formatTimeWhole(bean.getCreateTime())));
        Glide.with(context).load(Utils.getHeadeUrl(bean.getReceivePersonHead()))
                .into(viewHolder.userHeadIcon);
        viewHolder.userHeadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AppUserInfo.class);
                intent.putExtra("memberId",bean.getReceivePersonId());
                context.startActivity(intent);
            }
        });

    }




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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView userName;     //
        private CircleImageView userHeadIcon;//
        private TextView distance;     //
        private TextView createTime;   //
        private ViewGroup viewGroup;
        private TextView depict;
        private ImageView taskOrderStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            userHeadIcon = (CircleImageView) itemView.findViewById(R.id.task_confirm_head_icon);
            userName = (TextView) itemView.findViewById(R.id.task_confirm_user_name);
            userName = (TextView) itemView.findViewById(R.id.task_confirm_user_name);
            distance = (TextView) itemView.findViewById(R.id.confirm_task_adapter_distance);
            depict = (TextView) itemView.findViewById(R.id.confirm_task_order_depict);
            createTime = (TextView) itemView.findViewById(R.id.confirm_task_adapter_time);
            taskOrderStatus = (ImageView) itemView.findViewById(R.id.show_message_task_status);
            viewGroup = (ViewGroup) itemView.findViewById(R.id.task_confirm_container);
        }
    }
}
