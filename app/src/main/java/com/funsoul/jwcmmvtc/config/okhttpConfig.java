package com.funsoul.jwcmmvtc.config;

import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2019/7/5.
 */

public class okhttpConfig {
    private static okhttpConfig insten = new okhttpConfig();
    ;
    private static OkHttpClient client = new OkHttpClient();

    public static OkHttpClient getClient(okhttpConfig okhttpConfig) {
        return okhttpConfig.client;
    }

    private okhttpConfig() {

    }

    //获取okhttp3
    public static okhttpConfig getjwcDao() {
        if (insten == null) {
            synchronized (okhttpConfig.class) {
                if (insten == null) {
                    insten = new okhttpConfig();
                    client = new OkHttpClient();
                }
            }
        }
        return insten;
    }


}
