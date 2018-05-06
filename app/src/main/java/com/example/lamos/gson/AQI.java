package com.example.lamos.gson;

/**
 * Created by lamos on 2018/3/18.
 */

public class AQI {
    public AQICity city;

    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
