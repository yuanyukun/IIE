package com.horem.parachute.mine;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.common.HttpUrlConstant;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.adapter.CommonAdapter;
import com.horem.parachute.adapter.ProfessionRecyclerAdapter;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.mine.bean.MessageBean;
import com.horem.parachute.mine.bean.ModifyProfessionBean;
import com.horem.parachute.mine.bean.ProfessionBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;

public class ModifyProfessionActivity extends BaseActivity {



    private RecyclerView professionRecycler;
    private ProfessionRecyclerAdapter mAdapter;
    private ArrayList<String> professionNames = new ArrayList<>();
    private ArrayList<Boolean> checkLists = new ArrayList<>();
    private ArrayList<ProfessionBean> mlists =  new ArrayList<>();
    private int professionId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profession);

        init();
        initData();
    }

    private void initData() {
        startLoading();
        HashMap<String,String> params = new HashMap<>();
        params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng","");
        params.put("lat","");
        params.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");

        OkHttpClientManager.postAsyn(HttpUrlConstant.memberProfessionList, new OkHttpClientManager.ResultCallback<ModifyProfessionBean>() {
            @Override
            public void onError(Request request, Exception e) {
                stopLoading();
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
//                Log.d(getClass().getName(),e.toString());
            }

            @Override
            public void onResponse(ModifyProfessionBean response) {
                stopLoading();
                if(response.getStatusCode() == 1){
                    professionNames.clear();
                    for (int i = 0;i < response.getResult().size();i++){
                        mlists.addAll(response.getResult());
                        professionNames.add(response.getResult().get(i).getName());
                        if(response.getResult().get(i).getId() == professionId){
                            checkLists.add(true);
                        }else
                        checkLists.add(false);

                    }
                    mAdapter.refreshChecks(professionNames,checkLists);
                }else if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        },params);

    }

    private void init() {

        initTitleView();
        setTitleName("职业");
        setLeftButtonText(SharePreferencesUtils.getString(this,SharePrefConstant.MEMBER_NAME,""));
        setRightButtonText("保存");


        professionId = getIntent().getIntExtra("value",0);
        professionRecycler = (RecyclerView) findViewById(R.id.profession_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        professionRecycler.setLayoutManager(manager);

        mAdapter = new ProfessionRecyclerAdapter(this,professionNames,checkLists);
        mAdapter.setOnItemClickListener(new CommonAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                professionId = mlists.get(position).getId();
                resetCheckLists();
                checkLists.set(position,true);
                mAdapter.refreshChecks(professionNames,checkLists);

            }
        });
        professionRecycler.setAdapter(mAdapter);
    }

    private void resetCheckLists() {
        for(int i = 0;i < checkLists.size();i++)
            checkLists.set(i,false);
    }

    @Override
    protected void OnRightButtonClicked() {
        super.OnRightButtonClicked();
        HashMap<String,String> params = new HashMap<>();
        params.put("professionId",professionId+"");
        params.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lat","");
        params.put("lng","");
        params.put("clientId",SharePreferencesUtils.getString(this,SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");
        Log.d("professionId:  ",professionId+"");
        OkHttpClientManager.postAsyn(HttpUrlConstant.memberProfessionEdit, new OkHttpClientManager.ResultCallback<MessageBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(MessageBean response) {
                if(response.getStatusCode() == 1){
                    finish();
                }else if(response.getStatusCode() == -999){
                    exitApp();
                }else
                    ToastManager.show(getApplicationContext(),"噢，网络不给力！");
            }
        },params);
    }
}
