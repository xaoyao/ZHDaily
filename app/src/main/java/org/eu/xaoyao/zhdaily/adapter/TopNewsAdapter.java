package org.eu.xaoyao.zhdaily.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.eu.xaoyao.zhdaily.R;
import org.eu.xaoyao.zhdaily.bean.NewsListBean;
import org.eu.xaoyao.zhdaily.http.ImageLoader;

import java.util.ArrayList;

/**
 * Created by liu on 2016/8/26 0026.
 */
public class TopNewsAdapter extends PagerAdapter {
    private Context mContext;

    private ImageLoader mImageLoader;
    private ArrayList<NewsListBean.TopStoryBean> mTopNewsList;
    private ArrayList<ImageView> mImages;

    public TopNewsAdapter(Context context, ArrayList<NewsListBean.TopStoryBean> topNewsList) {
        mContext = context;
        mTopNewsList = topNewsList;
        mImageLoader = ImageLoader.getInstance();
        initImages();
    }


    public void setTopNewsList(ArrayList<NewsListBean.TopStoryBean> topNewsList) {
        mTopNewsList = topNewsList;
        initImages();
    }

    private void initImages() {
        mImages = new ArrayList<>();
        for (int i = 0; i < mTopNewsList.size(); i++) {
            ImageView view = new ImageView(mContext);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            view.setLayoutParams(params);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setColorFilter(Color.parseColor("#757575"), PorterDuff.Mode.MULTIPLY);
            mImages.add(view);
        }
    }

    @Override
    public int getCount() {
//        return mTopNewsList.size();
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int p=position%mTopNewsList.size();
        ImageView view = mImages.get(p);
//        Log.d("top",mTopNewsList.get(position).title);
        mImageLoader.loadImage(mTopNewsList.get(p).image, view);
        container.addView(view);
        return view;
    }
}
