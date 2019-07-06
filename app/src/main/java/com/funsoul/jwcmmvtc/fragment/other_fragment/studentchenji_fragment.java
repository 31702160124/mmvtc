package com.funsoul.jwcmmvtc.fragment.other_fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.funsoul.jwcmmvtc.Interface.Ichengji;
import com.funsoul.jwcmmvtc.R;
import com.funsoul.jwcmmvtc.entity.user;
import com.funsoul.jwcmmvtc.fragment.BaseFragment;

import java.util.ArrayList;

public class studentchenji_fragment extends BaseFragment {
    private SmartTable<Integer> table;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.xueshengchengji_fragment, null);
        table = (SmartTable<Integer>) view.findViewById(R.id.table);
        show();
        jwcDao.getchengji(new Ichengji() {
            @Override
            public void chemgji(final ArrayList<user> arrayList) {
                initTable(arrayList);
                hide();
            }
        });
        setRetainInstance(true);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * 初始化表格
     */
    private void initTable(ArrayList<user> dataList) {
        Column<String> year = new Column<>("学年", "year");
        Column<String> term = new Column<>("学期", "term");
        Column<String> code = new Column<>("课程代码", "code");
        Column<String> name = new Column<>("课程名称", "name");
        Column<String> property = new Column<>("课程性质", "property");
        Column<String> belong = new Column<>("课程归属", "belong");
        Column<String> credit = new Column<>("学分", "credit");
        Column<String> grade_point = new Column<>("绩点", "grade_point");
        Column<String> score = new Column<>("成绩", "score");
        Column<String> minor_tag = new Column<>("辅修标记", "minor_tag");
        Column<String> retest_score = new Column<>("补考成绩", "retest_score");
        Column<String> resume_score = new Column<>("重修成绩", "resume_score");
        Column<String> college = new Column<>("开课学院", "college");
        Column<String> note = new Column<>("备注", "note");
        Column<String> rebuild_tag = new Column<>("重修标记", "rebuild_tag");

        // 固定列
        term.setFixed(true);
        credit.setFixed(true);
        score.setFixed(true);
        TableData tableData = new TableData<user>("", dataList
                , year, term, code, property, belong, credit, grade_point, score, minor_tag, retest_score,
                resume_score, college, note, rebuild_tag, name);
        table.getConfig()
                .setContentStyle(new FontStyle(24, Color.rgb(68, 136, 187)))
                .setColumnTitleStyle(new FontStyle(24, Color.BLACK));

        table.setTableData(tableData);
        table.requestFocus();
    }

}
