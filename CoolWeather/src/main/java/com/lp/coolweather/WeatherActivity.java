package com.lp.coolweather;

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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lp.coolweather.util.Utility;

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
            Weather weather = Utility.handleWeatherResponse();
            weatherId = weather.basic.weatherId;
            showWeatherInfo();

        } else {
            // 无缓存时从服务器查询天气
            weatherId = getIntent().getStringExtra("weather_id");
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

    private void requestWeather(final String weatherId) {

    }

    private void loadBingPic() {

    }

    private void showWeatherInfo() {
    }
}
