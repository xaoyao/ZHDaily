package org.eu.xaoyao.zhdaily.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.widget.TextView;

import org.eu.xaoyao.zhdaily.BuildConfig;
import org.eu.xaoyao.zhdaily.R;
import org.eu.xaoyao.zhdaily.adapter.NewsCommentsAdapter;
import org.eu.xaoyao.zhdaily.bean.CommentsListBean;
import org.eu.xaoyao.zhdaily.bean.NewsInfoBean;
import org.eu.xaoyao.zhdaily.bean.NewsListBean;
import org.eu.xaoyao.zhdaily.http.ZHApiManager;
import org.eu.xaoyao.zhdaily.utils.LogHelper;
import org.eu.xaoyao.zhdaily.utils.ToastUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentsFragment extends Fragment {

    @BindView(R.id.recycler_view_long_comments)
    RecyclerView mRvLongComments;

    @BindView(R.id.recycler_view_short_comments)
    RecyclerView mRvShortComments;

    @BindView(R.id.tv_long_comments)
    TextView mTvLongComments;

    @BindView(R.id.tv_short_comments)
    TextView mTvShortComments;

//    @BindView(R.id.empty_view)
//    NestedScrollView mEmptyView;


    private String mNewsId;
    private ZHApiManager mZHApiManager;
    private ArrayList<CommentsListBean.CommentBean> mComments;

    private ArrayList<CommentsListBean.CommentBean> mLongComments;
    private ArrayList<CommentsListBean.CommentBean> mShortComments;
    private NewsCommentsAdapter mLongCommentsAdapter;
    private NewsCommentsAdapter mShortCommentsAdapter;

    public CommentsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        ButterKnife.bind(this, view);

        initData();

        return view;
    }

    private void initData() {
        mNewsId = getArguments().getString("newsId");
        mZHApiManager = ZHApiManager.getInstance();

        initCommentsCount();
        initLongComments();
        initShortComments();


    }

    private void initCommentsCount() {
        mZHApiManager.getNewsInfo(mNewsId, new Subscriber<NewsInfoBean>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(NewsInfoBean newsInfoBean) {
                mTvLongComments.setText(newsInfoBean.long_comments + "条长评");
                mTvShortComments.setText(newsInfoBean.short_comments + "条短评");
            }
        });

    }

    private void initLongComments() {
        mZHApiManager.getLongComments(mNewsId, new Subscriber<CommentsListBean>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                ToastUtil.showToast(getContext(), "网络错误");
            }

            @Override
            public void onNext(CommentsListBean commentsListBean) {

                mLongComments = commentsListBean.comments;
                LogHelper.d("LongComments", mLongComments.size());
                mLongCommentsAdapter = new NewsCommentsAdapter(getContext(), mLongComments);

                mLongCommentsAdapter.setOnLoadingMoreListener(
                        new NewsCommentsAdapter.OnLoadingMoreListener() {
                            @Override
                            public void onLoadingMore() {
                                loadingMordLongComments();
                            }
                        });

                mRvLongComments.setAdapter(mLongCommentsAdapter);
                mRvLongComments.setLayoutManager(new LinearLayoutManager(getContext()));
                mRvLongComments.setHasFixedSize(true);
                mRvLongComments.setNestedScrollingEnabled(false);

            }
        });
    }

    private void initShortComments() {
        mZHApiManager.getShortComments(mNewsId, new Subscriber<CommentsListBean>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(CommentsListBean commentsListBean) {


                mShortComments = commentsListBean.comments;
//                LogHelper.d("ShortComments", mShortComments.size());
//                for (CommentsListBean.CommentBean bean : mShortComments) {
//                    LogHelper.d(LogHelper.makeLogTag("shortComment"), bean.content);
//                    LogHelper.d("img",bean.avatar);
//                }
                mShortCommentsAdapter = new NewsCommentsAdapter(getContext(), mShortComments);
                mShortCommentsAdapter.setOnLoadingMoreListener(
                        new NewsCommentsAdapter.OnLoadingMoreListener() {
                            @Override
                            public void onLoadingMore() {
                                loadingMoreShortComments();
                            }
                        });
                mRvShortComments.setAdapter(mShortCommentsAdapter);
                mRvShortComments.setLayoutManager(new LinearLayoutManager(getContext()));
                mRvShortComments.setHasFixedSize(true);
                mRvShortComments.setNestedScrollingEnabled(false);
            }
        });
    }


    private void loadingMordLongComments() {
        if (mLongComments.size() < 1) {
            return;
        }
        mZHApiManager.getMoreLongComments(mNewsId, mLongComments.get(mLongComments.size() - 1).id,
                new Subscriber<CommentsListBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mLongCommentsAdapter.setIsLoadingMore(false);
                    }

                    @Override
                    public void onNext(CommentsListBean commentsListBean) {
                        if (commentsListBean.comments.size() < 1) {
                            return;
                        }
                        int size = mLongComments.size();
                        mLongComments.addAll(commentsListBean.comments);
                        mLongCommentsAdapter.notifyItemRangeInserted(size, mLongComments.size() - 1);
                        mLongCommentsAdapter.setIsLoadingMore(false);
                    }
                });
    }

    private void loadingMoreShortComments() {
        if (mShortComments.size() < 1) {
            return;
        }

        mZHApiManager.getMoreShortComments(mNewsId, mShortComments.get(mShortComments.size() - 1).id,
                new Subscriber<CommentsListBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mShortCommentsAdapter.setIsLoadingMore(false);
                    }

                    @Override
                    public void onNext(CommentsListBean commentsListBean) {
                        if (commentsListBean.comments.size() < 1) {
                            return;
                        }
                        int size = mShortComments.size();
                        mShortComments.addAll(commentsListBean.comments);
                        mShortCommentsAdapter.notifyItemRangeInserted(size, mShortComments.size() - 1);
                        mShortCommentsAdapter.setIsLoadingMore(false);
                    }
                });

    }


}
