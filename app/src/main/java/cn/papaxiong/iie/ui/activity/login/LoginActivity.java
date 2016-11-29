package cn.papaxiong.iie.ui.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import cn.papaxiong.iie.R;
import cn.papaxiong.iie.ui.activity.MainActivity;
import cn.papaxiong.iie.ui.activity.login.biz.IUserLoginView;
import cn.papaxiong.iie.ui.activity.login.biz.UserLoginPresenter;

public class LoginActivity extends AppCompatActivity implements IUserLoginView {
    private UserLoginPresenter loginPresenter;
    private EditText mEd_username;
    private EditText mEd_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
        //
        init();
    }

    private void init() {
        loginPresenter = new UserLoginPresenter(this);
        mEd_password = (EditText) findViewById(R.id.edit_password);
        mEd_username = (EditText) findViewById(R.id.edit_username);
    }

    @Override
    public String getUserName() {
        return mEd_username.getText().toString();
    }

    @Override
    public String getPassWord() {
        return mEd_password.getText().toString();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void toMainAty() {

    }

    @Override
    public void showErrorFailed() {

    }

    public void onLogin(View sourceView){
        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * 检测输入的字符是否符合要求
     * @param username
     * @param password
     * @return
     */
    private void  checkIsOk(String username,String password){

    }
}
