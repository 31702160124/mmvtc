package com.funsoul.jwcmmvtc.fragment.other_fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.funsoul.jwcmmvtc.Interface.Ibitmap;
import com.funsoul.jwcmmvtc.Interface.Istudentinfo;
import com.funsoul.jwcmmvtc.R;
import com.funsoul.jwcmmvtc.fragment.BaseFragment;

import java.util.Map;

public class studentinfo_fragment extends BaseFragment {
    private TextView tv_xh, tv_xm, tv_sex, tv_zzmm, tv_xi, tv_zymc, tv_xzb, tv_dqszj, tv_rxrq, tv_xlcc, tv_xxxs, tv_xz, tv_xjzt;
    private ImageView img;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.xueshengxingxi_fragment, null);
        init(view);
        show();
        jwcDao.getPersonalInfo(new Istudentinfo() {
            @Override
            public void setMap(final Map<String, String> map) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setXx(map);
                    }
                });
            }
        }, new Ibitmap() {
            @Override
            public void setBitmap(final Bitmap bitmap) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        img.setImageBitmap(bitmap);
                    }
                });
            }
        });
        setRetainInstance(true);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void init(View view) {
        tv_xh = (TextView) view.findViewById(R.id.tv_xh);
        tv_xm = (TextView) view.findViewById(R.id.tv_xm);
        tv_sex = (TextView) view.findViewById(R.id.tv_sex);
        tv_zzmm = (TextView) view.findViewById(R.id.tv_zzmm);
        tv_xi = (TextView) view.findViewById(R.id.tv_xi);
        tv_zymc = (TextView) view.findViewById(R.id.tv_zymc);
        tv_xzb = (TextView) view.findViewById(R.id.tv_xzb);
        tv_dqszj = (TextView) view.findViewById(R.id.tv_dqszj);
        tv_rxrq = (TextView) view.findViewById(R.id.tv_rxrq);
        tv_xlcc = (TextView) view.findViewById(R.id.tv_xlcc);
        tv_xxxs = (TextView) view.findViewById(R.id.tv_xxxs);
        tv_xz = (TextView) view.findViewById(R.id.tv_xz);
        tv_xjzt = (TextView) view.findViewById(R.id.tv_xjzt);
        img = (ImageView) view.findViewById(R.id.iv_pic);
    }

    private void setXx(Map<String, String> map) {
        tv_xh.setText(map.get("xh"));
        tv_xm.setText(map.get("xm"));
        tv_sex.setText(map.get("sex"));
        tv_zzmm.setText(map.get("zzmm"));
        tv_xi.setText(map.get("xi"));
        tv_zymc.setText(map.get("zymc"));
        tv_xzb.setText(map.get("xzb"));
        tv_dqszj.setText(map.get("dqszj"));
        tv_rxrq.setText(map.get("rxrq"));
        tv_xlcc.setText(map.get("xlcc"));
        tv_xxxs.setText(map.get("xxxs"));
        tv_xz.setText(map.get("xz"));
        tv_xjzt.setText(map.get("xjzt"));
        hide();
    }

}
