package org.eu.xaoyao.zhdaily.http;

import android.graphics.Bitmap;

import org.eu.xaoyao.zhdaily.bean.NewsListBean;
import org.eu.xaoyao.zhdaily.bean.NewsThemesBean;
import org.eu.xaoyao.zhdaily.bean.SplashImage;

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
     * @param imageSize
     * @return
     */
    @GET("api/4/start-image/{imageSize}")
    Observable<SplashImage> getSplashImage(@Path("imageSize") String imageSize);

    /**
     * 获取最新新闻
     * @return
     */
    @GET("api/4/news/latest")
    Observable<NewsListBean> getLatestNews();

    /**
     * 获取过往新闻
     * @param date
     * 每次新闻返回的date可以用来请求前一天新闻
     * @return
     */
    @GET("api/4/news/before/{date}")
    Observable<NewsListBean> getBeforeNews(@Path("date") String date);

    /**
     * 获取新闻详情
     * @param id
     * @return
     */
    @GET("api/4/news/{id}")
    Observable getNewsDetail(@Path("id") String id);


    /**
     * 获取新闻的额外信息
     * 评论数量，所获的『赞』的数量等
     * @param id
     * @return
     */
    @GET("api/4/story-extra/{id}")
    Observable getNewsInfo(@Path("id") String id);


    /**
     * 获取新闻的主题日报列表
     * @return
     */
    @GET("api/4/themes")
    Observable<NewsThemesBean> getNewsThemes();

    /**
     * 获取主题日报的新闻列表
     * @param id
     * @return
     */
    @GET("api/4/theme/{id}")
    Observable getThemeNewsList(@Path("id") String id);



}
