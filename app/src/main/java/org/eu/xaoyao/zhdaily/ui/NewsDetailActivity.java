package org.eu.xaoyao.zhdaily.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.eu.xaoyao.zhdaily.ActivityCollector;
import org.eu.xaoyao.zhdaily.R;
import org.eu.xaoyao.zhdaily.bean.NewsDetailBean;
import org.eu.xaoyao.zhdaily.http.ImageLoader;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;


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


    @BindView(R.id.news_title)
    TextView mNewsTitle;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private ArrayList<Fragment> mFragments;
    public String mNewsId;

    private ImageLoader mImageLoader;

    private boolean isDetail = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        ActivityCollector.addActivity(this);

        ButterKnife.bind(this);

        mToolbar.setTitle("");
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

        CommentsFragment commentsFragment = new CommentsFragment();
        commentsFragment.setArguments(bundle);
        mFragments.add(detailFragment);
        mFragments.add(commentsFragment);
        mViewPager.setAdapter(new ContentAdapter(getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                isDetail = position == 0 ? true : false;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

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
        mNewsTitle.setText(news.title);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_comment,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isDetail) {
                    finish();
                } else {
                    mViewPager.setCurrentItem(0);
                    isDetail = true;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (isDetail) {
            finish();
        } else {
            mViewPager.setCurrentItem(0);
            isDetail = true;
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
