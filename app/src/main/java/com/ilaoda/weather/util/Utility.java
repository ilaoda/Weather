package com.ilaoda.weather.util;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ilaoda.weather.db.City;
import com.ilaoda.weather.db.County;
import com.ilaoda.weather.db.Province;
import com.ilaoda.weather.gson.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hbh on 2017/6/25.
 * 描述：对服务器相应的json进行解析省市县的数据
 *  1. 用JSONArray 和 JSONObject 解析数据
 *  2. 将数据封装到实体对象中
 *  3. 实体.save()保存到数据库中
 */

public class Utility {

    private static final String TAG = "Utility";

    /**
     * 功能：解析处理省级数据
     * @param response
     * @return
     */
    public static boolean handleProvinceResponse(String response) {
        Log.d(TAG, "response: " + response);
        if(!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvince = new JSONArray(response);

                // 遍历json
                for(int i=0; i<allProvince.length(); i++) {
                    JSONObject provinceObject = allProvince.getJSONObject(i);

                    Province province = new Province();
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.setProvinceName(provinceObject.getString("name"));

                    // Saves the model to db
                    province.save();
                }
                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // 如果为response为null,即没处理,false
        return false;
    }

    /**
     * 功能：通过province id, 获取每个市
     * @param response
     * @param provinceId
     * @return
     */
    public static boolean handleCityResponse(String response, int provinceId) {
        Log.d(TAG, "response: " + response);
        if(!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCity = new JSONArray(response);

                // 遍历json
                for(int i=0; i<allCity.length(); i++) {
                    JSONObject cityObject = allCity.getJSONObject(i);

                    City city = new City();
                    city.setCityCode(cityObject.getInt("id"));
                    city.setCityName(cityObject.getString("name"));
                    city.setProvinceId(provinceId); // ?

                    // Saves the model to db
                    city.save();
                }
                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // 如果为response为null,即没处理,false
        return false;
    }

    /**
     * 功能：处理县级数据
     * @param response
     * @param cityId
     * @return
     */
    public static boolean handleCountyResponse(String response, int cityId) {
        Log.d(TAG, "response: " + response);
        if(!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounty = new JSONArray(response);

                // 遍历json
                for(int i=0; i<allCounty.length(); i++) {
                    JSONObject countyObject = allCounty.getJSONObject(i);

                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId); // ?

                    // Saves the model to db
                    county.save();
                }
                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // 如果为response为null,即没处理,false
        return false;
    }


    public static Weather handleWeatherResponse(String response) {

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();

            // 将weatherContent json 转换为 Weather 中实体类的对象
            return new Gson().fromJson(weatherContent, Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
