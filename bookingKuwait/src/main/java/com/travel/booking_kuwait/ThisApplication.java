package com.travel.booking_kuwait;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

public class ThisApplication extends Application {

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

}