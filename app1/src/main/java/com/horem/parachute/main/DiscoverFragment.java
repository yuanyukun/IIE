package com.horem.parachute.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.common.HttpUrlConstant;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.adapter.FragmentDiscoverBallonAdapter;
import com.horem.parachute.adapter.FragmentDiscoverThemeAdapter;
import com.horem.parachute.adapter.FragmentDiscoverVideoAdapter;
import com.horem.parachute.balloon.BalloonRecommendAndMoreActivity;
import com.horem.parachute.balloon.Bean.AdListBean;
import com.horem.parachute.balloon.Bean.FragmentDiscoveryBean;
import com.horem.parachute.balloon.Bean.ThemeListBean;
import com.horem.parachute.common.CustomLoading;
import com.horem.parachute.customview.LocalHolderImageView;
import com.horem.parachute.customview.PullDownElasticImp;
import com.horem.parachute.customview.PullDownScrollView;
import com.horem.parachute.main.bean.AdsComprotiomUrlBean;
import com.horem.parachute.main.bean.BalloonListSubBeanItem;
import com.horem.parachute.mine.UserGuidActivity;
import com.horem.parachute.util.ScreenBean;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.horem.parachute.util.Utils;
import com.http.request.HttpApi;
import com.http.request.IResponseApi;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yuanyukun on 2016/6/17.
 */
public class DiscoverFragment extends Fragment implements View.OnClickListener,AMapLocationListener{

    private static final int recommendNum = 6;//推荐主题·推荐气球·推荐用户数量
    private CustomLoading customLoadingDialog;
    private TextView titleName;
    private TextView userInfo;
    private TextView titleNext;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    //
    private LatLng currentLatLng;
    private boolean isFirstEnter = true;


    private RecyclerView themeRecycler;
    private RecyclerView recommendRecycler;
    private RecyclerView videoRecycler;


    private FragmentDiscoverThemeAdapter themeAdapter;
    private FragmentDiscoverBallonAdapter recommendAdapter;
    private FragmentDiscoverVideoAdapter MoreVideoAdapter;

//    private RelativeLayout  themeRL;
    private RelativeLayout  recomendRL;
    private RelativeLayout  moreVideRL;

    private ArrayList<AdListBean> AdLists = new ArrayList<>();
    private ArrayList<ThemeListBean> themeLists = new ArrayList<>();
    private ArrayList<BalloonListSubBeanItem> balloonLists = new ArrayList<>();
    private ArrayList<BalloonListSubBeanItem> videoBalloonLists = new ArrayList<>();

    private PullDownScrollView scrollView;
    private ConvenientBanner convenientBanner;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discovery, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        titleName = (TextView) view.findViewById(R.id.title_name);
        userInfo = (TextView) view.findViewById(R.id.title_back);
        titleNext = (TextView) view.findViewById(R.id.title_next);
        titleNext.setVisibility(View.INVISIBLE);
        titleName.setText("发现");
        userInfo.setVisibility(View.INVISIBLE);

        initMap();

        scrollView = (PullDownScrollView) view.findViewById(R.id.discover_scroll_view);
        scrollView.setRefreshTips("下拉刷新","释放立即刷新","刷新完成");
        scrollView.setRefreshListener(new PullDownScrollView.RefreshListener() {
            @Override
            public void onRefresh(PullDownScrollView view) {
                initData();
            }
        });
        scrollView.setPullDownElastic(new PullDownElasticImp(getContext()));

        themeRecycler = (RecyclerView) view.findViewById(R.id.fragment_discover_theme_grid);
        themeRecycler.setFocusable(false);
        recommendRecycler = (RecyclerView) view.findViewById(R.id.fragment_discover_balloon_grid);
        recommendRecycler.setFocusable(false);
        videoRecycler = (RecyclerView) view.findViewById(R.id.fragment_discover_video_grid);
        videoRecycler.setFocusable(false);

//        themeRL = (RelativeLayout) view.findViewById(R.id.fragment_discover_theme_rl);
        recomendRL = (RelativeLayout) view.findViewById(R.id.fragment_discover_ballon_rl);
        moreVideRL = (RelativeLayout) view.findViewById(R.id.fragment_discover__video_rl);

