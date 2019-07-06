package com.funsoul.jwcmmvtc.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.funsoul.jwcmmvtc.Interface.Ibitmap;
import com.funsoul.jwcmmvtc.Interface.IclosePane;
import com.funsoul.jwcmmvtc.Interface.Icookie;
import com.funsoul.jwcmmvtc.Interface.IshowPane;
import com.funsoul.jwcmmvtc.Interface.Istudentinfo;
import com.funsoul.jwcmmvtc.LoginActivity;
import com.funsoul.jwcmmvtc.MainActivity;
import com.funsoul.jwcmmvtc.R;
import com.funsoul.jwcmmvtc.config.userConfig;

import java.util.Map;

public class LeftFragment extends BaseFragment {
    private ListView lv;
    private IclosePane iclosePane;
    private ImageView img_tv;
    private TextView name;
    private LinearLayout img_left_tv;

    //和activity连接时获取content
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        iclosePane = (IclosePane) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.left_fragment, container, false);
        init(view);
        setRetainInstance(true);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    //初始化
    private void init(View view) {
        name = (TextView) view.findViewById(R.id.mane);
        name.setText(userConfig.getname());
        img_left_tv = (LinearLayout) view.findViewById(R.id.img_Left_tv);
        img_tv = (ImageView) view.findViewById(R.id.Student_image);
        jwcDao.getPersonalInfo(new Istudentinfo() {
            @Override
            public void setMap(final Map<String, String> map) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        }, new Ibitmap() {
            @Override
            public void setBitmap(final Bitmap bitmap) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        img_tv.setImageBitmap(bitmap);
                    }
                });
            }
        });
        lv = (ListView) view.findViewById(R.id.lv_left);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sInfo = parent.getItemAtPosition(position).toString();
                switch (position) {
                    case 0:
                        ListViewCase(sInfo, position);
                        break;
                    case 1:
                        ListViewCase(sInfo, position);
                        break;
                    case 2:
                        ListViewCase(sInfo, position);
                        break;
                    case 3:
                        ListViewCase(sInfo, position);
                        break;
                    default:
                        ListViewCase("教务管理系统", 4);
                        break;
                }
            }
        });

    }

    //回调给主activity,打开哪个碎片化视图
    private void ListViewCase(final String content, final int id) {
        iclosePane.rightStates(content, id);
    }
}
