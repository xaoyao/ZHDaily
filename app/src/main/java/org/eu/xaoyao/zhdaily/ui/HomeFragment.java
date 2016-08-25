package org.eu.xaoyao.zhdaily.ui;


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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.eu.xaoyao.zhdaily.R;
import org.eu.xaoyao.zhdaily.adapter.HomeNewsAdapter;
import org.eu.xaoyao.zhdaily.bean.NewsListBean;
import org.eu.xaoyao.zhdaily.http.ZHApiManager;
import org.eu.xaoyao.zhdaily.utils.ToastUtil;

import java.util.ArrayList;

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

//    @BindView(R.id.rl_top_news)
//    RelativeLayout mRlTopNews;
//
//    @BindView(R.id.vp_top_news)
//    ViewPager mVpTonNews;
//
//    @BindView(R.id.ll_points)
//    LinearLayout mLlPoints;

    @BindView(R.id.rv_news)
    RecyclerView mRvNews;

    private ZHApiManager mZHApiManager;
    private ArrayList<NewsListBean.StoryBean> mNewsList=new ArrayList<>();
    private ArrayList<NewsListBean.TopStoryBean> mTopNewsList;
    private HomeNewsAdapter mHomeNewsAdapter;

    /**
     * 每次请求新闻的date，用来加载前一天的新闻
     */
    private String mDate;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this,view);
        mZHApiManager=ZHApiManager.getInstance();

        mHomeNewsAdapter = new HomeNewsAdapter(getContext(),mNewsList);
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
     * 从网络获取最新新闻
     */
    private void initLatestNews(){
        mZHApiManager.getLatestNews(new Subscriber<NewsListBean>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                mSwipeRefreshLayout.setRefreshing(false);
                ToastUtil.showToast(getActivity(),"网络错误");
            }

            @Override
            public void onNext(NewsListBean newsListBean) {
                mSwipeRefreshLayout.setRefreshing(false);
                if(newsListBean!=null){
                    mDate=newsListBean.date;
                    mNewsList=newsListBean.stories;
                    mTopNewsList=newsListBean.top_stories;

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
                ToastUtil.showToast(getActivity(),"网络错误");
            }

            @Override
            public void onNext(NewsListBean newsListBean) {
                if(newsListBean==null||newsListBean.stories.size()<1){
                    ToastUtil.showToast(getContext(),"没有更多了");
                    mHomeNewsAdapter.setIsLoadingBefore(false);
                }else {
                    mDate=newsListBean.date;
                    int size=mNewsList.size();
                    mNewsList.addAll(newsListBean.stories);
//                    mHomeNewsAdapter.notifyDataSetChanged();
                    mHomeNewsAdapter.notifyItemRangeInserted(size,mNewsList.size()-1);
                    mHomeNewsAdapter.setIsLoadingBefore(false);
                }
            }
        });

    }

}