        convenientBanner = (ConvenientBanner) view.findViewById(R.id.banner_splash_pager);


//        themeRL.setOnClickListener(this);
        recomendRL.setOnClickListener(this);
        moreVideRL.setOnClickListener(this);
        GridLayoutManager themeManager = null;
        GridLayoutManager balloonManager = null;
        GridLayoutManager videoManager = null;
        if(Utils.px2dp(getContext(),ScreenBean.getScreenWidth()) <= 320){
//             themeManager = new GridLayoutManager(getContext(),2);
             balloonManager = new GridLayoutManager(getContext(),2);
             videoManager = new GridLayoutManager(getContext(),2);
        }else {
//             themeManager = new GridLayoutManager(getContext(),3);
             balloonManager = new GridLayoutManager(getContext(),3);
             videoManager = new GridLayoutManager(getContext(),3);
        }

//        themeRecycler.setLayoutManager(themeManager);
        recommendRecycler.setLayoutManager(balloonManager);
        videoRecycler.setLayoutManager(videoManager);

//        themeAdapter = new FragmentDiscoverThemeAdapter(getContext(),themeLists);
//        themeRecycler.setAdapter(themeAdapter);

        recommendAdapter = new FragmentDiscoverBallonAdapter(getContext(),balloonLists);
        recommendRecycler.setAdapter(recommendAdapter);

