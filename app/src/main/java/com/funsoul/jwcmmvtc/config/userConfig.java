package com.funsoul.jwcmmvtc.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.funsoul.jwcmmvtc.jwcApploaction;
import com.funsoul.jwcmmvtc.utils.Md5;

import java.util.HashMap;
import java.util.Map;

public class userConfig {
    private static Context context = jwcApploaction.getContext();
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    //用户账号和密码
    public static void saveUserConfig(String username, String password) {
        sharedPreferences = context.getSharedPreferences("userConfig", 0);
        editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", Md5.JM(password));
        editor.commit();
    }

    public static Map getUserConfig() {
        Map map = new HashMap();
        sharedPreferences = context.getSharedPreferences("userConfig", 0);
        String use = sharedPreferences.getString("username", "");
        String pwd = sharedPreferences.getString("password", "");
        map.put("username", use);
        map.put("password", Md5.JM(pwd));
        return map;
    }

    public static String getUserPwd() {
        sharedPreferences = context.getSharedPreferences("userConfig", 0);
        String pwd = sharedPreferences.getString("password", "");
        return Md5.JM(pwd);
    }

    public static String getUsername() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userConfig", 0);
        String username = sharedPreferences.getString("username", "");
        return username;
    }

    //用户是否登入
    public static Boolean saveIslogin(Boolean isLogin) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userConfig", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLogin", isLogin);
        editor.commit();
        return true;
    }

    public synchronized static Boolean userIsLogin() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userConfig", 0);
        Boolean islog = sharedPreferences.getBoolean("isLogin", false);
        return islog;
    }

    //用户是否第一次登入
    public static Boolean isFristlogin() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userConfig", 0);
        Boolean islog = sharedPreferences.getBoolean("isFristlogin", true);
        return islog;
    }

    public static void saversFristlogn(Boolean isFristlogin) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userConfig", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isFristlogin", isFristlogin);
        editor.commit();
    }

    //用户cookie
    public static String getCookie() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userConfig", 0);
        String Cookie = sharedPreferences.getString("Cookie", "");
        return Cookie;
    }

    public static void setCookie(String Cookie) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userConfig", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Cookie", Cookie);
        editor.commit();
    }

    //获取退出错误
    public static void addError() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userConfig", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Eerror", "Cookie过期");
        editor.commit();
    }

    public static void delError() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userConfig", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Eerror", "用户点击退出");
        editor.commit();
    }

    public static String getError() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userConfig", 0);
        String error = sharedPreferences.getString("Eerror", "用户点击退出");
        return error;
    }

    //用户名字
    public static String getname() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userConfig", 0);
        String name = sharedPreferences.getString("name", "");
        return name;
    }

    public static void setname(String name) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userConfig", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.commit();
    }

    //url地址
    public static void setUrlbody(String i, String i1, String i2, String i3) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userConfig", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("grxxUrl", i);
        editor.putString("cjcxUrl", i1);
        editor.putString("mmxgUrl", i2);
        editor.putString("bjkbcxUrl", i3);
        editor.commit();
    }

    public static void delUrlbody() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userConfig", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("grxxUrl", "");
        editor.putString("cjcxUrl", "");
        editor.putString("mmxgUrl", "");
        editor.putString("bjkbcxUrl", "");
        editor.commit();
    }

    public static String getGrxxUrl() {
        sharedPreferences = context.getSharedPreferences("userConfig", 0);
        String grxxUrl = sharedPreferences.getString("grxxUrl", "");
        return grxxUrl;
    }

    public static String getCjcxUrl() {
        sharedPreferences = context.getSharedPreferences("userConfig", 0);
        String cjcxUrl = sharedPreferences.getString("cjcxUrl", "");
        return cjcxUrl;
    }

    public static String getMmxgUrl() {
        sharedPreferences = context.getSharedPreferences("userConfig", 0);
        String mmxgUrl = sharedPreferences.getString("mmxgUrl", "");
        return mmxgUrl;
    }

    public static String getBjkbcxUrl() {
        sharedPreferences = context.getSharedPreferences("userConfig", 0);
        String bjkbcxUrl = sharedPreferences.getString("bjkbcxUrl", "");
        return bjkbcxUrl;
    }

    public static String getHPicSrc() {
        sharedPreferences = context.getSharedPreferences("userConfig", 0);
        String HPicSrc = sharedPreferences.getString("photo", "");
        return HPicSrc;
    }

    public static void setHPicSrc(String HPicSrc) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userConfig", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("photo", HPicSrc);
        editor.commit();
    }

}
