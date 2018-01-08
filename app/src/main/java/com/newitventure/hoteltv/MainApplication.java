package com.newitventure.hoteltv;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by NITV-Vinay on 1/5/2018.
 */

public class MainApplication extends Application {

    final String  TAG = getClass().getSimpleName();

    static Context context;


    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        super.onCreate();

        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