        MoreVideoAdapter = new FragmentDiscoverVideoAdapter(getContext(),videoBalloonLists);
        videoRecycler.setAdapter(MoreVideoAdapter);
    }


    private void initMap() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getContext());
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //启动定位
        mLocationClient.startLocation();
    }

    private void initData() {
        startLoading();
        HashMap<String,String> params = new HashMap<>();
        params.put("recommendThemeNum",String.valueOf(recommendNum));
        params.put("recommendBalloonNum",String.valueOf(recommendNum));
        params.put("hotBalloonNum",String.valueOf(recommendNum));
        params.put("recommendUserNum",String.valueOf(recommendNum));

        params.put("memberId", SharePreferencesUtils.getLong(getContext(), SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng",currentLatLng.longitude+"");
        params.put("lat",currentLatLng.latitude+"");
        params.put("clientId", SharePreferencesUtils.getString(getContext(), SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");
//        for(Map.Entry<String,String> entry:params.entrySet()){
//            Log.d(getClass().getName(),entry.getKey()+": "+entry.getValue());
//        }
        OkHttpClientManager.postAsyn(HttpUrlConstant.balloonDiscovery, new OkHttpClientManager.ResultCallback<FragmentDiscoveryBean>() {
            @Override
            public void onError(Request request, Exception e) {
                scrollView.finishRefresh();
                stopLoading();
                ToastManager.show(getContext(),"噢！网络不给力");
            }

            @Override
            public void onResponse(FragmentDiscoveryBean response) {
                scrollView.finishRefresh();
                stopLoading();
//                Log.d("发现列表数据",new Gson().toJson(response));
                if(response.getStatusCode() == 1){
                    dealWithData(response);
                }else if(response.getStatusCode() == -999){
                    HttpApi httpApi = new ExitSystemHttpImpl();
                    httpApi.httpRequest(getContext(), new IResponseApi() {
                        @Override
                        public void onSuccess(Object object) {

                        }

                        @Override
                        public void onFailed(Exception e) {

                        }
                    },new HashMap<String, String>());
                }else{
                    ToastManager.show(getContext(),"噢！网络不给力");
                }

            }
        },params);
    }

    private void dealWithData(FragmentDiscoveryBean response) {

       AdLists =  response.getResult().getAdsList();
        dealWithAds(AdLists);

//        //更多主题栏
//        if(response.getResult().isMoreRecommendTheme()){
//            themeRL.setVisibility(View.VISIBLE);
//        }else{
//            themeRL.setVisibility(View.GONE);
//        }
        //热门推荐
        if(response.getResult().isMoreRecommendBalloon()){
            recomendRL.setVisibility(View.VISIBLE);
        }else{
            recomendRL.setVisibility(View.GONE);
        }

        if(response.getResult().isMoreHotBalloon()){
            moreVideRL.setVisibility(View.VISIBLE);
        }else{
            moreVideRL.setVisibility(View.GONE);
        }
        Log.d("是否有更多热门视频",response.getResult().isMoreHotBalloon()+"");
        if(response.getResult().getRecommendThemeList() != null){

             themeLists =  response.getResult().getRecommendThemeList();
            themeAdapter.RefreshData(themeLists);
        }

        if(response.getResult().getRecommendBalloonList() != null){

            balloonLists=  response.getResult().getRecommendBalloonList();
            recommendAdapter.RefreshData(balloonLists);
        }

        if(response.getResult().getHotBalloonList() != null){

             videoBalloonLists =  response.getResult().getHotBalloonList();
            MoreVideoAdapter.RefreshData(videoBalloonLists);
        }
    }

    private void dealWithAds(final ArrayList<AdListBean> adLists) {
        List<String> urlLists = new ArrayList<>();
        for(AdListBean bean:adLists){
//            checkAdsInfo(bean.getAdsId());
            urlLists.add(Utils.getSmalleImageUrl(bean.getCoverImg(),360,100,getContext()));
        }
        convenientBanner.setPages(new CBViewHolderCreator<LocalHolderImageView>() {
                    @Override
                    public LocalHolderImageView createHolder() {
                        return new LocalHolderImageView();
                    }
                }, urlLists)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{R.drawable.dot_normal, R.drawable.dot_focused})
                //设置指示器的方向
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
        //设置翻页的效果，不需要翻页效果可用不设
        //.setPageTransformer(Transformer.DefaultTransformer);    集成特效之后会有白屏现象，新版已经分离，如果要集成特效的例子可以看Demo的点击响应。
//        convenientBanner.setManualPageable(false);//设置不能手动影响

        convenientBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                checkAdsInfo(adLists.get(position).getAdsId(),adLists.get(position).getAdsName());
            }
        });
    }
    private void  checkAdsInfo(int adsId, final String adsName){
        HashMap<String,String> params = new HashMap<>();
        params.put("adsId",adsId+"");

        params.put("memberId", SharePreferencesUtils.getLong(getContext(), SharePrefConstant.MEMBER_ID,(long)0)+"");
        params.put("lng",currentLatLng.longitude+"");
        params.put("lat",currentLatLng.latitude+"");
        params.put("clientId", SharePreferencesUtils.getString(getContext(), SharePrefConstant.INSTALL_CODE,""));
        params.put("deviceType","android");
        OkHttpClientManager.postAsyn(HttpUrlConstant.checkAdsInfo, new OkHttpClientManager.ResultCallback<AdsComprotiomUrlBean>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastManager.show(getContext(),"噢，网络不给力！");
            }

            @Override
            public void onResponse(AdsComprotiomUrlBean response) {
                Log.d(getClass().getName(),new Gson().toJson(response));
                if(response.getStatusCode() == 1){
                    Intent intent = new Intent(getContext(), UserGuidActivity.class);
                    intent.putExtra("url",response.getResult());
                    intent.putExtra("value",3);
                    intent.putExtra("name",adsName);
                    startActivity(intent);
                }else if(response.getStatusCode() == -999){
                    HttpApi httpApi = new ExitSystemHttpImpl();
                    httpApi.httpRequest(getContext(), new IResponseApi() {
                        @Override
                        public void onSuccess(Object object) {

                        }

                        @Override
                        public void onFailed(Exception e) {

                        }
                    },new HashMap<String, String>());
                }else
                    ToastManager.show(getContext(),"噢，网络不给力！");
            }
        },params);

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
//            case R.id.fragment_discover_theme_rl:
//                intent = new Intent(getContext(), BalloonItemInfoActivity.class);
//
//                break;
            case R.id.fragment_discover_ballon_rl:
                intent = new Intent(getContext(), BalloonRecommendAndMoreActivity.class);
                intent.putExtra("memberId",SharePreferencesUtils.getLong(getContext(),SharePrefConstant.MEMBER_ID
                ,(long)0));
                intent.putExtra("type","recommend");
                break;
            case R.id.fragment_discover__video_rl:
                intent = new Intent(getContext(), BalloonRecommendAndMoreActivity.class);
                intent.putExtra("memberId",SharePreferencesUtils.getLong(getContext(),SharePrefConstant.MEMBER_ID
                        ,(long)0));
                intent.putExtra("type","more");
                break;
        }
        startActivity(intent);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                currentLatLng = new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude());
                if(isFirstEnter){
                    isFirstEnter = false;
                    initData();
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }
    /**
     * 进度加载动画
     */
    protected void startLoading(){
        if(customLoadingDialog == null){
            customLoadingDialog = CustomLoading.CreateLoadingDialog(getContext());
            customLoadingDialog.setMessage("正在加载...");
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
            customLoadingDialog = CustomLoading.CreateLoadingDialog(getContext());
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
    protected void stopLoading(){
        if(customLoadingDialog != null){
            customLoadingDialog.dismiss();
            customLoadingDialog.dismiss();
        }
    }
}
