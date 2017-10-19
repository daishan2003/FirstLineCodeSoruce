package com.lp.coolweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.lp.coolweather.db.City;
import com.lp.coolweather.db.County;
import com.lp.coolweather.db.Province;
import com.lp.coolweather.gson.Weather;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by LP on 2017/10/18/11:08.
 */

public class Utility {
    private static final String TAG = Utility.class.getSimpleName();

    /**
     * 将返回的JSON数据解析成Weather实体类
     * @param responseStr
     * @return
     */
    public static Weather handleWeatherResponse(String responseStr) {
        Log.i(TAG, "handleWeatherResponse responseStr = " + responseStr);
        try {
            JSONObject jsonObject = new JSONObject(responseStr);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherText = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherText, Weather.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析和处理服务器返回的省级数据
     * @param responseStr
     * @return
     */
    public static boolean handleProvinceResponse(String responseStr){
        Log.i(TAG, "handleProvinceResponse responseStr = " + responseStr);
        if (!TextUtils.isEmpty(responseStr)){
            try {
                JSONArray allProvinces = new JSONArray(responseStr);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject pObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(pObject.getString("name"));
                    province.setProvinceCode(pObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     * @param responseStr
     * @param provinceId
     * @return
     */
    public static boolean handleCityResponse(String responseStr, int provinceId){
        Log.i(TAG, "handleCityResponse responseStr = " + responseStr + ";provinceId = " + provinceId);
        if (!TextUtils.isEmpty(responseStr)){
            try {
                JSONArray allCities = new JSONArray(responseStr);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cObject.getString("name"));
                    city.setCityCode(cObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     * @param responseStr
     * @param cityId
     * @return
     */
    public static boolean handleCountyResponse(String responseStr, int cityId){
        Log.i(TAG, "handleCountyResponse responseStr = " + responseStr + ";cityId = " + cityId);
        if (!TextUtils.isEmpty(responseStr)){
            try {
                JSONArray allCounties = new JSONArray(responseStr);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject pObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(pObject.getString("name"));
                    county.setWeatherId(pObject.getInt("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
