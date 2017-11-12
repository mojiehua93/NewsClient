package com.example.mojiehua93.newsclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by MOJIEHUA93 on 2017/11/13.
 */

public class ImageLoader {
    private Bitmap mBitmap;
    private Handler mHandler = new Handler();
    public void setImageUseThread(final ImageView imageView, final String url) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mBitmap = getBitmap(url);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (imageView.getTag().equals(url)) {
                            imageView.setImageBitmap(mBitmap);
                        }
                    }
                });
            }
        }.start();
    }

    private Bitmap getBitmap(String url) {
        Bitmap bitmap = null;
        BufferedInputStream inputStream = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            inputStream = new BufferedInputStream(connection.getInputStream());
            bitmap = BitmapFactory.decodeStream(inputStream);
            connection.disconnect();
//            Thread.sleep(1000);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
}
