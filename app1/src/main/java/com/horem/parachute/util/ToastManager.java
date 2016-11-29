package com.horem.parachute.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by user on 2016/4/13.
 */
public class ToastManager {

    public static void show(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }
    public static void showLng(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
}
