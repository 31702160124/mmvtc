package com.funsoul.jwcmmvtc.fragment.other_fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.funsoul.jwcmmvtc.Interface.IshowPane;
import com.funsoul.jwcmmvtc.R;
import com.funsoul.jwcmmvtc.config.userConfig;
import com.funsoul.jwcmmvtc.config.xkBG;
import com.funsoul.jwcmmvtc.fragment.BaseFragment;

/**
 * Created by Administrator on 2019/7/1.
 */

public class changPwd_fragment extends BaseFragment implements View.OnFocusChangeListener {
    private EditText old_pwd, new_pwd1, new_pwd2;
    private String old_pwdstr, new_pwd1str, new_pwd2str;
    private Button chang_pwd;
    private RelativeLayout chang_pwd_tv;
    private IshowPane ishowPane;
    private Switch show_pwd;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ishowPane = (IshowPane) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.changpwd_fragment, null);
        init(view);
        return view;
    }

    private void init(View view) {
        chang_pwd_tv = (RelativeLayout) view.findViewById(R.id.chang_pwd_tv);
        old_pwd = (EditText) view.findViewById(R.id.old_pwd);
        new_pwd1 = (EditText) view.findViewById(R.id.new_pwd1);
        new_pwd1.setNextFocusDownId(R.id.new_pwd2);
        new_pwd2 = (EditText) view.findViewById(R.id.new_pwd2);
        chang_pwd = (Button) view.findViewById(R.id.chang_pwd);
        old_pwd.setOnFocusChangeListener(this);
        new_pwd1.setOnFocusChangeListener(this);
        new_pwd2.setOnFocusChangeListener(this);
        chang_pwd.setOnFocusChangeListener(this);
        if (!userConfig.getUserPwd().isEmpty()) {
            old_pwd.setText(userConfig.getUserPwd());
        }
        show_pwd = (Switch) view.findViewById(R.id.show_pwd);
        show_pwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
                if (b) {
                    setHide_pwd();
                } else {
                    setShow_pwd();
                }
            }
        });
        chang_pwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, final boolean hasFocus) {
                InputMethodManager manager = ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                if (hasFocus) {
                    if (manager != null) {
                        manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        changPed();
                    }
                }
            }
        });
    }

    private void changPed() {
        old_pwdstr = old_pwd.getText().toString().trim();
        new_pwd1str = new_pwd1.getText().toString().trim();
        new_pwd2str = new_pwd2.getText().toString().trim();
        // 判断参数是否为空
        if (old_pwdstr.equals("")) {
            showAlertDialog("请输入原密码", old_pwd);
            return;
        }
        if (new_pwd1str.equals("")) {
            showAlertDialog("请输入新密码", new_pwd1);
            return;
        }
        if (new_pwd2str.equals("")) {
            showAlertDialog("请输入重密码", new_pwd2);
            return;
        }
        if (new_pwd1str.equals(new_pwd2str)) {
            if (new_pwd1str.length() < 6 | new_pwd2str.length() < 6) {
                cleanEd();
                showAlertDialog("密码小于6位", new_pwd1);
                return;
            }
            if (new_pwd1str.equals("123456") | new_pwd1str.equals("000000") | new_pwd1str.equals(userConfig.getUserPwd())) {
                cleanEd();
                showAlertDialog("密码不能为“123456”或“000000”和原密码相同", new_pwd1);
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String status = jwcDao.changePwd(old_pwdstr, new_pwd1str);
                    if (status.contains("修改成功")) {
                        userConfig.saveUserConfig(userConfig.getUsername(), new_pwd1str);
                        ishowPane.loginOut();
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showAlertDialog(status);
                            }
                        });
                    }
                    Log.i("修改密码", "run: " + status);
                }
            }).start();
        } else {
            cleanEd();
            showAlertDialog("两次新密码不相等", new_pwd1);
            return;
        }
    }

    private void showAlertDialog(String string, final EditText... editText) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(string);     //设置对话框标题

        //        builder.setIcon(R.drawable.ic_launcher);      //设置对话框标题前的图标

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getContext(), "" + editText.length, Toast.LENGTH_SHORT).show();
                if (editText.length != 0) {
                    editText[0].requestFocus();
                }
            }
        });

        builder.setCancelable(false);   //设置按钮是否可以按返回键取消,false则不可以取消

        AlertDialog dialog = builder.create();  //创建对话框

        dialog.setCanceledOnTouchOutside(false);      //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏

        dialog.show();
    }

    //清空输入框
    private void cleanEd() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new_pwd1.setText("");
                new_pwd2.setText("");
            }
        });
    }

    //获取焦点后改变背景
    @Override
    public void onFocusChange(View view, final boolean b) {
        if (b) {
            chang_pwd_tv.setBackgroundDrawable(xkBG.getRandm(xkBG.loginarray));
        }
    }

    //显示隐藏密码
    private void setShow_pwd() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HideReturnsTransformationMethod show = HideReturnsTransformationMethod.getInstance();
                old_pwd.setTransformationMethod(show);
                new_pwd1.setTransformationMethod(show);
                new_pwd2.setTransformationMethod(show);
            }
        });
    }

    private void setHide_pwd() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PasswordTransformationMethod hide = PasswordTransformationMethod.getInstance();
                old_pwd.setTransformationMethod(hide);
                new_pwd1.setTransformationMethod(hide);
                new_pwd2.setTransformationMethod(hide);
            }
        });
    }

}
