package com.company.administrator.universalandroidappframework;

import android.os.Bundle;
import android.view.Window;

import com.company.administrator.universalandroidappframework.ui.activity.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setShowView(ViewType.LOADING_LAYOUT);
    }
}
