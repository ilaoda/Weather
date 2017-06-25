package com.ilaoda.weather;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hbh on 2017/6/25.
 */

public class ChooseAreaFragment extends Fragment {

    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTY = 2;

    private TextView titleText;
    private Button backButton;
    private ListView listView;
    
    private List<String> dataList = new ArrayList<String>();
    private ArrayAdapter<String> listViewAdapter;

    /**
     * 功能：初始化View
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.choose_area, container, false);

        // 获取页面元素
        titleText = (TextView) view.findViewById(R.id.tv_title);
        backButton = (Button) view.findViewById(R.id.btn_back);
        listView = (ListView) view.findViewById(R.id.list_view);

        // 获取listView的适配器
        listViewAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(listViewAdapter);

        return view;
    }


    /**
     * function: Set backbutton and listView clickListener
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // backButtion click
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        // listView click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    /**
     * function: 从数据库查询所有的省，若不存在去服务器上查询
     */
    private void queryProvince() {

    }


    /**
     * function: 从数据库查询所有的市，若不存在去服务器上查询
     */
    private void queryCity() {

    }


    /**
     * function: 从数据库查询所有的县，若不存在去服务器上查询
     */
    private void queryCounty() {

    }

    /**
     * 如果上面三个查询省市县没有从数据库查到，则执行该函数
     * function: 从服务器上查询数据
     * @param address
     * @param type
     */
    private void queryFromServer(String address, String type) {

    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {

    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {

    }

}
