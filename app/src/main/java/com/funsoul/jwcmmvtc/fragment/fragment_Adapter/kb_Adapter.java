package com.funsoul.jwcmmvtc.fragment.fragment_Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.funsoul.jwcmmvtc.R;
import com.funsoul.jwcmmvtc.entity.kebiao;

import java.util.ArrayList;
import java.util.List;

import static com.funsoul.jwcmmvtc.entity.kebiao.getkebiao;

public class kb_Adapter extends BaseAdapter {

    private Context mContext;
    private List<kebiao> mDataLists;
    private int mColumnNum = 0;

    public kb_Adapter() {
    }

    /**
     * 构造函数
     *
     * @param mContext
     * @param mDataLists
     */
    public kb_Adapter(Context mContext, List<kebiao> mDataLists, int columnNum) {
        this.mContext = mContext;
        this.mDataLists = mDataLists;
        this.mColumnNum = columnNum;
    }

    @Override
    public int getCount() {
        return mDataLists.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ResourceType")
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        view = LayoutInflater.from(mContext).inflate(R.layout.kb_row, parent, false);
        final LinearLayout row = (LinearLayout) view.findViewById(R.id.kb_row);  // 行布局
        row.setId(position);
        if (row != null) row.removeAllViews(); // 清空行
        for (int i = 0; i < mColumnNum; i++) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.kb_item, null, false);

            final TextView t_tv = (TextView) itemView.findViewById(R.id.t_tv);
            final TextView b_tv = (TextView) itemView.findViewById(R.id.b_tv);

            final int t_tvid = Integer.valueOf(row.getId() + "" + i);
            final int b_tvid = Integer.valueOf((row.getId() + 1) + "" + i);
            t_tv.setId(t_tvid);
            b_tv.setId(b_tvid);
            t_tv.setText(getkebiao((ArrayList<kebiao>) mDataLists, position).get(i));
            if (position < 12) {
                b_tv.setText(getkebiao((ArrayList<kebiao>) mDataLists, (position + 1)).get(i));
            }

            t_tv.setMinHeight(0);
            b_tv.setMinHeight(0);
            t_tv.setMaxHeight(400);
            b_tv.setMaxHeight(400);

            t_tv.setEms(9);
            b_tv.setEms(9);
            if (i == 0) {
                t_tv.setWidth(110);
                b_tv.setWidth(110);
            } else {
                t_tv.setWidth(250);
                b_tv.setWidth(250);
            }

            final int id = Integer.valueOf(position + "" + 0);
            if (t_tv.getId() == id) {
                t_tv.setWidth(110);
                t_tv.setHeight(200);
                b_tv.setHeight(200);
            } else {
                if (b_tv.getText().equals("") & t_tv.getText().equals("")) {
                    t_tv.setHeight(200);
                    b_tv.setHeight(200);
                } else if (b_tv.getText().equals("")) {
                    t_tv.setHeight(400);
                    b_tv.setHeight(0);
                } else if (t_tv.getText().equals("")) {
                    t_tv.setHeight(0);
                    b_tv.setHeight(400);
                }
            }
            t_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("课表", t_tv.getText() + " t_tv: " + t_tv.getId());
                }
            });
            b_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("课表", b_tv.getText() + " b_tv: " + b_tv.getId());
                }
            });
            // 将每个元素添加到行布局中去
            if (row.getId() < 2 | row.getId() % 2 == 1)
                row.removeAllViews();
            else
                row.addView(itemView);
        }
        return view;
    }

}
