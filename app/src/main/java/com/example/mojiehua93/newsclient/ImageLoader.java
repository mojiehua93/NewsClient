package com.example.mojiehua93.newsclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;
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
    private String mUrl;
    private Bitmap mBitmap;
    private LruCache<String, Bitmap> mLruCache;
    private Handler mHandler = new Handler();

    public ImageLoader() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 4;
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    public void setBitmapToCache(String url, Bitmap bitmap) {
        if (getBitmapFromCache(url) == null) {
            mLruCache.put(url, bitmap);
        }
    }

    public Bitmap getBitmapFromCache(String url) {
        return mLruCache.get(url);
    }
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
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    private class LoadImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView mImageView;
        private String mUrl;

        public LoadImageAsyncTask(ImageView imageView, String url) {
            mImageView = imageView;
            mUrl = url;
        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            Bitmap bitmap = getBitmap(url);
            if (bitmap != null) {
                setBitmapToCache(url, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (mImageView.getTag().equals(mUrl)) {
                mImageView.setImageBitmap(bitmap);
            }
        }
    }

    public void setImageUseAsyncTask(ImageView imageView, String url) {
        Bitmap bitmap = getBitmapFromCache(url);
        if (bitmap == null) {
            new LoadImageAsyncTask(imageView, url).execute(url);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }
}
