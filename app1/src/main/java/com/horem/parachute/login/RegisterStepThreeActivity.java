package com.horem.parachute.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.horem.parachute.R;
import com.horem.parachute.common.BaseActivity;

public class RegisterStepThreeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step_three);
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
                login();
            }

            private void login() {
//                Intent intent = new Intent(RegisterStepThreeActivity.this,MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
            }
        });
    }
}
