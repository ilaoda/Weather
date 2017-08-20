package com.ilaoda.weather;


import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Toast;

import com.ilaoda.weather.db.City;
import com.ilaoda.weather.db.County;
import com.ilaoda.weather.db.Province;
import com.ilaoda.weather.util.HttpUtil;
import com.ilaoda.weather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by hbh on 2017/6/25.
 */

public class ChooseAreaFragment extends Fragment {

    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTY = 2;

    private int currentLevel;

    private TextView titleText;
    private Button backButton;
    private ListView listView;

    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;

    /**
     * 通过数据库查出来的都是List集合
     */
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    /**
     * 将查询出数据库的List遍历，然后存储在dataList中
     */
    private List<String> dataList = new ArrayList<String>();

    private ArrayAdapter<String> listViewAdapter;

    private ProgressDialog progressDialog;


    /**
     * 功能：初始化View
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

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
     *  当与Fragment关联的Activity被创建的时候执行
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /**
         * 默认查询省级列表,当刚进入软件的时候，就显示省级列表
         */
        queryProvince();


        /**
         * 如果点击的是某一省的item，那么肯定要显示该省的所有市，因此，去query市
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(currentLevel == LEVEL_PROVINCE) {
                    // 标记该省，再下面查询市的时候用
                    selectedProvince = provinceList.get(position);
                    queryCity();

                } else if (currentLevel == LEVEL_CITY) {
                    // 标记该市，再下面查询县的时候用
                    selectedCity = cityList.get(position);
                    queryCounty();
                } else if (currentLevel == LEVEL_COUNTY) {

                    /**
                     * 才正真在选择的县区上，点击后显示天气的信息
                     */
                    String weatherId = countyList.get(position).getWeatherId();
                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
                    intent.putExtra("weather_id", weatherId);

                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });


        /**
         * 当点击该级别页面的back后，就返回到上一级别的页面，因此，必须调query上一级的列表
         */
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(currentLevel == LEVEL_COUNTY) {
                    queryCity();
                } else if(currentLevel == LEVEL_CITY) {
                    queryProvince();
                }
            }
        });

    }


    /**
     * function: 从数据库查询所有的省，若不存在去服务器上查询
     */
    private void queryProvince() {

        titleText.setText("中国");
        backButton.setVisibility(View.GONE);  // 在显示省的界面，隐藏返回

        provinceList = DataSupport.findAll(Province.class);
        /** 将返回的list集合遍历，存储在dataList中 */
        if(provinceList.size() > 0) {
            dataList.clear(); // 清理掉原有的其他数据
            for(Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }

            // 修改已经生成的列表，不用重新刷新Activity，通知Activity更新ListView
            listViewAdapter.notifyDataSetChanged();
            // 表示将列表移动到指定的Position处。
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;

        } else { // 如果数据库没有，那就从网络上去请求
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }

    }


    /**
     * function: 从数据库查询所有的市，若不存在去服务器上查询
     */
    private void queryCity() {

        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);

        // cityList = DataSupport.findAll(City.class);
        // 根据选中的省，查询该省所有的市
        cityList = DataSupport.where("provinceid = ?",
                String.valueOf(selectedProvince.getId()))
                .find(City.class);


        /** 将返回的list集合遍历，存储在dataList中 */
        if(cityList.size() > 0) {
            dataList.clear(); // 清理掉原有的其他数据
            for(City city : cityList) {
                dataList.add(city.getCityName());
            }

            // 下面三行意思???
            listViewAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;

        } else { // 如果数据库没有，那就从网络上去请求
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china" + "/" + provinceCode;
            queryFromServer(address, "city");
        }
    }


    /**
     * function: 从数据库查询所有的县，若不存在去服务器上查询
     */
    private void queryCounty() {

        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);

        // cityList = DataSupport.findAll(City.class);
        // 根据选中的省，查询该省所有的市
        countyList = DataSupport.where("cityid = ?",
                String.valueOf(selectedCity.getId()))
                .find(County.class);


        /** 将返回的list集合遍历，存储在dataList中 */
        if(countyList.size() > 0) {
            dataList.clear(); // 清理掉原有的其他数据
            for(County county : countyList) {
                dataList.add(county.getCountyName());
            }

            // 下面三行意思???
            listViewAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;

        } else { // 如果数据库没有，那就从网络上去请求
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china" + "/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }

    /**
     * 如果上面三个查询省市县没有从数据库查到，则执行该函数
     * function: 从服务器上查询数据
     * @param address
     * @param type
     */
    private void queryFromServer(String address, final String type) {

        showProgressDialog();

        HttpUtil.sendOkHttpRequest(address, new Callback() {

            /**
             *
             * @param call
             * @param response
             * @throws IOException
             */
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();

                boolean result = false;
                if("province".equals(type)) {
                    // 存入数据库
                    result = Utility.handleProvinceResponse(responseText);
                } else if("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                } else if("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }

                if(result) {
                    /**
                     * 从子线程切换到UI主线程中
                     */
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)) {
                                queryProvince();
                            } else if("city".equals(type)) {
                                queryCity();
                            } else if("county".equals(type)) {
                                queryCounty();
                            }
                        }
                    });
                }
            }


            /**
             *
             * @param call
             * @param e
             */
            @Override
            public void onFailure(Call call, IOException e) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {

        if(progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");

            // 在对话框的外面点击,是否让对话框消失
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if(progressDialog != null) {
            progressDialog.dismiss();
        }

    }

}
