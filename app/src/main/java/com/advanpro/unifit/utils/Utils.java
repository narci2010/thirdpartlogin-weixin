package com.advanpro.unifit.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 时间: 2017/9/20 15:37
 * 作者: 曾繁盛
 * 邮箱: 43068145@qq.com
 * 功能:
 */

public class Utils {
    /**
     * 根据一个网络连接(String)获取bitmap图像
     */
    public static Bitmap getNetBitmap(String imageUri) {
        // 显示网络上的图片
        Bitmap bitmap;
        try {
            URL myFileUrl = new URL(imageUri);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }
}
