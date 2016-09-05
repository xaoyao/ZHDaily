package org.eu.xaoyao.zhdaily.ui;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.eu.xaoyao.zhdaily.R;
import org.eu.xaoyao.zhdaily.adapter.ThemeNewsAdapter;
import org.eu.xaoyao.zhdaily.bean.ThemeNewsListBean;
import org.eu.xaoyao.zhdaily.http.ImageLoader;
import org.eu.xaoyao.zhdaily.http.ZHApiManager;
import org.eu.xaoyao.zhdaily.utils.ToastUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThemeNewsFragment extends Fragment {

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.rv_theme_news)
    RecyclerView mRecyclerView;

    View mHeaderView;

    private ZHApiManager mZhApiManager;
    private ImageLoader mImageLoader;

    private ArrayList<ThemeNewsListBean.ThemeNewsBean> mThemeNewsList = new ArrayList<>();

    private ThemeNewsAdapter mThemeNewsAdapter;

    private int mThemeId;


    public ThemeNewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_theme_news, container, false);
        ButterKnife.bind(this, view);

        initData();

        return view;
    }

    private void initData() {
        mThemeId = getArguments().getInt("themeId");

        mZhApiManager = ZHApiManager.getInstance();
        mImageLoader = ImageLoader.getInstance();
        mHeaderView = View.inflate(getContext(), R.layout.header_theme_news, null);

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.setRefreshing(true);

        mThemeNewsAdapter = new ThemeNewsAdapter(getContext(), mThemeNewsList);
        mThemeNewsAdapter.setHeaderView(mHeaderView);
        mRecyclerView.setAdapter(mThemeNewsAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);

        initListener();
        initLatestThemeNews();

    }

    private void initListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initLatestThemeNews();
            }
        });
        mThemeNewsAdapter.setOnLoadingBeforeListener(new ThemeNewsAdapter.OnLoadingBeforeListener() {
            @Override
            public void onLoadingBefore() {
                loadingBeforeNews();
            }
        });
    }

    /**
     * 下拉时加载更多新闻
     */
    private void loadingBeforeNews() {
        mZhApiManager.getBeforeThemeNews(mThemeId + "", mThemeNewsList.get(mThemeNewsList.size() - 1).id,
                new Subscriber<ThemeNewsListBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mThemeNewsAdapter.setIsLoadingBefore(false);
                    }

                    @Override
                    public void onNext(ThemeNewsListBean themeNewsListBean) {
                        if(themeNewsListBean==null||themeNewsListBean.stories.size()<1){
                            ToastUtil.showToast(getContext(), "没有更多了");
                            mThemeNewsAdapter.setIsLoadingBefore(false);
                        }else {
                            int size=mThemeNewsList.size();
                            mThemeNewsList.addAll(themeNewsListBean.stories);
                            mThemeNewsAdapter.notifyItemRangeInserted(size,mThemeNewsList.size()-1);
                            mThemeNewsAdapter.setIsLoadingBefore(false);
                        }

                    }
                });

    }


    /**
     * 获取最新新闻，填充recyclerview
     */
    private void initLatestThemeNews() {
        mZhApiManager.getThemeNewsList(mThemeId + "", new Subscriber<ThemeNewsListBean>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                mSwipeRefreshLayout.setRefreshing(false);
                ToastUtil.showToast(getContext(), "网络错误");
            }

            @Override
            public void onNext(ThemeNewsListBean themeNewsListBean) {
                mSwipeRefreshLayout.setRefreshing(false);
                mThemeNewsList = themeNewsListBean.stories;

                updateHeaderView(themeNewsListBean);

                mThemeNewsAdapter.setThemeNewsList(themeNewsListBean.stories);
                mThemeNewsAdapter.notifyDataSetChanged();
            }
        });


    }

    /**
     * 更新头布局
     *
     * @param themeNewsListBean
     */
    private void updateHeaderView(ThemeNewsListBean themeNewsListBean) {
        ImageView themeImage = (ImageView) mHeaderView.findViewById(R.id.iv_theme_image);
        themeImage.setColorFilter(Color.parseColor("#757575"), PorterDuff.Mode.MULTIPLY);

        TextView themeDescription = (TextView) mHeaderView.findViewById(R.id.tv_description);
        mImageLoader.loadImage(themeNewsListBean.image, themeImage);
        themeDescription.setText(themeNewsListBean.description);
    }

}
