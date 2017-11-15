package com.example.mojiehua93.newsclient;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private static String sUrl = "http://www.imooc.com/api/teacher?type=4&num=30";
    private ListView mListView;
    private TextView mEmptyView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = findViewById(R.id.list_news);
        mEmptyView = findViewById(R.id.empty_view);

        new NewsAsyncTask().execute(sUrl);
    }

    class NewsAsyncTask extends AsyncTask<String, Void, List<NewsBean>> {

        @Override
        protected List<NewsBean> doInBackground(final String... strings) {
            return getJsonData(strings[0]);
        }

        @Override
        protected void onPostExecute(List<NewsBean> list) {
            super.onPostExecute(list);
            NewsAdapter adapter = new NewsAdapter(getApplicationContext(), list, mListView);
            mListView.setAdapter(adapter);
        }
    }

    private List<NewsBean> getJsonData(final String url) {
        List<NewsBean> newsBeanList = new ArrayList<>();
        try {
            String jsonSting = readStream(new URL(sUrl).openStream());
            Log.d(TAG, "getJsonData: jsonSting = " + jsonSting);
            JSONObject jsonObject = new JSONObject(jsonSting);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            NewsBean newsBean = null;
            for (int i=0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                newsBean = new NewsBean();
                newsBean.newsIconUrl = jsonObject.getString("picSmall");
                newsBean.newsTitle = jsonObject.getString("name");
                newsBean.newsContent = jsonObject.getString("description");
                newsBeanList.add(newsBean);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsBeanList;
    }

    private String readStream(final InputStream inputStream) {
        StringBuilder result = new StringBuilder();
        InputStreamReader streamReader = null;
        BufferedReader bufferedReader = null;
        try {
            String line = null;
            streamReader = new InputStreamReader(inputStream, "utf-8");
            bufferedReader = new BufferedReader(streamReader);
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, "readStream: ", e);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }
}
