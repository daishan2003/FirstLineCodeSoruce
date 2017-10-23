package com.lp.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LP on 2017/10/19/12:00.
 */

public class Forecast {

    @SerializedName("date")
    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    public class Temperature {

        public String max;

        public String min;
    }

    @SerializedName("cond")
    public More more;

    public class More {

        @SerializedName("txt_d")
        public String info;
    }

}
