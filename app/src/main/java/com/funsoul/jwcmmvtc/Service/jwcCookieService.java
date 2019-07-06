package com.funsoul.jwcmmvtc.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.funsoul.jwcmmvtc.Interface.IshowPane;
import com.funsoul.jwcmmvtc.config.okhttpConfig;
import com.funsoul.jwcmmvtc.config.urlConfig;
import com.funsoul.jwcmmvtc.config.userConfig;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.funsoul.jwcmmvtc.config.okhttpConfig.getClient;

public class jwcCookieService extends Service {
    private String TAG = "jwcCookieService";
    //用ThreadGroup类而不是Thread类，因为Service中可能有多个耗时的子线程
    private ThreadGroup myThreads = new ThreadGroup("ServiceWorker");
    private static OkHttpClient client = getClient(okhttpConfig.getjwcDao());
    private TimerTask timerTask;
    private Timer timer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
//        new Thread(myThreads,new logun_bg(),"login").start();
    }

    /*
     * 服务执行
     *
     * */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand is Run----------");
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (userConfig.userIsLogin()) {
                    checkCookie();
                } else {
                    addbg();
                }
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 0, 1800000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void addbg() {
        Intent intent = new Intent();
        intent.setAction("funsoul.addbg");
        intent.putExtra("msg1", "更换背景");
        sendBroadcast(intent);
    }

    private void checkCookie() {
        Request request = new Request.Builder()
                .url(urlConfig.cookieisoverUrl + userConfig.getUsername())
                .addHeader(urlConfig.jwccookieheader[0], userConfig.getCookie())
                .addHeader(urlConfig.jwccookieheader[2], urlConfig.jwccookieheader[3] + userConfig.getUsername())
                .build();
        String html = null;
        try {
            html = client.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String str1 = "Object moved"; //html.contains(str1)
        String str2 = "正方教务管理系统";//!html.contains(str2)
        if (html.contains(str1)) {
            //cookie 失效
            userConfig.addError();
            userConfig.setCookie("");
            userConfig.saveIslogin(false);
            Intent intent = new Intent();
            intent.setAction("funsoul.cookeover");
            intent.putExtra("msg2", "cookie失效");
            sendBroadcast(intent);
        } else {
            //cookie 可用
            userConfig.delError();
            userConfig.saveIslogin(true);
        }
    }

    class logun_bg implements Runnable {

        @Override
        public void run() {

        }
    }

}

