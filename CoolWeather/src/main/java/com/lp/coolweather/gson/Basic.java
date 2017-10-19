package com.lp.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LP on 2017/10/19/12:00.
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
