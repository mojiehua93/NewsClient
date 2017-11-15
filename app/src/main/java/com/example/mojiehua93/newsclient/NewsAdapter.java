package com.example.mojiehua93.newsclient;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

/**
 * Created by MOJIEHUA93 on 2017/11/12.
 */

public class NewsAdapter extends BaseAdapter implements AbsListView.OnScrollListener{
    public static final String TAG = "NewsAdapter";

    private LayoutInflater mInflater;
    private List<NewsBean> mList;
    private Context mContext;
    private ImageLoader mImageLoader;
    private int mLoadStart;
    private int mLoadEnd;
    public static String[] sUrlArray;
    private boolean mFirstLoad;

    public NewsAdapter(Context context, List<NewsBean> list, ListView listView) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(mContext);
        mImageLoader = new ImageLoader(listView);
        sUrlArray = new String[list.size()];
        for (int i=0; i < list.size(); i++) {
            sUrlArray[i] = list.get(i).newsIconUrl;
        }
        listView.setOnScrollListener(this);
        mFirstLoad = true;
    }
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.layout_item, null);
            viewHolder.icon = view.findViewById(R.id.image_item);
            viewHolder.title = view.findViewById(R.id.text_title);
            viewHolder.content = view.findViewById(R.id.text_content);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.icon.setImageResource(R.mipmap.ic_launcher);
//        setGlideImageView(i, viewHolder.icon);
        String url = mList.get(i).newsIconUrl;
        viewHolder.icon.setTag(url);
//        mImageLoader.setImageUseThread(viewHolder.icon, mList.get(i).newsIconUrl);
        mImageLoader.setImageUseAsyncTask(viewHolder.icon, url);
        viewHolder.title.setText(mList.get(i).newsTitle);
        viewHolder.content.setText(mList.get(i).newsContent);
        return view;
    }

    private void setGlideImageView(int position, ImageView imageView) {
        RequestOptions options = new RequestOptions()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher);
        GlideApp.with(mContext)
                .load(mList.get(position).newsIconUrl)
                .thumbnail(0.2F)
                .into(imageView);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if (i == SCROLL_STATE_IDLE) {
            mImageLoader.loadImage(mLoadStart, mLoadEnd);
        } else {
            mImageLoader.cancel();
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        mLoadStart = i;
        mLoadEnd = mLoadStart + i1;
        if (mFirstLoad && i1 > 0) {
            mImageLoader.loadImage(mLoadStart, mLoadEnd);
            mFirstLoad = false;
        }
    }

    class ViewHolder {
        public TextView title;
        public TextView content;
        public ImageView icon;
    }
}
