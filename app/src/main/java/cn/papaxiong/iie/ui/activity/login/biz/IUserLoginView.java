package cn.papaxiong.iie.ui.activity.login.biz;

/**
 * Created by Administrator on 2016/11/28.
 */

public interface IUserLoginView {
    String getUserName();
    String getPassWord();
    void  showLoading();
    void  hideLoading();
    void  toMainAty();
    void showErrorFailed();
}
