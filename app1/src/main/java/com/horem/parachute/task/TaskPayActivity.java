package com.horem.parachute.task;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.common.HttpUrlConstant;
import com.horem.parachute.R;
import com.horem.parachute.common.BaseActivity;
import com.horem.parachute.common.CustomApplication;
import com.horem.parachute.task.httpImpl.AliPayImpl;
import com.horem.parachute.task.httpImpl.TaskPayImpl;
import com.horem.parachute.task.httpImpl.WeinXinPayImpl;
import com.horem.parachute.util.SharePrefConstant;
import com.horem.parachute.util.SharePreferencesUtils;
import com.horem.parachute.util.ToastManager;
import com.http.request.HttpApi;
import com.http.request.IResponseApi;
import com.http.request.OkHttpClientManager;
import com.squareup.okhttp.Request;
import com.tencent.mm.sdk.modelpay.PayReq;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class TaskPayActivity extends BaseActivity implements View.OnClickListener,IResponseApi{
    private static final String url = HttpUrlConstant.taskWithdrawalWeChatUrl;
    private ImageView restMoneyCheck;   //选择零钱支付
    private ImageView weixinPayCheck;   //选择微信支付
    private ImageView alipayCheck;      //选择支付宝支付

    private static final int REST_MONEY_TYPE = 101;
    private static final int WEIXIN_PAY_TYPE = 102;
    private static final int ALIPAY_TYPE = 103;
    private static final int SDK_PAY_FLAG = 104;

    private int currentType = 101;

    private TextView tvRestMoney;   //零钱剩余
    private TextView tvTaskPay;     //应付款

    private String taskId;
    private boolean isSubTask;
    private boolean isFlowers;


    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(TaskPayActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(TaskPayActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(TaskPayActivity.this, "支付失败", Toast.LENGTH_SHORT).show();

                        }
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_pay);
        init();
        initialChangeDate();

    }



    private void init() {
        initTitleView();
        setTitleName("支付");

        Bundle bundle = getIntent().getBundleExtra("bundle");
        if(null != bundle) {
            taskId = bundle.getString("taskId");
            String intentType = bundle.getString("intentType");
            isSubTask = intentType.equals("MineTaskSend");
            isFlowers  = intentType.equals("sendFlowers");
        }

        tvRestMoney = (TextView) findViewById(R.id.tv_rest_money);
        tvTaskPay = (TextView) findViewById(R.id.tv_task_pay);

        restMoneyCheck = (ImageView) findViewById(R.id.method_rest_money_checked);
        weixinPayCheck = (ImageView) findViewById(R.id.method_weixin_pay_checked);
        alipayCheck = (ImageView) findViewById(R.id.method_ali_pay_checked);

        findViewById(R.id.ll_rest_money_pay).setOnClickListener(this);
        findViewById(R.id.ll_weixin_pay).setOnClickListener(this);//微信支付
        findViewById(R.id.button_pay_clicked).setOnClickListener(this);
        findViewById(R.id.ll_ali_pay).setOnClickListener(this);//支付宝支付
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        this.finish();
    }

    private void initialChangeDate() {
        String url =" ";
        HashMap<String,String> map = new HashMap<>();
        map.clear();
        map.put("taskId",taskId);

        map.put("memberId", SharePreferencesUtils.getLong(this, SharePrefConstant.MEMBER_ID,(long)0)+"");
        map.put("lng","");
        map.put("lat","");
        map.put("clientId", SharePreferencesUtils.getString(this, SharePrefConstant.INSTALL_CODE,""));
        map.put("deviceType","android");

        if(isSubTask){
            url = HttpUrlConstant.taskPayInfoSubTask;
        }else{
            if(!isFlowers){
                url = HttpUrlConstant.taskPayInfoMainTask;
            }else {
                map.remove("taskId");
                map.put("flowersId",taskId);
                url = HttpUrlConstant.flowersPayInfo;
            }

        }
        OkHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
//                Log.d(getClass().getName(),response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getInt("statusCode") == 1) {
                        String PayStr = jsonObject.getJSONObject("result").optString("feeStr");
                        String myChangeStr = jsonObject.getJSONObject("result").optString("myChangeStr");
                        tvTaskPay.setText(PayStr);
                        tvRestMoney.setText(myChangeStr);
                    }else if(jsonObject.getInt("statusCode") == -999){
                        exitApp();
                    }else
                        ToastManager.show(getApplicationContext(),"噢，网络不给力");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },map);

    }
    private void resetChecked(){
        restMoneyCheck.setImageDrawable(ContextCompat.getDrawable(this,R.mipmap.uncheck_48));
        weixinPayCheck.setImageDrawable(ContextCompat.getDrawable(this,R.mipmap.uncheck_48));
        alipayCheck.setImageDrawable(ContextCompat.getDrawable(this,R.mipmap.uncheck_48));
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_rest_money_pay:
                    resetChecked();
                    restMoneyCheck.setImageDrawable(ContextCompat.getDrawable(this,R.mipmap.check_48));
                    currentType = REST_MONEY_TYPE;
                break;
            case R.id.ll_weixin_pay:
                    resetChecked();
                    weixinPayCheck.setImageDrawable(ContextCompat.getDrawable(this,R.mipmap.check_48));
                    currentType = WEIXIN_PAY_TYPE;
                break;
            case R.id.ll_ali_pay:
                    resetChecked();
                    alipayCheck.setImageDrawable(ContextCompat.getDrawable(this,R.mipmap.check_48));
                    currentType = ALIPAY_TYPE;
                break;

            case R.id.button_pay_clicked:
                startLoading();
                switch (currentType){
                    case REST_MONEY_TYPE:

                        HashMap<String,String> map = new HashMap<>();
                        map.put("taskId",taskId);
                        if(isSubTask){
                            map.put("url",HttpUrlConstant.taskChangeToPaySubTaskUrl);
                        }else{
                            if(!isFlowers)
                                map.put("url",HttpUrlConstant.taskChangeToPayMainTaskUrl);
                            else
                                map.put("url",HttpUrlConstant.flowersRestMoneyToPay);
                        }
                        HttpApi api = new TaskPayImpl();
                        api.httpRequest(this,this,map);
                        break;
                    case WEIXIN_PAY_TYPE:

                        HashMap<String,String> map1 = new HashMap<>();
                        map1.put("taskId",taskId);
                        if(isSubTask){
                            map1.put("url",HttpUrlConstant.taskWeChatUnifiedOrderSubTaskUrl);
                        }else{
                            if(!isFlowers)
                                map1.put("url",HttpUrlConstant.taskWeChatUnifiedOrderMainTaskUrl);
                            else
                                map1.put("url",HttpUrlConstant.flowersWeChatPay);
                        }


                        HttpApi api1 = new WeinXinPayImpl();
                        api1.httpRequest(this,this,map1);

                        break;
                    case ALIPAY_TYPE:
                        HashMap<String,String> map2 = new HashMap<>();
                        map2.put("taskId",taskId);
                        if(isSubTask){
                            map2.put("url",HttpUrlConstant.taskAliUnifiedOrderSubTaskUrl);
                        }else{
                            if(!isFlowers)
                                map2.put("url",HttpUrlConstant.taskAliUnifiedOrderMainTaskUrl);
                            else
                                map2.put("url",HttpUrlConstant.flowersAliPay);
                        }

                        HttpApi api2 = new AliPayImpl();
                        api2.httpRequest(this,this,map2);
                        break;
                }
        }
    }

    @Override
    public void onSuccess(final Object object) {
        stopLoading();
        JSONObject jsonObject = null;

        switch (currentType){
            case REST_MONEY_TYPE:
                int status = 0;
                String message ="";
                try {
                    jsonObject = new JSONObject(object.toString());
                    status = jsonObject.getInt("statusCode");
                    message = jsonObject.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ToastManager.show(this,message);
                if(1==status){
                    TaskPayActivity.this.finish();
                }
                break;
            case WEIXIN_PAY_TYPE:

                try {
                    jsonObject = new JSONObject(object.toString());
                    PayReq req = new PayReq();
                    //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
                    req.appId			= jsonObject.getString("appid");
                    req.partnerId		= jsonObject.getString("partnerid");
                    req.prepayId		= jsonObject.getString("prepayid");
                    req.nonceStr		= jsonObject.getString("noncestr");
                    req.timeStamp		= jsonObject.getString("timestamp");
                    req.packageValue	= jsonObject.getString("package");
                    req.sign			= jsonObject.getString("sign");
                    req.extData			= "app data"; // optional
                    // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                    CustomApplication.api.sendReq(req);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case ALIPAY_TYPE:
            //异步调用
                Runnable payRunnable = new Runnable() {

                    @Override
                    public void run() {
                        // 构造PayTask 对象
                        PayTask alipay = new PayTask(TaskPayActivity.this);
                        // 调用支付接口，获取支付结果
                        String result = alipay.pay(object.toString(),true);
                        Log.i("支付宝回调信息：",result);
                        Message msg = new Message();
                        msg.what = SDK_PAY_FLAG;
                        msg.obj = result;
                        mHandler.sendMessage(msg);
                    }
                };
                // 必须异步调用
                Thread payThread = new Thread(payRunnable);
                payThread.start();
                break;
        }



    }

    @Override
    public void onFailed(Exception e) {

    }
}
