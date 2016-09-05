package org.eu.xaoyao.zhdaily.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.eu.xaoyao.zhdaily.R;
import org.eu.xaoyao.zhdaily.bean.ThemeNewsListBean;
import org.eu.xaoyao.zhdaily.http.ImageLoader;
import org.eu.xaoyao.zhdaily.ui.NewsDetailActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liu on 2016/8/27 0027.
 */
public class ThemeNewsAdapter extends RecyclerView.Adapter<ThemeNewsAdapter.ViewHolder> {
    private static int TYPE_NORMAL = 1;
    private static int TYPE_HEADER = 2;

    private Context mContext;
    private ImageLoader mImageLoader;
    private ArrayList<ThemeNewsListBean.ThemeNewsBean> mThemeNewsList;

    private View mHeaderView;

    private boolean mIsLoadingBefore = false;
    /**
     * 下拉加载更多监听
     */
    private OnLoadingBeforeListener mOnLoadingBeforeListener;

    public ThemeNewsAdapter(Context context, ArrayList<ThemeNewsListBean.ThemeNewsBean> themeNewsList) {
        mContext = context;
        mThemeNewsList = themeNewsList;
        mImageLoader = ImageLoader.getInstance();
    }

    public void setThemeNewsList(ArrayList<ThemeNewsListBean.ThemeNewsBean> themeNewsList) {
        mThemeNewsList = themeNewsList;
    }


    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
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

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null) {
            return TYPE_NORMAL;
        }

        return position == 0 ? TYPE_HEADER : TYPE_NORMAL;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) {
            return new ViewHolder(mHeaderView);
        } else if (viewType == TYPE_NORMAL) {
            return new ViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_theme_news, null, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //头布局不做处理
        if (getItemViewType(position) == TYPE_HEADER) {
            return;
        }

        //下拉加载更多新闻
        if (position > mThemeNewsList.size() - 3 && mIsLoadingBefore == false) {
            mIsLoadingBefore = true;
            if (mOnLoadingBeforeListener != null) {
                mOnLoadingBeforeListener.onLoadingBefore();
            }

        }


        ThemeNewsListBean.ThemeNewsBean entity = null;
        if (mHeaderView != null) {
            entity = mThemeNewsList.get(position - 1);

        } else {
            entity = mThemeNewsList.get(position);
        }

        if (entity == null) {
            return;
        }

        holder.newsTitle.setText(entity.title);
        //新闻没有图片处理
        if (entity.images != null && entity.images.size() > 0) {
            holder.newsImage.setVisibility(View.VISIBLE);
            mImageLoader.loadImage(entity.images.get(0), holder.newsImage);
        } else {
            holder.newsImage.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mThemeNewsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.news_title)
        TextView newsTitle;

        @BindView(R.id.news_image)
        ImageView newsImage;

        @BindView(R.id.card_view)
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            //头布局不做处理
            if (itemView == mHeaderView) {
                return;
            }

            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.card_view)
        public void click() {
            Intent intent = new Intent(mContext, NewsDetailActivity.class);
            int position;
            if(mHeaderView!=null){
                position=getAdapterPosition()-1;
            }else {
                position=getAdapterPosition();
            }
            String id = mThemeNewsList.get(position).id;
            intent.putExtra("newsId", id);
            mContext.startActivity(intent);
        }

    }

    public interface OnLoadingBeforeListener {
        void onLoadingBefore();
    }

}
