package com.advanpro.unifit;

import android.app.Application;

/**
 * 时间: 2017/10/13 15:42
 * 作者: 曾繁盛
 * 邮箱: 43068145@qq.com
 * 功能:
 */

public class MyApp extends Application {
    private static MyApp inst;

    @Override
    public void onCreate() {
        super.onCreate();
        inst = this;
    }

    public static MyApp getInst() {
        return inst;
    }
}
