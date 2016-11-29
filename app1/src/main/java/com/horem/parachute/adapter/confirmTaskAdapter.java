package com.horem.parachute.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.horem.parachute.menu.PopupPhotoView;
import com.horem.parachute.mine.AppUserInfo;
import com.horem.parachute.mine.bean.IsCheckedBean;
import com.horem.parachute.mine.bean.OrderListBean;
import com.horem.parachute.mine.bean.ShowMessageSubBean;
import com.horem.parachute.util.MyTimeUtil;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.horem.parachute.util.Utils;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 2016/3/18.
 */
public class confirmTaskAdapter extends  RecyclerView.Adapter<confirmTaskAdapter.ViewHolder> implements View.OnClickListener{

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;
    private  List<OrderListBean> lists;
    private Context context;
    private LayoutInflater inflater;
    private View mHeaderView;
    private ArrayList<String> urlLists;
    private CustomLoading customLoadingDialog;
    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(View view,int position);
    }

    public void setOnItemClickListener(OnItemClickListener li) {
        mListener = li;
    }

    private List<IsCheckedBean> mlists;
    public confirmTaskAdapter(Context context, List<OrderListBean> lists,List<IsCheckedBean> mlists) {
        this.lists = lists;
        this.mlists = mlists;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? lists.size() : lists.size() + 1;
    }

    public void setData(List<OrderListBean> mLists,List<IsCheckedBean> IsCheckList) {
        lists = mLists;
        this.mlists = IsCheckList;
    }
    public ArrayList<IsCheckedBean> getIsCheckedList(){
        return (ArrayList<IsCheckedBean>) mlists;
    }

    public void addHeader(View view){
        this.mHeaderView = view;
        notifyItemInserted(0);
    }

    @Override
    public int getItemViewType(int position) {
        if(mHeaderView == null) return TYPE_NORMAL;
        if(position == 0) return TYPE_HEADER;
        return TYPE_NORMAL;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(mHeaderView != null && viewType == TYPE_HEADER)
            return new ViewHolder(mHeaderView);
        View layout = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.task_confirm_adapter_view, viewGroup, false);
        return new ViewHolder(layout);
    }

    public boolean getIsChecked(int position){
        return mlists.get(position).isChecked();
    }

    public void setIsChecked(int position,boolean value){
        mlists.get(position).setChecked(value);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        if(getItemViewType(position) == TYPE_HEADER) return;
        //列表下标与数据下标的统一
        final int pos = getRealPosition(viewHolder);
        if(viewHolder instanceof ViewHolder) {
            if(mListener == null) return;
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(v, pos);
                }
            });
        }
        urlLists = new ArrayList<>();
        for(int i = 0;i < lists.get(pos).getPhotoList().size();i++){
            String url = Utils.getSmalleImageUrl(lists.get(pos).getPhotoList().get(i).getName(),100,100,context
                ,lists.get(pos).getPhotoList().get(i).getId(),lists.get(pos).getPhotoList().get(i).getTaskOrderState());
/*
            String url = HttpUrlConstant.getSmallImageUrl+"?imgName="+lists.get(pos).getPhotoList().get(i).getName()
                    +"&width="+Utils.dp2px(context,100f)+"&height="+Utils.dp2px(context,100f);
*/
            urlLists.add(url);
        }

        if(mlists.get(pos).isChecked()){
            viewHolder.checked.setImageDrawable(ContextCompat.getDrawable(context,R.mipmap.check_48));
        }else{
            viewHolder.checked.setImageDrawable(ContextCompat.getDrawable(context,R.mipmap.uncheck_48));
        }

        switch (lists.get(pos).getMediaType()){
            case ApplicationConstant.MEDIA_TYPE_PHOTO:
                viewHolder.viewGroup.removeAllViews();
                int[] resId = { R.id.glide_tag,R.id.glide_tag1,R.id.glide_tag2 };
                for(int i = 0;i<lists.get(pos).getPhotoList().size();i++){
                    ImageView imageView = new ImageView(context);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setId(resId[i]);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dp2px(context,100f),Utils.dp2px(context,100f));
                    params.setMargins(0,0,10,0);
                    imageView.setLayoutParams(params);

                    Glide.with(context).load(Utils.getSmalleImageUrl(lists.get(pos).getPhotoList().get(i).getName(),100,100,context
                            ,lists.get(pos).getPhotoList().get(i).getId(),lists.get(pos).getPhotoList().get(i).getTaskOrderState())).into(imageView
                    );
                    imageView.setOnClickListener(this);
                    viewHolder.viewGroup.addView(imageView);
                }
                break;
            case ApplicationConstant.MEDIA_TYPE_VIDEO:

                viewHolder.viewGroup.removeAllViews();
                View view = LayoutInflater.from(context).inflate(R.layout.viedeo_preview_item,null);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Utils.dp2px(context,200f),Utils.dp2px(context,200f));
                view.setLayoutParams(params);
                CustomVideoView customVideoView = (CustomVideoView) view.findViewById(R.id.custom_view);
//                originPic.setBackgroundColor(ContextCompat.getColor(context,R.color.black));
                customVideoView.setData(lists.get(pos).getMediaType(),lists.get(pos).getPhotoList().get(0).getFileSize()
                ,lists.get(pos).getPhotoList().get(0).getTimeLength(), lists.get(pos).getPhotoList().get(0).getName(),lists.get(pos).getPhotoList().get(0).getPreviewImg()
                ,Utils.getRunningActivityName(context));
                break;
        }
        viewHolder.distance.setText("距伞的位置"+Math.round(lists.get(pos).getP_distance()*1000+0.5)+"m");
        viewHolder.depict.setText(lists.get(pos).getPicDepict());
        viewHolder.userName.setText(lists.get(pos).getReceivePersonName());
        viewHolder.createTime.setText(MyTimeUtil.hxDateTimeString(lists.get(pos).getCreateTime()));

        Glide.with(context).load(Utils.getHeadeUrl(lists.get(pos).getReceivePersonHead())).into(viewHolder.userHeadIcon);
        viewHolder.userHeadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(context, AppUserInfo.class);
                intent.putExtra("memberId",lists.get(pos).getReceivePersonId());
                context.startActivity(intent);
            }
        });
    }
    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
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
        private ImageView checked;     //
        private CircleImageView userHeadIcon;//
        private TextView distance;     //
        private TextView createTime;   //
        private ViewGroup viewGroup;
        private TextView depict;

        public ViewHolder(View itemView) {
            super(itemView);
            checked = (ImageView)itemView.findViewById(R.id.task_confirm_checked);
            userHeadIcon = (CircleImageView) itemView.findViewById(R.id.task_confirm_head_icon);
            userName = (TextView) itemView.findViewById(R.id.task_confirm_user_name);
            distance = (TextView) itemView.findViewById(R.id.confirm_task_adapter_distance);
            depict = (TextView) itemView.findViewById(R.id.confirm_task_order_depict);
            createTime = (TextView) itemView.findViewById(R.id.confirm_task_adapter_time);
            viewGroup = (ViewGroup) itemView.findViewById(R.id.task_confirm_container);
        }
    }
}
