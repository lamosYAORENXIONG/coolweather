package com.example.lamos.coolweather.view;


import com.example.lamos.gson.Weather;

/**
 * Created by lamos on 4/16/18.
 */

public interface IWeatherActivityView {
    void login();
    void init();
    void loadfragment(Weather weather);


}
