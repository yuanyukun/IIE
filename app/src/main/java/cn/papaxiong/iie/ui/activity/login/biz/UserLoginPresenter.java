package cn.papaxiong.iie.ui.activity.login.biz;

import android.os.Handler;

/**
 * Created by Administrator on 2016/11/28.
 */

public class UserLoginPresenter {
    private IUserBiz userBiz;
    private IUserLoginView userLoginView;
    private Handler mHandler = new Handler();

    public UserLoginPresenter(IUserLoginView userLoginView) {
        userBiz = new UserBiz();
        this.userLoginView = userLoginView;
    }

    public void login(){
        userLoginView.showLoading();
        userBiz.doLogin(userLoginView.getUserName(), userLoginView.getPassWord(), new OnLoginListener() {
            @Override
            public void loginSucess() {
                //需要在UI线程执行
                mHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        userLoginView.toMainAty();
                        userLoginView.hideLoading();
                    }
                });
            }

            @Override
            public void loginFailed() {
                //需要在UI线程执行
                mHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        userLoginView.showErrorFailed();
                        userLoginView.hideLoading();
                    }
                });
            }
        });

    }
}
