package com.funsoul.jwcmmvtc.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.funsoul.jwcmmvtc.Interface.IshowPane;
import com.funsoul.jwcmmvtc.R;

public class RightFragment extends BaseFragment {
    private IshowPane ishowPane;
    private TextView tx;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ishowPane = (IshowPane) context;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.right_fragment, container, false);
        init(view);
        setRetainInstance(true);
        return view;
    }

    public void init(View view) {
        tx = (TextView) view.findViewById(R.id.title_tv);
        view.findViewById(R.id.right_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ishowPane.showPane();
            }
        });
        view.findViewById(R.id.login_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ishowPane.loginOut();
            }
        });
        addFragments(4);
    }

    //设置标题
    public void setTite(final String content) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tx.setText(content);
            }
        });
    }

    //试图选择器
    public void addFragments(int id) {
        switch (id) {
            case 0:
                whatFragments(f1);
                break;
            case 1:
                whatFragments(f2);
                break;
            case 2:
                whatFragments(f3);
                break;
            case 3:
                whatFragments(f4);
                break;
            case 4:
                whatFragments(f0);
                break;
        }
    }

    //碎片化选择器
    private void whatFragments(Fragment fragment) {
        if (fragment != null) {
            if (fragment.isAdded()) {
                getFragmentManager().beginTransaction().show(fragment).commit();
            } else {
                getFragmentManager().beginTransaction().replace(R.id.other_fragment, fragment).commit();
            }
        }
    }

}
