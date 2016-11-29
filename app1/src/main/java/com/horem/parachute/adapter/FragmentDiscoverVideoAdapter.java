package com.horem.parachute.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.common.ApplicationConstant;
import com.horem.parachute.R;
import com.horem.parachute.balloon.BalloonItemInfoActivity;
import com.horem.parachute.common.CustomLoading;
import com.horem.parachute.main.bean.BalloonListSubBeanItem;
import com.horem.parachute.util.ScreenBean;
import com.horem.parachute.util.Utils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by yuanyukun on 2016/6/21.
 */
public class FragmentDiscoverVideoAdapter extends RecyclerView.Adapter<FragmentDiscoverVideoAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<BalloonListSubBeanItem> lists;
    private CustomLoading customLoadingDialog;

    public FragmentDiscoverVideoAdapter(Context context, List<BalloonListSubBeanItem> lists) {
        this.context = context;
        this.lists = lists;
        inflater = LayoutInflater.from(context);
    }

    public void RefreshData(List<BalloonListSubBeanItem> list){
        this.lists= list;
        notifyDataSetChanged();
    }
    @Override
    public FragmentDiscoverVideoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = inflater.inflate(R.layout.fragement_discover_balloon, parent, false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(final FragmentDiscoverVideoAdapter.ViewHolder holder, final int position) {
        final BalloonListSubBeanItem subItem = lists.get(position);
        //设置子项的宽高
        RelativeLayout.LayoutParams lp = null;
        if(Utils.px2dp(context, ScreenBean.getScreenWidth()) > 320){
            lp  = new RelativeLayout.LayoutParams(ScreenBean.getScreenWidth()/3,ScreenBean.getScreenWidth()/3);
        }else{
            lp = new RelativeLayout.LayoutParams(ScreenBean.getScreenWidth()/2,ScreenBean.getScreenWidth()/2);
        }
        holder.rootView.setLayoutParams(lp);

        //预览图
        switch (subItem.getMediaType()){
            case ApplicationConstant.MEDIA_TYPE_PHOTO:
                holder.startPlay.setVisibility(View.INVISIBLE);
                if(subItem.getAttList() != null && subItem.getAttList().size() > 0){
                    Glide.with(context).load(Utils.getSmalleImageUrl(subItem.getAttList().get(0).getName(),200,200,context))
                            .into(holder.original);
                }

                break;
            case ApplicationConstant.MEDIA_TYPE_VIDEO:
                holder.startPlay.setVisibility(View.VISIBLE);
                if(subItem.getAttList() != null && subItem.getAttList().size() > 0){
                    Glide.with(context).load(Utils.getVideoPreviewImgUrl(subItem.getAttList().get(0).getPreviewImg()))
                            .into(holder.original);
                }
                break;
        }
        //头像
        Glide.with(context).load(Utils.getHeadeUrl(subItem.getCreatePersonHead()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.userIcon)
        ;
        //花的数量
//        holder.tvFlowersNum.setText(subItem.getFlowersNum()+"");
        holder.address.setText(subItem.getBalloonAddress());

        holder.original.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BalloonItemInfoActivity.class);
                intent.putExtra("balloonId",subItem.getBalloonId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView original;
        private ImageView startPlay;
        private CircleImageView userIcon;
//        private TextView tvFlowersNum;
        private TextView address;
        private RelativeLayout rootView;
        public ViewHolder(View itemView) {
            super(itemView);

            original = (ImageView) itemView.findViewById(R.id.img_recommend_original);
            startPlay = (ImageView) itemView.findViewById(R.id.recommend_start);
            userIcon = (CircleImageView) itemView.findViewById(R.id.img_circle_recommend_user_header);
//            tvFlowersNum = (TextView) itemView.findViewById(R.id.tv_recommend_flowers_numger);
            address = (TextView) itemView.findViewById(R.id.tv_recommend_address);
            rootView = (RelativeLayout) itemView.findViewById(R.id.relative_container);
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
    }
    /**
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
}
