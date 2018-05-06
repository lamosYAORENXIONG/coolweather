package com.example.lamos.coolweather.persenter;

import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lamos.coolweather.view.IWeatherActivityView;
import com.example.lamos.coolweather.view.WeatherActivity;
import com.example.lamos.coolweather.view.WeatherFragment;
import com.example.lamos.gson.Weather;
import com.example.lamos.util.HttpUtil;
import com.example.lamos.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by lamos on 4/16/18.
 */

public class WeatherActivityPersenterCompl implements IWeatherActivityPersenter {
    private static final String TAG = "WeatherActivityPersente";
    private IWeatherActivityView iWeatherActivityView;


    public WeatherActivityPersenterCompl(IWeatherActivityView i){
        iWeatherActivityView = i;
    }

    @Override
    public void requestWeather(String weatherId) {
        final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        if (weatherId != null){
            editor.putString("weatherId",weatherId);
        }
        String weatherUrl =  "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=c9b0b4b4c953435b8cf477b3af6e7d62";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "获取天气失败", Toast.LENGTH_SHORT).show();
                        getActivity().getSwipeRefresh().setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            editor.putString("weather",responseText);
                            editor.apply();
                            iWeatherActivityView.loadfragment(weather);
                        }else {
                            Toast.makeText(getActivity(), "获取天气失败", Toast.LENGTH_SHORT).show();
                        }
                        getActivity().getSwipeRefresh().setRefreshing(false);
                    }
                });
            }
        });
    }



    @Override
    public void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getActivity()).load(bingPic).into(getActivity().getBingPicImg());
                    }
                });
            }


        });
    }

    @Override
    public void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();

        if (DocumentsContract.isDocumentUri(getActivity(), uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }

        displayImage(imagePath);
    }

    @Override
    public void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }

    @Override
    public String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getActivity().getContentResolver().query(uri,null,selection,null,null);

        if (cursor != null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }

        return path;
    }

    @Override
    public void displayImage(String imagePath) {
        if (imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            getActivity().getUserimage().setImageBitmap(bitmap);
        }else {
            Toast.makeText(getActivity(), "faild to get image", Toast.LENGTH_SHORT).show();
        }
    }

    private WeatherActivity getActivity(){
        return (WeatherActivity)iWeatherActivityView;
    }
}
