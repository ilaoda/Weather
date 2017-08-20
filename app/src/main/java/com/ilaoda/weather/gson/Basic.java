package com.ilaoda.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hbh on 2017/8/12.
 * @SerializedName 注解作用：和jdon串中的字段来映射
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;


    public class Update {

        @SerializedName("loc")
        public String updateTime;
    }
}
