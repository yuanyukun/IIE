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
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.common.ApplicationConstant;
import com.common.HttpUrlConstant;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.autoupdate.internal.NetworkUtil;
import com.horem.parachute.balloon.BalloonItemInfoActivity;
import com.horem.parachute.balloon.Bean.AdListBean;
import com.horem.parachute.balloon.Bean.ThemeListBean;
import com.horem.parachute.common.CustomLoading;
import com.horem.parachute.main.bean.BalloonListSubBeanItem;
import com.horem.parachute.main.bean.GetExtroInfoBean;
import com.horem.parachute.menu.PopupPhotoView;
import com.horem.parachute.mine.AppUserInfo;
import com.horem.parachute.mine.ShowLocationActivity;
import com.horem.parachute.util.MyTimeUtil;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.horem.parachute.util.Utils;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by yuanyukun on 2016/6/21.
 */
public class FragmentDiscoverThemeAdapter extends RecyclerView.Adapter<FragmentDiscoverThemeAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<ThemeListBean> lists;

    public FragmentDiscoverThemeAdapter(Context context, List<ThemeListBean> lists) {
        this.context = context;
        this.lists = lists;
        inflater = LayoutInflater.from(context);
    }

    public void RefreshData(List<ThemeListBean> list){
        this.lists= list;
        notifyDataSetChanged();
    }
    @Override
    public FragmentDiscoverThemeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = inflater.inflate(R.layout.fragement_discover_theme, parent, false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(final FragmentDiscoverThemeAdapter.ViewHolder holder, final int position) {
        ThemeListBean subItem = lists.get(position);
        holder.AdStr.setText(subItem.getThemeName());

    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView AdStr;
        public ViewHolder(View itemView) {
            super(itemView);
            AdStr = (TextView) itemView.findViewById(R.id.discover_theme_ll);
        }
    }
}
