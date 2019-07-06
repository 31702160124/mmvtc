package com.funsoul.jwcmmvtc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.funsoul.jwcmmvtc.Interface.IclosePane;
import com.funsoul.jwcmmvtc.Interface.Icookie;
import com.funsoul.jwcmmvtc.Interface.IshowPane;
import com.funsoul.jwcmmvtc.config.userConfig;
import com.funsoul.jwcmmvtc.config.xkBG;
import com.funsoul.jwcmmvtc.fragment.LeftFragment;
import com.funsoul.jwcmmvtc.fragment.RightFragment;
import com.funsoul.jwcmmvtc.utils.jwcDao;

import java.util.zip.Inflater;

public class MainActivity extends FragmentActivity implements IclosePane, IshowPane {
    public SlidingPaneLayout slp;
    private RightFragment rightFragment;
    private LeftFragment leftFragment;
    private myBroadcastReceiver myBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initTopbar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myBroadcastReceiver = new myBroadcastReceiver();
        IntentFilter inflater = new IntentFilter();
        inflater.addAction("funsoul.cookeover");
        registerReceiver(myBroadcastReceiver, inflater);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }

    //初始化
    private void init() {
        slp = (SlidingPaneLayout) findViewById(R.id.slp);
        rightFragment = (RightFragment) getSupportFragmentManager().findFragmentById(R.id.right_fragment);
        if (userConfig.isFristlogin()) {
            showANDelouse();
            userConfig.saversFristlogn(false);
        } else {
            Toast.makeText(this, userConfig.getname(), Toast.LENGTH_SHORT).show();
        }
    }

    //关闭侧边栏
    @Override
    public void rightStates(final String content, final int id) {
        jwcDao.checkCookie(new Icookie() {
            @Override
            public void cookie(Boolean b) {
                if (b) {
                    slp.closePane();
                    rightFragment.setTite(content);
                    rightFragment.addFragments(id);
                } else {
                    log_in();
                }
            }
        });
    }

    //打开侧边栏
    @Override
    public void showPane() {
        slp.openPane();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getSupportFragmentManager()
                        .findFragmentById(R.id.left_fragment)
                        .getView()
                        .findViewById(R.id.img_Left_tv).setBackgroundDrawable(xkBG.getRandm(xkBG.bgarray));
            }
        });
    }

    //退出
    @Override
    public void loginOut() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new jwcDao().jwcloginOut();
            }
        }).start();
        userConfig.delError();
        userConfig.saveIslogin(false);
        log_in();
    }

    private void log_in() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //用户第一次登入时的提示
    private void showANDelouse() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                slp.openPane();
            }
        }, 1000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                slp.closePane();
            }
        }, 2000);
    }

    //沉浸式状态栏
    private void initTopbar() {

        //4.4以上设置状态栏为透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        // 5.0以上系统状态栏透明，
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //            window.setStatusBarColor(Color.TRANSPARENT);//设置状态栏颜色和主布局背景颜色相同
            window.setStatusBarColor(Color.parseColor("#03A9F4"));//设置状态栏为指定颜色
        }
    }


    private class myBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, intent.getStringExtra("msg2"), Toast.LENGTH_SHORT).show();
            log_in();
        }
    }
}