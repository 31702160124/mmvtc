package com.funsoul.jwcmmvtc.fragment;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.funsoul.jwcmmvtc.config.userConfig;
import com.funsoul.jwcmmvtc.fragment.other_fragment.changPwd_fragment;
import com.funsoul.jwcmmvtc.fragment.other_fragment.other_fragment;
import com.funsoul.jwcmmvtc.fragment.other_fragment.studentchenji_fragment;
import com.funsoul.jwcmmvtc.fragment.other_fragment.studentinfo_fragment;
import com.funsoul.jwcmmvtc.fragment.other_fragment.xueshengkebiao_fragment;
import com.funsoul.jwcmmvtc.utils.jwcDao;

public abstract class BaseFragment extends Fragment {
    public jwcDao jwcDao;
    private ProgressDialog progressDialog;
    public studentinfo_fragment f1;
    public studentchenji_fragment f2;
    public xueshengkebiao_fragment f3;
    public changPwd_fragment f4;
    public other_fragment f0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("loading");
        f0 = new other_fragment();
        f1 = new studentinfo_fragment();
        f2 = new studentchenji_fragment();
        f3 = new xueshengkebiao_fragment();
        f4 = new changPwd_fragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void show() {
        progressDialog.show();
    }

    public void hide() {
        progressDialog.dismiss();
    }

}
