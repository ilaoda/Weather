package com.ilaoda.weather.gson;

import java.util.List;

/**
 * Created by hbh on 2017/8/12.
 *
 * gson 包下面的类，都要根据返回的json串来编写对应的实体类
 */

public class Weather {

    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public Suggestion suggestion;

    // json 串 daily_forecast中包含的是过个数组
    // 因此对于每一个数组所对应的Forecast类，都装在List中
    public List<Forecast> forecastList;

}
