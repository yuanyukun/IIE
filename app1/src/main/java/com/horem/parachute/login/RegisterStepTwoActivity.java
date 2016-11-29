package com.horem.parachute.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.horem.parachute.R;
import com.horem.parachute.common.BaseActivity;

public class RegisterStepTwoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step_two);
        init();
    }

    private void init() {
        initTitleView();
        setTitleName("注册");

        findViewById(R.id.return_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterStepTwoActivity.this,RegisterStepThreeActivity.class);
                startActivity(intent);
            }
        });
    }
}
