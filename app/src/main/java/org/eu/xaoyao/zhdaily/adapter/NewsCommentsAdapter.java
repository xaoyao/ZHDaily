package org.eu.xaoyao.zhdaily.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.eu.xaoyao.zhdaily.R;
import org.eu.xaoyao.zhdaily.bean.CommentsListBean;
import org.eu.xaoyao.zhdaily.http.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liu on 2016/9/3 0003.
 */
public class NewsCommentsAdapter extends RecyclerView.Adapter<NewsCommentsAdapter.ViewHolder> {
    private static final int TYPE_NORMAL = 1;
    private static final int TYPE_HEADER = 2;

    private Context mContext;
    private ImageLoader mImageLoader;
    private ArrayList<CommentsListBean.CommentBean> mComments;

    private View mHeaderView;
    private OnLoadingMoreListener mOnLoadingMoreListener;
    private boolean mIsLoadingMore = false;

    private SimpleDateFormat format;


    public NewsCommentsAdapter(Context context, ArrayList<CommentsListBean.CommentBean> comments) {
        mContext = context;
        mComments = comments;
        mImageLoader = ImageLoader.getInstance();
        format = new SimpleDateFormat("MM月dd日 HH:mm:ss");
    }

    public void setComments(ArrayList<CommentsListBean.CommentBean> comments) {
        mComments = comments;
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
    }

    public void setOnLoadingMoreListener(OnLoadingMoreListener onLoadingMoreListener) {
        mOnLoadingMoreListener = onLoadingMoreListener;
    }

    public void setIsLoadingMore(boolean isLoadingMore){
        mIsLoadingMore=isLoadingMore;
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
        }
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_news_comment, null, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (getItemViewType(position) == TYPE_HEADER) {
            return;
        }

        // 下拉加载更多
        if (position > mComments.size() - 3 && !mIsLoadingMore && mOnLoadingMoreListener != null) {
            mIsLoadingMore = true;
            mOnLoadingMoreListener.onLoadingMore();
        }

        CommentsListBean.CommentBean entity = null;
        if (mHeaderView != null) {
            entity = mComments.get(position - 1);
        } else {
            entity = mComments.get(position);
        }

        if (entity == null) {
            return;
        }

        if (!TextUtils.isEmpty(entity.avatar)) {
            mImageLoader.loadImage(entity.avatar, holder.avatar);
        }
        holder.author.setText(entity.author);
        holder.likes.setText(entity.likes+"");
        holder.content.setText(entity.content);
        holder.time.setText(format.format(entity.time*1000));
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.avatar)
        public ImageView avatar;

        @BindView(R.id.author)
        public TextView author;

        @BindView(R.id.likes)
        public TextView likes;

        @BindView(R.id.content)
        public TextView content;

        @BindView(R.id.time)
        public TextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            if (itemView == mHeaderView) {
                return;
            }
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnLoadingMoreListener {
        public void onLoadingMore();
    }
}
