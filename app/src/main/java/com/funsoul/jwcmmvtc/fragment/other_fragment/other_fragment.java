package com.funsoul.jwcmmvtc.fragment.other_fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.funsoul.jwcmmvtc.R;
import com.funsoul.jwcmmvtc.config.xkBG;
import com.funsoul.jwcmmvtc.fragment.BaseFragment;

public class other_fragment extends BaseFragment {
    private ImageView img_tv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.other_fragment, null);
        img_tv = (ImageView) view.findViewById(R.id.img_tv);
        img_tv.setImageDrawable(xkBG.getRandm(xkBG.catarray));
        return view;
    }

}
