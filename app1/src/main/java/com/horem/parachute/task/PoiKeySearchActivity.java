package com.horem.parachute.task;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.adapter.SimpleRecylerAdapter;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.util.ToastManager;

import java.util.ArrayList;
import java.util.List;

public class PoiKeySearchActivity extends BaseActivity implements PoiSearch.OnPoiSearchListener {

    private SearchView PoiEdit;
    private RecyclerView mRecy;
    private SimpleRecylerAdapter simpleRecylerAdapter;
    private List<PoiItem> mLists;

    private LatLng currentLatLng;
    private String keyWord;
//    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索

    private CustomApplication application;
    private Tracker mTracker;
    private static  final  String TAG = "PoiKeyAty";
    @Override
    protected void onStart() {
        super.onStart();
        //google analytics
        application = (CustomApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(this.getPackageName()+" [Android] " + TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Share")
                .build());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_key_search);
        init();
    }

    private void init() {
        initTitleView();
        setRightButtonText("搜索");
        setLeftButtonText("取消");
        setTitleName("搜索位置");


        mLists = new ArrayList<>();
        PoiEdit = (SearchView) findViewById(R.id.poi_key_search_edit);
        PoiEdit.setIconifiedByDefault(false);
        PoiEdit.setSubmitButtonEnabled(true);
        PoiEdit.setQueryHint("请输入地址");

        PoiEdit.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                keyWord = newText;
                return true;
            }
        });



        mRecy = (RecyclerView) findViewById(R.id.poi_key_search_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecy.setLayoutManager(manager);
        simpleRecylerAdapter = new SimpleRecylerAdapter(this,mLists);
        mRecy.setAdapter(simpleRecylerAdapter);

        simpleRecylerAdapter.setOnItemClicked(new SimpleRecylerAdapter.OnItemClicked() {
            @Override
            public void subItemClicked(View view, LatLng date) {
                currentLatLng = date;
                goBack(currentLatLng);
            }
        });
    }

    @Override
    protected void OnLeftButtonClicked() {
        super.OnLeftButtonClicked();
        goBack(currentLatLng);
    }

    private void goBack(LatLng mLatLng) {
        Intent intent = new Intent(this,PointTaskLocationActivity.class);
        if(null != mLatLng){
            intent.putExtra("latlng",mLatLng);
        }
        setResult(RESULT_OK,intent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack(currentLatLng);
    }

    @Override
    protected void OnRightButtonClicked() {
        super.OnRightButtonClicked();
//        keyWord = PoiEdit.get().toString();
        if(!TextUtils.isEmpty(keyWord)){
                currentPage = 0;
                query = new PoiSearch.Query(keyWord, "","");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
                query.setPageSize(10);// 设置每页最多返回多少条poiitem
                query.setPageNum(currentPage);// 设置查第一页

                poiSearch = new PoiSearch(this, query);
                poiSearch.setOnPoiSearchListener(this);
                poiSearch.searchPOIAsyn();
        }else
            ToastManager.show(this,"请输入您的搜索内容");
    }


    @Override
    public void onPoiSearched(PoiResult poiResult, int rCode) {
        Log.d("PoiKeySearchTesult",new Gson().toJson(poiResult.getQuery()));
        if (rCode == 1000) {
            if (poiResult != null && poiResult.getQuery() != null) {// 搜索poi的结果
                if (poiResult.getQuery().equals(query)) {// 是否是同一条
                    poiResult = poiResult;
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    Log.d("PoiKeySearchTesult",new Gson().toJson(poiResult.getPois()));
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                    if (poiItems != null && poiItems.size() > 0) {
                        simpleRecylerAdapter.setData(poiItems);
                        simpleRecylerAdapter.notifyDataSetChanged();
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        ToastManager.show(PoiKeySearchActivity.this, "搜索结果为空");
//                        showSuggestCity(suggestionCities);
                    } else {
//                        ToastManager.show(PoiKeySearchActivity.this, "");
                    }
                }
            } else {
//                ToastManager.show(this, String.valueOf(rCode));
            }
        } else {
//            ToastManager.show(this, String.valueOf(rCode));
        }
    }
    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
        ToastManager.show(PoiKeySearchActivity.this, infomation);

    }
    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
}
