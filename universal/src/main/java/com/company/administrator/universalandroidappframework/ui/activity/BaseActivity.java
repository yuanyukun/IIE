package com.company.administrator.universalandroidappframework.ui.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.company.administrator.universalandroidappframework.R;

import static com.company.administrator.universalandroidappframework.ui.activity.BaseActivity.ViewType.CONTAINER_LAYOUT;

public class BaseActivity extends AppCompatActivity {
    private FrameLayout headerLayout;
    private FrameLayout containerLayout;
    private FrameLayout loadingView;
    private FrameLayout errorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBaseData();
        super.setContentView(R.layout.activity_base);
        initView();
        initHeaderLayout();
        initLoadingView();
    }

    private void initBaseData() {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    }
    private void initView() {
        headerLayout = (FrameLayout) findViewById(R.id.fl_base_header );
        containerLayout = (FrameLayout) findViewById(R.id.fl_base_container );
        loadingView =  (FrameLayout) findViewById(R.id.fl_base_loading);
        errorView = (FrameLayout) findViewById(R.id.fl_base_error );
    }

    private void initHeaderLayout() {

    }

    private void initLoadingView() {

    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
//        super.setContentView(layoutResID);
        View view = getLayoutInflater().inflate(layoutResID,containerLayout,true);
        initAcitivy();
    }

    private void initAcitivy() {

    }

    @Override
    public void setContentView(View view) {
//        super.setContentView(view);
        containerLayout.addView(view);
        initAcitivy();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
//        super.setContentView(view, params);\
        this.containerLayout.addView(view,params);
        initAcitivy();

    }

    /**
     * 添加HeaderView
     */
    public void setHeaderLayout(@LayoutRes int HeaderLayoutResId){
        View view = getLayoutInflater().inflate(HeaderLayoutResId,headerLayout,true);
    }
    public void setHeaderLayout(View view){
        this.headerLayout.addView(view );
    }
    /**
     * 添加loadingView
     */
    public void setLoadingView(@LayoutRes int loadingViewResId){
        View view = getLayoutInflater().inflate(loadingViewResId,loadingView,true);
    }
    public void setLoadingView(View view){
        this.loadingView.addView(view);
    }
    /**
     * 添加errorView
     */
    public void setErrorView(@LayoutRes int errorViewResId){
        View view = getLayoutInflater().inflate(errorViewResId,loadingView,true);
    }
    public void setErrorView(View view){
        this.errorView.addView(view);
    }
    /**
     * 通过状态显示加载页面
     */
    public void setShowView(ViewType viewType){
        hideAllView();
        switch (viewType){
            case CONTAINER_LAYOUT:
                containerLayout.setVisibility(View.VISIBLE);
                break;
            case LOADING_LAYOUT:
                loadingView.setVisibility(View.VISIBLE);
                break;
            case ERROR_LAYOUT:
                errorView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void hideAllView() {
        containerLayout.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
    }
    public  enum ViewType {
        CONTAINER_LAYOUT,
        LOADING_LAYOUT,
        ERROR_LAYOUT
    }

}
