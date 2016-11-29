package com.horem.parachute.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.login.Activity_Login;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

public class
WXEntryActivity extends Activity implements IWXAPIEventHandler{

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        CustomApplication.api.handleIntent(getIntent(),this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        CustomApplication.api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.i(getClass().getName(),"req触发");
    }

    @Override
    public void onResp(BaseResp baseResp) {
        String result = "";

        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "微信成功返回";
                boolean isWXLogin = SharePreferencesUtils.getBoolean(this,SharePrefConstant.iswxLon,false);
                boolean isWXBinding = SharePreferencesUtils.getBoolean(this,SharePrefConstant.iswxBinding,false);

                if( isWXLogin|| isWXBinding ){
                    String code = ((SendAuth.Resp)baseResp).code;
                    SharePreferencesUtils.setString(this, SharePrefConstant.WX_CODE,code);
                    SharePreferencesUtils.setBoolean(this,SharePrefConstant.wx_callback_OK,true);
                }else{
                    Log.d("设置分享标志","需要分享");
                    SharePreferencesUtils.setBoolean(getApplicationContext(),SharePrefConstant.isNeedConfirmShare,true);
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "用户取消授权";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "用户拒绝";
                break;
            default:
                result = "未知错误";
                break;
        }
        Log.d(getClass().getName(),result);
//        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        finish();
    }
}
