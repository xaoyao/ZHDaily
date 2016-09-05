package org.eu.xaoyao.zhdaily.http;

import android.graphics.Bitmap;

import org.eu.xaoyao.zhdaily.bean.CommentsListBean;
import org.eu.xaoyao.zhdaily.bean.NewsDetailBean;
import org.eu.xaoyao.zhdaily.bean.NewsInfoBean;
import org.eu.xaoyao.zhdaily.bean.NewsListBean;
import org.eu.xaoyao.zhdaily.bean.NewsThemesBean;
import org.eu.xaoyao.zhdaily.bean.SplashImage;
import org.eu.xaoyao.zhdaily.bean.ThemeNewsListBean;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by liu on 2016/8/20 0020.
 */
public interface ZHApi {

    @GET
    Observable<ResponseBody> getImage(@Url String url);

    /**
     * 获取欢迎页图片信息
     *
     * @param imageSize
     * @return
     */
    @GET("api/4/start-image/{imageSize}")
    Observable<SplashImage> getSplashImage(@Path("imageSize") String imageSize);

    /**
     * 获取最新新闻
     *
     * @return
     */
    @GET("api/4/news/latest")
    Observable<NewsListBean> getLatestNews();

    /**
     * 获取过往新闻
     *
     * @param date 每次新闻返回的date可以用来请求前一天新闻
     * @return
     */
    @GET("api/4/news/before/{date}")
    Observable<NewsListBean> getBeforeNews(@Path("date") String date);

    /**
     * 获取新闻详情
     *
     * @param id
     * @return
     */
    @GET("api/4/news/{id}")
    Observable<NewsDetailBean> getNewsDetail(@Path("id") String id);


    /**
     * 获取新闻的额外信息
     * 评论数量，所获的『赞』的数量等
     *
     * @param id
     * @return
     */
    @GET("api/4/story-extra/{id}")
    Observable<NewsInfoBean> getNewsInfo(@Path("id") String id);


    /**
     * 获取新闻的主题日报列表
     *
     * @return
     */
    @GET("api/4/themes")
    Observable<NewsThemesBean> getNewsThemes();

    /**
     * 获取主题日报的新闻列表
     *
     * @param id
     * @return
     */
    @GET("api/4/theme/{id}")
    Observable<ThemeNewsListBean> getThemeNewsList(@Path("id") String id);

    /**
     * 获取主题日报获取过往新闻
     *
     * @param themeId
     * @param newsId
     * @return
     */
    @GET("api/4/theme/{themeId}/before/{newsId}")
    Observable<ThemeNewsListBean> getBeforeThemeNews(@Path("themeId") String themeId,
                                                     @Path("newsId") String newsId);

    /**
     * 获取新闻对应的长评论
     *
     * @param newsId
     * @return
     */
    @GET("api/4/story/{newsId}/long-comments")
    Observable<CommentsListBean> getLongComments(@Path("newsId") String newsId);

    /**
     * 获取更多长评
     *
     * @param newsId
     * @param commentId
     * @return
     */
    @GET("api/4/story/{newsId}/long-comments/before/{commentId}")
    Observable<CommentsListBean> getMoreLongComments(@Path("newsId") String newsId
            , @Path("commentId") String commentId);

    /**
     * 获取新闻对应的短评论
     *
     * @return
     */
    @GET("api/4/story/{newsId}/short-comments")
    Observable<CommentsListBean> getShortComments(@Path("newsId") String newsId);

    /**
     * 获取更多短评
     *
     * @param newsId
     * @param commentId
     * @return
     */
    @GET("api/4/story/{newsId}/short-comments/before/{commentId}")
    Observable<CommentsListBean> getMoreShortComments(@Path("newsId") String newsId
            , @Path("commentId") String commentId);


}
