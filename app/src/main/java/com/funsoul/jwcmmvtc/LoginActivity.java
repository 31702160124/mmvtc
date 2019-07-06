package com.funsoul.jwcmmvtc;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.funsoul.jwcmmvtc.config.userConfig;
import com.funsoul.jwcmmvtc.config.xkBG;
import com.funsoul.jwcmmvtc.utils.jwcDao;

import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = "jwcLogin";
    private EditText user, pwd, code;
    private Button login;
    private String codestr, usestr, pwdstr;
    private ImageView code_image;
    private TextView Tv_err;
    private ImageView login_tv;
    private Switch show_pwd;
    private InputMethodManager manager;
    private myBroadcastReceiver1 myBroadcastReceiver1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initTopbar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (userConfig.userIsLogin()) {
            goToMain();
        } else {
            init();
        }
    }

    //初始化控件
    private void init() {
        myBroadcastReceiver1 = new myBroadcastReceiver1();
        IntentFilter inflater = new IntentFilter();
        inflater.addAction("funsoul.addbg");
        registerReceiver(myBroadcastReceiver1, inflater);
        login_tv = (ImageView) findViewById(R.id.login_tv);
        manager = ((InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE));
        show_pwd = (Switch) findViewById(R.id.Login_show_pwd);
        Tv_err = (TextView) findViewById(R.id.Tv_err);
        if (!userConfig.getError().equals("用户点击退出")) {
            Tv_err.setText(userConfig.getError());
        }
        user = (EditText) findViewById(R.id.user);
        pwd = (EditText) findViewById(R.id.pwd);
        pwd.setNextFocusForwardId(R.id.code);
        code = (EditText) findViewById(R.id.code);
        code_image = (ImageView) findViewById(R.id.code_image);
        login = (Button) findViewById(R.id.login);
        initEdit();
        //设置控件的事件
        login.setOnClickListener(this);
        code_image.setOnClickListener(this);

        show_pwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
                if (b) {
                    setHide_pwd();
                } else {
                    setShow_pwd();
                }
                pwd.requestFocus();
                pwd.setSelection(pwd.getText().toString().length());
            }
        });
        login.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, final boolean hasFocus) {
                Log.i("onFocusChange", "onFocusChange: " + view.getId() + hasFocus);
                if (hasFocus) {
                    if (manager != null) {
                        manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        postLogin();
                    }
                }
            }
        });
        showCheckImg();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                postLogin();
                break;
            case R.id.code_image:
                showCheckImg();
                break;
        }
    }

    //显示验证码
    private void showCheckImg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Bitmap bitmap = jwcDao.getCheckCodeImg();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            code_image.setImageBitmap(bitmap);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //登入
    private void postLogin() {
        //获取字符
        usestr = user.getText().toString().trim();
        pwdstr = pwd.getText().toString().trim();
        codestr = code.getText().toString().trim();
        // 判断参数是否为空
        if (usestr.equals("")) {
            user.requestFocus();
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pwdstr.equals("")) {
            pwd.requestFocus();
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (codestr.equals("")) {
            code.requestFocus();
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("登录中");
        progressDialog.setMessage("Loading");
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] result = jwcDao.Login(usestr, pwdstr, codestr);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
                switch (result[0]) {
                    case "checkCode_error":
                    case "pwd_error":
                    case "user_error":
                    case "user_locked":
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                code.setText("");
                            }
                        });
                        showCheckImg();
                        showHintsMsg(result[1]);
                        break;
                    case "ok":
                        goToMain();
                        showHintsMsg(result[1]);
                        break;
                    default:
                        showCheckImg();
                        showHintsMsg("程序出错，登录失败");
                        break;
                }
            }
        }).start();
    }

    //登入成功提示
    private void showHintsMsg(String s) {
        Looper.prepare();
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    //初始化账号密码
    private void initEdit() {
        Map map = userConfig.getUserConfig();
        user.setText((String) map.get("username"));
        pwd.setText((String) map.get("password"));
        user.setSelection(user.getText().toString().length());
    }

    //跳转MainActivity
    private void goToMain() {
        if (myBroadcastReceiver1 != null) {
            unregisterReceiver(myBroadcastReceiver1);
        }
        Log.i(TAG, "goToMain: ");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //密码的显示和隐藏
    private void setShow_pwd() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HideReturnsTransformationMethod show = HideReturnsTransformationMethod.getInstance();
                pwd.setTransformationMethod(show);
            }
        });
    }

    private void setHide_pwd() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PasswordTransformationMethod hide = PasswordTransformationMethod.getInstance();
                pwd.setTransformationMethod(hide);
            }
        });
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
            window.setStatusBarColor(Color.TRANSPARENT);//设置状态栏颜色和主布局背景颜色相同
            //            window.setStatusBarColor(Color.parseColor("#45b97f"));//设置状态栏为指定颜色
        }
    }

    private class myBroadcastReceiver1 extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            login_tv.setBackgroundDrawable(xkBG.getRandm(xkBG.loginarray));
        }
    }

}


