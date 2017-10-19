package com.lp.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LP on 2017/10/19/12:00.
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {

        @SerializedName("txt")
        public String info;
    }
}
