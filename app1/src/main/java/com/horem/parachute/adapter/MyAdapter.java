package com.horem.parachute.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.utils.Md5Encrypt;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.common.ApplicationConstant;
import com.common.HttpUrlConstant;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.autoupdate.internal.NetworkUtil;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.common.CustomLoading;
import com.horem.parachute.customview.CustomVideoView;
import com.horem.parachute.menu.PopupPhotoView;
import com.horem.parachute.mine.bean.MinePhotoSubBean;
import com.horem.parachute.util.ScreenBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.Utils;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by jianghejie on 15/11/26.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {


    private  List<MinePhotoSubBean> datas = null;
    private List<String> bigPicUrl = new ArrayList<>();
    private List<Integer> picPosition = new ArrayList<>();
    public  CustomLoading customLoadingDialog;
    private Activity context;
    private int currentClickedPosition;
    public MyAdapter(ArrayList<MinePhotoSubBean> datas,Activity context) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return datas.get(position).getMediaType();
    }
    public void RefreshDatas(List<MinePhotoSubBean> datas){
        this.datas = datas;
        notifyDataSetChanged();
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.viedeo_preview_item,viewGroup,false);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        MinePhotoSubBean bean = datas.get(position);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ScreenBean.getScreenWidth()/3,ScreenBean.getScreenWidth()/3);
        viewHolder.rootView.setLayoutParams(layoutParams);
        viewHolder.customVideoView.setData(bean.getMediaType(),bean.getFileSize()
            ,bean.getTimeLength(),bean.getName(),bean.getPreviewImg(),Utils.getRunningActivityName(context));
        viewHolder.customVideoView.isPrivate(bean.isPrivate());
        viewHolder.customVideoView.setSmallSize();
    }
    //获取数据的数量
    @Override
    public int getItemCount() {
        return datas.size();
    }
    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rootView;
        private CustomVideoView customVideoView;
        public ViewHolder(View view){
            super(view);
            rootView  = (RelativeLayout) view.findViewById(R.id.root_view);
            customVideoView = (CustomVideoView) view.findViewById(R.id.custom_view);
        }
    }
}
