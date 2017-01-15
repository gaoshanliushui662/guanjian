package com.guanjian.mm;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by Administrator on 2017/1/14.
 */

public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }
}