package com.app.utils;

import com.common.ApplicationConstant;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 2016/4/8.
 */
public class SignatureTool {

    //获取签名
    public static String getSignStr(Object object){

        String signStr =  GetSign(object);

        String sign = signStr + "key=" + ApplicationConstant.signKey;

        String mSign = Md5Encrypt.MD5(sign);

        return mSign;
    }

    //算法
    public static <T> String GetSign(T t) {
        String s = "";
        if (t != null) {
            Field[] fields = t.getClass().getDeclaredFields();
            ArrayList<String> nameArrayList = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            for (Field field : fields) {
                Method method = null;
                Object value = null;
                String name = field.getName();
                String upperName = name.substring(0, 1).toUpperCase()
                        + name.substring(1);
                try {
                    method = t.getClass()
                            .getMethod("get" + upperName);
                    value = method.invoke(t);

                    map.put(name, value);
                    nameArrayList.add(name);
                   /* if (name.startsWith("s_")) {
                        map.put(name, value);
                        nameArrayList.add(name);
                    }*/
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (map != null && map.size() > 0) {
                Collections.sort(nameArrayList);
                nameArrayList= sortArrayList(nameArrayList);
                if (nameArrayList != null) {
                    for (int i = 0; i < nameArrayList.size(); i++) {
                        String name = nameArrayList.get(i);
                        String value = map.get(name) != null ? map.get(name).toString() : "";
                        s += name + "=" + value + "&";
                    }
                }

            }
            return s;
        }
        return null;
    }

    private static ArrayList<String> sortArrayList(ArrayList<String> nameArrayList) {
        int count = nameArrayList.size();
        ArrayList<String> nameArrayList_New = nameArrayList;
        for (int i = 0; i < count; i++) {
            String compareStr = nameArrayList.get(i);
            for (int j = i; j < count; j++) {
                String compare_next = nameArrayList.get(j);
                int isbig = compareStr.toLowerCase().compareTo(compare_next.toLowerCase());
                if (isbig > 0) {
                    String temp = nameArrayList_New.get(i);
                    nameArrayList_New.set(i, compare_next);
                    nameArrayList_New.set(j, compareStr);
                }
            }
        }
        return  nameArrayList_New;
    }
}
