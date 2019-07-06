package com.funsoul.jwcmmvtc;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.funsoul.jwcmmvtc.Service.jwcCookieService;
import com.funsoul.jwcmmvtc.utils.jwcDao;

public class jwcApploaction extends Application {
    private static String TAG = "jwcApploaction";
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        startSeriver();
    }

    //开启cookie检测服务
    private void startSeriver() {
        Intent intent = new Intent(this, jwcCookieService.class);
        startService(intent);
    }

    //获取content
    public static Context getContext() {
        return context;
    }

}
