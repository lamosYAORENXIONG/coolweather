package com.example.lamos.crash;

import android.app.Application;

/**
 * Created by lamos on 4/19/18.
 */

public class TestApp extends Application {
    private static TestApp sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }

    public static TestApp getInstance(){
        return sInstance;
    }
}
