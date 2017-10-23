package com.lp.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lp.coolweather.gson.Forecast;
import com.lp.coolweather.gson.Weather;
import com.lp.coolweather.service.AutoUpdateService;
import com.lp.coolweather.util.Constant;
import com.lp.coolweather.util.HttpUtil;
import com.lp.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by LP on 2017/10/17/17:25.
 */

public class WeatherActivity extends AppCompatActivity{
    private static final String TAG = WeatherActivity.class.getSimpleName();

    public DrawerLayout drawerLayout;

    public SwipeRefreshLayout swipeRefreshLayout;

    public ScrollView weatherLayout;

    public Button bn_navButton;
    public TextView tv_titleCity;
    public TextView tv_titleUpdateTime;
    public TextView tv_degreeText;
    public TextView tv_weatherInfoText;

    public LinearLayout lv_forecastLayout;
    public TextView tv_aqiText;
    public TextView tv_pm25Text;
    public TextView tv_comfortText;
    public TextView tv_carWashText;
    public TextView tv_sportText;
    public ImageView iv_bingPicImg;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        initView();
    }

    /**
     * 初始化各种控件
     */
    private void initView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        bn_navButton = (Button) findViewById(R.id.nav_button);
        tv_titleCity = (TextView) findViewById(R.id.title_city);
        tv_titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        tv_degreeText = (TextView) findViewById(R.id.degree_text);
        tv_weatherInfoText = (TextView) findViewById(R.id.weather_info_text);

        lv_forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        tv_aqiText = (TextView) findViewById(R.id.aqi_text);
        tv_pm25Text = (TextView) findViewById(R.id.pm25_text);
        tv_comfortText = (TextView) findViewById(R.id.comfort_text);
        tv_carWashText = (TextView) findViewById(R.id.car_wash_text);
        tv_sportText = (TextView) findViewById(R.id.sport_text);
        iv_bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);

        final String weatherId;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherStr = sp.getString("weather", null);
        if (weatherStr != null){
            // 有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherStr);
            weatherId = weather.basic.weatherId;
            Log.i(TAG, "initView 11 weatherId = " + weatherId);
            showWeatherInfo(weather);
        } else {
            // 无缓存时从服务器查询天气
            weatherId = getIntent().getStringExtra("weather_id");
            Log.i(TAG, "initView 22 weatherId = " + weatherId);
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });

        bn_navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        String bingPic = sp.getString("bing_pic", null);
        if (bingPic != null){
            Glide.with(this).load(bingPic).into(iv_bingPicImg);
        } else {
            loadBingPic();
        }

    }

    /*
     * 根据天气id请求城市天气信息。
     * @param weatherId
     */
    public void requestWeather(final String weatherId) {
        Log.i(TAG, "requestWeather weatherId = " + weatherId);
        String weatherUrl = Constant.WEATHER_URL1 + weatherId + Constant.WEATHER_URL2;
        HttpUtil.sentOkHttpRequest(weatherUrl, new Callback(){

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor =
                                    PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();

    }

    /*
     * 加载必应每日一图
     */
    private void loadBingPic() {
        String requestBingPicUrl = Constant.BING_PIC_URL;
        HttpUtil.sentOkHttpRequest(requestBingPicUrl, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor =
                        PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(iv_bingPicImg);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 处理并展示Weather实体类中的数据
     * @param weather
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        tv_titleCity.setText(cityName);
        tv_titleUpdateTime.setText(updateTime);
        tv_degreeText.setText(degree);
        tv_weatherInfoText.setText(weatherInfo);
        lv_forecastLayout.removeAllViews();
        Log.i(TAG, "showWeatherInfo forecastList.size() = " + weather.forecastList.size());
        for (Forecast forecast : weather.forecastList){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, lv_forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            //Log.i(TAG, "showWeatherInfo forecast = " + forecast);
            String date = forecast.date;
            String info = forecast.more.info;
            String max = forecast.temperature.max;
            String min = forecast.temperature.min;
            //Log.i(TAG, "showWeatherInfo date = " + date + ";info = " + info +";max = " + max + ";min = " + min);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            lv_forecastLayout.addView(view);
        }
        Log.i(TAG, "showWeatherInfo weather.aqi = " + weather.aqi);
        if (weather.aqi != null){
            Log.i(TAG, "showWeatherInfo tv_aqiText = " + tv_aqiText + ";tv_pm25Text = " + tv_pm25Text);
            Log.i(TAG, "showWeatherInfo aqi = " + weather.aqi.aqiCity.aqi + ";pm25 = " + weather.aqi.aqiCity.pm25);
            tv_aqiText.setText(weather.aqi.aqiCity.aqi);
            tv_pm25Text.setText(weather.aqi.aqiCity.pm25);
        }
        String comfort = "舒适度:" + weather.suggestion.comfort.info;
        String carWash = "洗车指数:" + weather.suggestion.carWash.info;
        String sport = "运动建议:" + weather.suggestion.sport.info;
        tv_comfortText.setText(comfort);
        tv_carWashText.setText(carWash);
        tv_sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }
}
