package com.horem.parachute.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.common.HttpUrlConstant;
import com.horem.parachute.R;
import com.horem.parachute.balloon.Bean.FlowersListBean;
import com.horem.parachute.balloon.Bean.ThemeListBean;
import com.horem.parachute.common.CustomLoading;
import com.horem.parachute.customview.CustomHeadView;
import com.horem.parachute.main.ExitSystemHttpImpl;
import com.horem.parachute.main.bean.BalloonListSubBeanItem;
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
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by yuanyukun on 2016/6/21.
 */
public class SendFlowerAdapter extends RecyclerView.Adapter<SendFlowerAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<FlowersListBean> lists;
    private CustomLoading customLoadingDialog;

    public SendFlowerAdapter(Context context, List<FlowersListBean> lists) {
        this.context = context;
        this.lists = lists;
        inflater = LayoutInflater.from(context);
    }

    public void RefreshData(List<FlowersListBean> list){
        this.lists= list;
        notifyDataSetChanged();
    }
    @Override
    public SendFlowerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = inflater.inflate(R.layout.send_flowers_list, parent, false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(final SendFlowerAdapter.ViewHolder holder, final int position) {
        final FlowersListBean subItem = lists.get(position);
        holder.userName.setText(subItem.getCreateUserName());
        holder.flowersNum.setText("送"+subItem.getFlowerNumber()+"朵鲜花");

        holder.customerUserHead.showHeadView(subItem.getCreateUserHead(),subItem.getAuthType()
                ,subItem.getCreateUserId(),true);
        holder.btnCare.setClickable(true);
        if(subItem.getCreateUserId() == SharePreferencesUtils.getLong(context, SharePrefConstant.MEMBER_ID,(long)0)){
            holder.btnCare.setVisibility(View.INVISIBLE);
        }else{
            holder.btnCare.setVisibility(View.VISIBLE);
            if(subItem.isFollow()){
                holder.btnCare.setText("已关注");
            }else{
                holder.btnCare.setText("关注");
            }
        }
        holder.btnCare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,String> params = buildRequestParams(subItem);
                if(!subItem.isFollow()){
                    addFollow(params,holder,position);
                }else{
                    delFollow(params,holder,position);
                }
            }
        });
    }
    private void delFollow(final HashMap<String, String> params, final ViewHolder holder, final int dataPosition) {

        new AlertView("确定取消关注吗？", null, "取消", new String[]{"取消关注"},null, context, AlertView.Style.ActionSheet,
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

//                                Log.d("加关注",response);
                                try {
                                    JSONObject object = new JSONObject(response);
                                    if(!object.isNull("statusCode")){
                                        int errCode = object.optInt("statusCode");
                                        if(1 == errCode){
                                            lists.get(dataPosition).setFollow(false);
                                            holder.btnCare.setText("关注");
                                            notifyDataSetChanged();
                                        }else if(-999 == errCode){
                                           HttpApi httpApi = new ExitSystemHttpImpl();
                                            httpApi.httpRequest(context, new IResponseApi() {
                                                @Override
                                                public void onSuccess(Object object) {

                                                }

                                                @Override
                                                public void onFailed(Exception e) {

                                                }
                                            },new HashMap<String, String>());
                                        }else
                                        ToastManager.show(context,"噢！网络不给力");
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
                            holder.btnCare.setText("已关注");
                            notifyDataSetChanged();
                        }else if(errCode == -999){
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

    private HashMap<String,String> buildRequestParams(FlowersListBean subItem) {
        HashMap<String,String> params = new HashMap<>();
        params.put("followUserId",subItem.getCreateUserId()+"");
        params.put("memberId",SharePreferencesUtils.getLong(context,SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lat","");
        params.put("lng","");
        params.put("clientId",SharePreferencesUtils.getString(context,SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");
        return  params;
    }
    @Override
    public int getItemCount() {
        return lists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView userName;
        private TextView flowersNum;
        private CustomHeadView customerUserHead;
        private TextView btnCare;
        public ViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.tv_send_flower_list_user_name);
            flowersNum = (TextView) itemView.findViewById(R.id.tv_send_flower_list_flower_num);
            customerUserHead = (CustomHeadView) itemView.findViewById(R.id.send_flowers_list_user_icon);
            btnCare = (TextView) itemView.findViewById(R.id.btn_send_flowers_list_care);
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
