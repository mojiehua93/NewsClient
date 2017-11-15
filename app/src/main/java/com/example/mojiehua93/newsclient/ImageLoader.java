package com.example.mojiehua93.newsclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by MOJIEHUA93 on 2017/11/13.
 */

public class ImageLoader {
    private String mUrl;
    private Bitmap mBitmap;
    private LruCache<String, Bitmap> mLruCache;
    private Handler mHandler = new Handler();
    private ListView mListView;
    private Set<LoadImageAsyncTask> mTasksSet;

    public ImageLoader(ListView listView) {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 4;
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
        mListView = listView;
        mTasksSet = new HashSet<>();
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

    public void cancel() {
        if (mTasksSet != null) {
            for (LoadImageAsyncTask task : mTasksSet) {
                task.cancel(false);
            }
        }
    }

    private class LoadImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

//        private ImageView mImageView;
        private String mUrl;

        public LoadImageAsyncTask(String url) {
//            mImageView = imageView;
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
            ImageView imageView = mListView.findViewWithTag(mUrl);
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            mTasksSet.remove(this);
//            if (mImageView.getTag().equals(mUrl)) {
//                mImageView.setImageBitmap(bitmap);
//            }
        }
    }

    public void setImageUseAsyncTask(ImageView imageView, String url) {
        Bitmap bitmap = getBitmapFromCache(url);
        if (bitmap == null) {
//            new LoadImageAsyncTask(url).execute(url);
            imageView.setImageResource(R.mipmap.ic_launcher);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    public void loadImage(int start, int end) {
        for (int i=start; i < end; i++) {
            String url = NewsAdapter.sUrlArray[i];
            Bitmap bitmap = getBitmapFromCache(url);
            if (bitmap == null) {
                LoadImageAsyncTask task = new LoadImageAsyncTask(url);
                task.execute(url);
                mTasksSet.add(task);
            } else {
                ImageView imageView = mListView.findViewWithTag(url);
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
