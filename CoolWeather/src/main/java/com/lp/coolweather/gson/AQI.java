package com.lp.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LP on 2017/10/19/12:00.
 */

public class AQI {

    @SerializedName("city")
    public AQICity aqiCity;

    public class AQICity {

        public String aqi;

        public String pm25;
    }
}
