package com.http.request;


/**
 * Created by user on 2016/4/7.
 */
public interface IResponseApi {



     void onSuccess(Object object);


     void onFailed(Exception e);
}
