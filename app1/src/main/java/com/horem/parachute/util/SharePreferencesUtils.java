package com.horem.parachute.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by yuanyukun on 2016/3/13.
 */
public class SharePreferencesUtils {

    private enum Type{
        STRING,INTERGER,LONG,BOOLEAN,FLOAT
    }

    private static Object get(Context context,Type type,String key,Object defValue){
        Object result = null;
        SharedPreferences preferences = context.getSharedPreferences(SharePrefConstant.LOGIN_CONFIG,Context.MODE_PRIVATE);
        switch (type){

            case STRING: result = preferences.getString(key, (String) defValue);
                break;
            case INTERGER:result = preferences.getInt(key, (Integer) defValue);
                break;
            case LONG:result = preferences.getLong(key, (Long) defValue);
                break;
            case BOOLEAN:result = preferences.getBoolean(key, (Boolean) defValue);
                break;
            case FLOAT:result = preferences.getFloat(key, (Float) defValue);
                break;
        }

        return result;
    }

    private static boolean set(Context context,Type type,String key,Object value){
        boolean result = false;
        SharedPreferences preferences = context.getSharedPreferences(SharePrefConstant.LOGIN_CONFIG,Context.MODE_PRIVATE);
        switch (type){
            case STRING: result = preferences.edit().putString(key, (String) value).commit();
                break;
            case INTERGER: result = preferences.edit().putInt(key, (Integer) value).commit();
                break;
            case LONG: result = preferences.edit().putLong(key, (Long) value).commit();
                break;
            case BOOLEAN: result = preferences.edit().putBoolean(key, (boolean) value).commit();
                break;
            case FLOAT: result = preferences.edit().putFloat(key, (Float) value).commit();
                break;
        }
        return result;
    }
    /**********************************************************************************************/

    public static long getLong(Context context,String key,Object value){
        return (long) get(context,Type.LONG,key,value);
    }

    public static Float getFloat(Context context,String key,Object value){
        return (Float) get(context,Type.FLOAT,key,value);
    }

    public static Integer getInt(Context context,String key,Object value){
        return (Integer) get(context,Type.INTERGER,key,value);
    }

    public static String getString(Context context,String key,Object value){
        return (String) get(context,Type.STRING,key,value);
    }

    public static Boolean getBoolean(Context context,String key,Object value){
        return (boolean) get(context,Type.BOOLEAN,key,value);
    } /**********************************************************************************************/

    public static Boolean setLong(Context context,String key,Object value){
        return  set(context,Type.LONG,key,value);
    }

    public static Boolean setFloat(Context context,String key,Object value){
        return  set(context,Type.FLOAT,key,value);
    }

    public static Boolean setInt(Context context,String key,Object value){
        return  set(context,Type.INTERGER,key,value);
    }

    public static Boolean setString(Context context,String key,Object value){
        return  set(context,Type.STRING,key,value);
    }

    public static Boolean setBoolean(Context context,String key,Object value){
        return set(context,Type.BOOLEAN,key,value);
    }
}
