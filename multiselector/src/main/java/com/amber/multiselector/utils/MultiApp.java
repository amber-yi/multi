package com.amber.multiselector.utils;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

/**
 * Created by luosiyi on 2017/8/29.
 */

public class MultiApp extends Application {
    private static MultiApp multiApp;


    @Override
    public void onCreate() {
        super.onCreate();
        multiApp = this;
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
    }

    public static MultiApp getMultiApp() {
        return multiApp;
    }
}
