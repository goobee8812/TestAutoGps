package com.example.administrator.testautogps;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePalApplication;

/**
 * Created by Administrator on 2017/6/3.
 */
public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        context = getApplicationContext();
        LitePalApplication.initialize(context);
    }
    public static Context getContext(){
        return context;
    }
}
