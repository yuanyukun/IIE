package cn.papaxiong.iie.ui.activity.login.biz;

/**
 * Created by Administrator on 2016/11/28.
 */

public interface IUserBiz {
    void doLogin(String username,String password,OnLoginListener listener);
    void goIntroduceAty();
    void goMainAty();
}
