package com.example.lamos.coolweather.persenter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by lamos on 4/16/18.
 */

public interface IWeatherActivityPersenter {
    void requestWeather(String weatherId);
    void loadBingPic();
    void handleImageOnKitKat(Intent data);
    void handleImageBeforeKitKat(Intent data);
    String getImagePath(Uri uri, String selection);
    void displayImage(String imagePath);

}
