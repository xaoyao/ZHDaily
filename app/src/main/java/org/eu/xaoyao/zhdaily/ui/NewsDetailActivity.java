package org.eu.xaoyao.zhdaily.ui;

import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import org.eu.xaoyao.zhdaily.R;
import org.eu.xaoyao.zhdaily.bean.NewsDetailBean;
import org.eu.xaoyao.zhdaily.http.ImageLoader;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class NewsDetailActivity extends AppCompatActivity {


    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.app_bar)
    AppBarLayout mAppBar;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mcCollapsingToolbar;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.appbar_image)
    ImageView mAppbarImage;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private ArrayList<Fragment> mFragments;
    public String mNewsId;

    private ImageLoader mImageLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        initData();

    }

    private void initData() {

        mImageLoader = ImageLoader.getInstance();
        mNewsId = getIntent().getStringExtra("newsId");
        initViewPager();

    }

    private void initViewPager() {
        mFragments = new ArrayList<>();
        Bundle bundle = new Bundle();
        bundle.putString("newsId", mNewsId);
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(bundle);

        CommentFragment commentFragment = new CommentFragment();
        commentFragment.setArguments(bundle);
        mFragments.add(detailFragment);
        mFragments.add(commentFragment);
        mViewPager.setAdapter(new ContentAdapter(getSupportFragmentManager()));

    }


    /**
     * fragment加载完数据后调用此方法
     *
     * @param news
     */
    public void updateView(NewsDetailBean news) {
        if (news == null) {
            return;
        }

        if (news.image == null) {
            mAppBar.setExpanded(false);

        } else {
            mAppbarImage.setVisibility(View.VISIBLE);
            mImageLoader.loadImage(news.image, mAppbarImage);
        }

    }


    int startX;
    int startY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX= (int) event.getX();
                startY= (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int endX= (int) event.getX();
                int endY= (int) event.getY();
                int dx=endX-startX;
                int dy=endY-startY;
                Log.d("dx",dx+"");
                //右滑
                if(Math.abs(dx)>Math.abs(dy)){
                    if(dx>0){
                        finish();
                    }
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }



    public class ContentAdapter extends FragmentPagerAdapter {

        public ContentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }


}
