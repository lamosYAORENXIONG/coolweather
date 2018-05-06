package com.example.lamos.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lamos on 2018/3/18.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public  String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
