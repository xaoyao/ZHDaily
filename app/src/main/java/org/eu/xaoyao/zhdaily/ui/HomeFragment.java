package org.eu.xaoyao.zhdaily.ui;


import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.util.TimeUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.eu.xaoyao.zhdaily.R;
import org.eu.xaoyao.zhdaily.adapter.HomeNewsAdapter;
import org.eu.xaoyao.zhdaily.adapter.TopNewsAdapter;
import org.eu.xaoyao.zhdaily.bean.NewsListBean;
import org.eu.xaoyao.zhdaily.http.ZHApiManager;
import org.eu.xaoyao.zhdaily.utils.DisplayUtil;
import org.eu.xaoyao.zhdaily.utils.ToastUtil;

import java.util.ArrayList;
import java.util.logging.ErrorManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.rv_news)
    RecyclerView mRvNews;

    View mHeaderView;

    ViewPager mVpTonNews;

    TextView mTopNewsTitle;

    LinearLayout mLlPoints;

    private ZHApiManager mZHApiManager;
    private ArrayList<NewsListBean.StoryBean> mNewsList = new ArrayList<>();
    private ArrayList<NewsListBean.TopStoryBean> mTopNewsList = new ArrayList<>();
    private HomeNewsAdapter mHomeNewsAdapter;

    /**
     * 每次请求新闻的date，用来加载前一天的新闻
     */
    private String mDate;
    private TopNewsAdapter mTopNewsAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        mZHApiManager = ZHApiManager.getInstance();

        initHeaderView();

        mTopNewsAdapter = new TopNewsAdapter(getContext(), mTopNewsList);
        mVpTonNews.setAdapter(mTopNewsAdapter);

        mHomeNewsAdapter = new HomeNewsAdapter(getContext(), mNewsList);

        mHomeNewsAdapter.setHeaderView(mHeaderView);
        mHomeNewsAdapter.setOnLoadingBeforeListener(new HomeNewsAdapter.OnLoadingBeforeListener() {
            @Override
            public void onLoadingBefore() {
                loadingBefore();
                mHomeNewsAdapter.setIsLoadingBefore(false);
            }
        });
        mRvNews.setAdapter(mHomeNewsAdapter);
        mRvNews.setHasFixedSize(true);
        mRvNews.setLayoutManager(new LinearLayoutManager(getContext()));

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                initLatestNews();
            }
        });

        mSwipeRefreshLayout.setRefreshing(true);
        initLatestNews();

        return view;
    }

    /**
     * 初始化头布局
     */
    private void initHeaderView() {

        mHeaderView = View.inflate(getContext(), R.layout.header_top_news, null);
        mVpTonNews = (ViewPager) mHeaderView.findViewById(R.id.vp_top_news);
        mLlPoints = (LinearLayout) mHeaderView.findViewById(R.id.ll_points);
        mTopNewsTitle = (TextView) mHeaderView.findViewById(R.id.top_news_title);

        mVpTonNews.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                int realPosition = position % mTopNewsList.size();
                mTopNewsTitle.setText(mTopNewsList.get(realPosition).title);
                updateCheckedPoint(realPosition);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    /**
     * 数据改变后更新TopNews的标题与小圆点的选中位置
     */
    private void updateHeaderView() {
        if (mTopNewsList.size() < 1) {
            return;
        }

        mLlPoints.removeAllViews();
        //初始化小圆点
        for (int i = 0; i < mTopNewsList.size(); i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(R.drawable.point_selector);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i != 0) {
                //从第二个原点开始，有6dp的左边距
                params.leftMargin = DisplayUtil.dip2px(getContext(), 6);
            }
            imageView.setLayoutParams(params);
            mLlPoints.addView(imageView);
        }

        int position = mVpTonNews.getCurrentItem();
        int realPosition = position % mTopNewsList.size();
        mTopNewsTitle.setText(mTopNewsList.get(realPosition).title);

        updateCheckedPoint(realPosition);
    }

    /**
     * 更新选中的小圆点
     *
     * @param realPosition
     */
    private void updateCheckedPoint(int realPosition) {
        for (int i = 0; i < mLlPoints.getChildCount(); i++) {
            mLlPoints.getChildAt(i).setEnabled(i == realPosition);
        }
    }


    /**
     * 从网络获取最新新闻
     */
    private void initLatestNews() {
        mZHApiManager.getLatestNews(new Subscriber<NewsListBean>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                mSwipeRefreshLayout.setRefreshing(false);
                ToastUtil.showToast(getActivity(), "网络错误");
            }

            @Override
            public void onNext(NewsListBean newsListBean) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (newsListBean != null) {
                    mDate = newsListBean.date;
                    mNewsList = newsListBean.stories;
                    mTopNewsList = newsListBean.top_stories;

                    mTopNewsAdapter.setTopNewsList(mTopNewsList);
                    mTopNewsAdapter.notifyDataSetChanged();
                    updateHeaderView();

                    mHomeNewsAdapter.setNewsList(mNewsList);
                    mHomeNewsAdapter.notifyDataSetChanged();

//                    for (NewsListBean.StoryBean bean:mNewsList){
//                        Log.d("news",bean.title);
//                        Log.d("publishDate",bean.publishDate);
//                    }
                }

            }
        });

    }

    /**
     * 加载过往新闻，下拉刷新时调用
     */
    private void loadingBefore() {
        mZHApiManager.getBeforeNews(mDate, new Subscriber<NewsListBean>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.showToast(getActivity(), "网络错误");
            }

            @Override
            public void onNext(NewsListBean newsListBean) {
                if (newsListBean == null || newsListBean.stories.size() < 1) {
                    ToastUtil.showToast(getContext(), "没有更多了");
                    mHomeNewsAdapter.setIsLoadingBefore(false);
                } else {
                    mDate = newsListBean.date;
                    int size = mNewsList.size();
                    mNewsList.addAll(newsListBean.stories);
//                    mHomeNewsAdapter.notifyDataSetChanged();
                    mHomeNewsAdapter.notifyItemRangeInserted(size, mNewsList.size() - 1);
                    mHomeNewsAdapter.setIsLoadingBefore(false);
                }
            }
        });

    }

}
