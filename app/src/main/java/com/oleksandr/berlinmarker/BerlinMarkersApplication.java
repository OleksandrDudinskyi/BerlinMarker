package com.oleksandr.berlinmarker;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

/**
 * @author Oleksandr Dudinskyi (dudinskyj@gmail.com)
 */
public class BerlinMarkersApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
}
