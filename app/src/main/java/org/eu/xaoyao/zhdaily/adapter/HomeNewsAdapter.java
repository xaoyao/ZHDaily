package org.eu.xaoyao.zhdaily.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.eu.xaoyao.zhdaily.R;
import org.eu.xaoyao.zhdaily.bean.NewsListBean;
import org.eu.xaoyao.zhdaily.http.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liu on 2016/8/25 0025.
 */
public class HomeNewsAdapter extends RecyclerView.Adapter<HomeNewsAdapter.ViewHolder> {
    public static final int TYPE_HAS_DATE = 1;
    public static final int TYPE_NO_DATE = 2;

    public static final int TYPE_HEADER = 3;

    /**
     * 头布局
     */
    public View mHeaderView;

    private ArrayList<NewsListBean.StoryBean> mNewsList;
    private Context mContext;

    public ImageLoader mImageLoader;

    private boolean mIsLoadingBefore = false;
    /**
     * 下拉加载更多监听
     */
    private OnLoadingBeforeListener mOnLoadingBeforeListener;

    public HomeNewsAdapter(Context context, ArrayList<NewsListBean.StoryBean> newsList) {
        mContext = context;
        mNewsList = newsList;
        mImageLoader = ImageLoader.getInstance();
    }

    public void setNewsList(ArrayList<NewsListBean.StoryBean> newsList) {
        mNewsList = newsList;
    }

    /**
     * 下拉加载更多完成后设置为false
     *
     * @param isLoadingBefore
     */
    public void setIsLoadingBefore(boolean isLoadingBefore) {
        mIsLoadingBefore = isLoadingBefore;
    }

    /**
     * 下拉加载更多新闻监听
     *
     * @param listener
     */
    public void setOnLoadingBeforeListener(OnLoadingBeforeListener listener) {
        mOnLoadingBeforeListener = listener;
    }

    /**
     * 设置头布局
     *
     * @param headerView
     */
    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
    }


    /**
     * 决定使用哪种布局
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {

        if (mHeaderView != null) {
            //显示头布局
            if (position == 0) {
                return TYPE_HEADER;
            } else if (position == 1) {
                //第一条数据显示日期
                return TYPE_HAS_DATE;
            } else {
                //如果和前一条日期一样，就不显示日期，不一样，显示日期
                return mNewsList.get(position).publishDate
                        .equals(mNewsList.get(position - 1).publishDate)
                        ? TYPE_NO_DATE : TYPE_HAS_DATE;
            }
        } else {
            //第一条数据显示日期
            if (position == 0) {
                return TYPE_HAS_DATE;
            } else {
                //如果和前一条日期一样，就不显示日期，不一样，显示日期
                return mNewsList.get(position).publishDate
                        .equals(mNewsList.get(position - 1).publishDate)
                        ? TYPE_NO_DATE : TYPE_HAS_DATE;
            }
        }


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) {
            return new ViewHolder(mHeaderView);
        } else if (viewType == TYPE_HAS_DATE) {
            return new HasDateViewHolder(
                    LayoutInflater.from(mContext).inflate(R.layout.item_home_news, null, false));
        } else {
            return new ViewHolder(
                    LayoutInflater.from(mContext).inflate(R.layout.item_home_news_no_date, null, false));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        //头布局不做处理
        if(getItemViewType(position)==TYPE_HEADER){
            return;
        }

        //下拉加载更多新闻
        if (position > mNewsList.size() - 3 && mIsLoadingBefore == false) {
            mIsLoadingBefore = true;
            if (mOnLoadingBeforeListener != null) {
                mOnLoadingBeforeListener.onLoadingBefore();
            }

        }

        NewsListBean.StoryBean entity = mNewsList.get(position);
        if (entity == null) {
            return;
        }

        if (holder instanceof HasDateViewHolder) {
            bindHasDateNews((HasDateViewHolder) holder, entity);
        } else {
            bindNoDateNews(holder, entity);
        }

    }

    /**
     * 显示日期的新闻数据绑定
     *
     * @param holder
     * @param entity
     */
    private void bindHasDateNews(HasDateViewHolder holder, NewsListBean.StoryBean entity) {
        String date = entity.publishDate;
        String currentDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        if (currentDate.equals(date)) {
            holder.newsDate.setText("今日热闻");
        } else if (currentDate.substring(0, 4).equals(date.substring(0, 4))) {
            holder.newsDate.setText(String.format("%s月%s日",
                    date.substring(4, 6), date.substring(6, 8)));
        } else {
            holder.newsDate.setText(String.format("%s年%s月%s日",
                    date.substring(0, 4), date.substring(4, 6), date.substring(6, 8)));
        }
        bindNoDateNews(holder, entity);
    }

    /**
     * 不显示日期的新闻数据绑定
     *
     * @param holder
     * @param entity
     */
    private void bindNoDateNews(ViewHolder holder, NewsListBean.StoryBean entity) {

        //新闻没有图片处理
        if (entity.images != null && entity.images.size() > 0) {
            holder.newsImage.setVisibility(View.VISIBLE);
            mImageLoader.loadImage(entity.images.get(0), holder.newsImage);
        } else {
            holder.newsImage.setVisibility(View.GONE);
        }
        holder.newsTitle.setText(entity.title);
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.news_image)
        ImageView newsImage;

        @BindView(R.id.news_title)
        TextView newsTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            //头布局不做处理
            if (itemView == mHeaderView) {
                return;
            }
            ButterKnife.bind(this, itemView);
        }
    }

    public class HasDateViewHolder extends ViewHolder {

        @BindView(R.id.news_date)
        TextView newsDate;

        public HasDateViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnLoadingBeforeListener {
        void onLoadingBefore();
    }
}
