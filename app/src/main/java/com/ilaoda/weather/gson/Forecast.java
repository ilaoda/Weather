package com.ilaoda.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hbh on 2017/8/12.
 */

public class Forecast {

    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    // 具体的天气
    @SerializedName("cond")
    public More more;

    public class Temperature {

        public String max;
        public String min;
    }

    public class More {

        @SerializedName("text_d")
        public String info;
    }
}
