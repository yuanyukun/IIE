package cn.papaxiong.iie.common;

import android.content.Context;
import android.os.Looper;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import cn.papaxiong.iie.http.asyn.Async;
import cn.papaxiong.iie.utils.DimenUtils;

/**
 * Created by kenny on 2015/09/11.
 */
public class ToastManager {
    private static Toast sToast;
    private static TextView sTvContent;

    public static void show(final String message) {
        //nothing to show
        if (message == null || message.isEmpty()) {
            return;
        }

        getToast();
//        sToast.cancel();

        if (Looper.getMainLooper() == Looper.myLooper()) {
            sTvContent.setText(message);
            sToast.show();
        } else {
            Async.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sTvContent.setText(message);
                    sToast.show();
                }
            });
        }
    }

    public static void getToast() {
        if (sToast == null) {
            synchronized (ToastManager.class) {
                if (sToast == null) {
                    Context context = CustomApplication.getInstance();
                    sToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
//                    sTvContent = (TextView) LayoutInflater.from(context).inflate(R.layout.layout_toast, null);
//                    sToast.setView(sTvContent);
                    sToast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, DimenUtils.dp2px(context, 80));
                }
            }
        }
    }
}