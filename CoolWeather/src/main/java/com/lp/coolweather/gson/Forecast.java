package com.lp.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LP on 2017/10/19/12:00.
 */

public class Forecast {

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

        @SerializedName("txt_id")
        public String info;
    }

}
