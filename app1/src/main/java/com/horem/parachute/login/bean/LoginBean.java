package com.horem.parachute.login.bean;

import java.io.Serializable;

/**
 * Created by user on 2016/4/8.
 */
public class LoginBean implements Serializable{

    private int statusCode;
    private String message;
    private User result;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return result;
    }

    public void setUser(User user) {
        this.result = user;
    }

}
