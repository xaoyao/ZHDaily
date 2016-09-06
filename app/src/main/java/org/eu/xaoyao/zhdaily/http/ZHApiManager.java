package org.eu.xaoyao.zhdaily.http;

import android.text.TextUtils;

import org.eu.xaoyao.zhdaily.MyApplication;
import org.eu.xaoyao.zhdaily.bean.CommentsListBean;
import org.eu.xaoyao.zhdaily.bean.NewsDetailBean;
import org.eu.xaoyao.zhdaily.bean.NewsInfoBean;
import org.eu.xaoyao.zhdaily.bean.NewsListBean;
import org.eu.xaoyao.zhdaily.bean.NewsThemesBean;
import org.eu.xaoyao.zhdaily.bean.ThemeNewsListBean;
import org.eu.xaoyao.zhdaily.bean.SplashImage;
import org.eu.xaoyao.zhdaily.util.Utils;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by liu on 2016/8/20 0020.
 */
public class ZHApiManager {

    private ZHApi mZHApi;

    private ZHApiManager() {
        initApi();
    }

    private static class ZHApiManagerInstance {
        public static final ZHApiManager INSTANCE = new ZHApiManager();
    }

    /**
     * 获取ZHApiManager实例
     *
     * @return
     */
    public static ZHApiManager getInstance() {
        return ZHApiManagerInstance.INSTANCE;
    }

    private void initApi() {
        //okhttp本地缓存配置
        File cacheFile = Utils.getDiskCacheDir(MyApplication.getContext(), "okhttp");
        Cache cache = new Cache(cacheFile, 100 * 1024 * 1024);//最大缓存100M

        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                if (Utils.isNetworkReachable()) {
                    String cache = chain.request().header("cache");
                    Response response = chain.proceed(chain.request());
                    String cacheControl = response.header("Cache_Control");
                    if (cache == null) {
                        //如果api 没有指定缓存时间，默认缓存时间为5秒
                        if (TextUtils.isEmpty(cache)) {
                            cacheControl = 5 + "";
                        }
                        response = response.newBuilder()
                                .header("Cache_Control", "public, max-age=" + cache)
                                .build();
                    }
                    return response;

                } else {
                    //没有网络时，读取缓存数据,实现离线阅读
                    Request request = chain.request();
                    request = request.newBuilder()
                            .addHeader("Cache-Control",
                                    "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7)
                            .build();
                    return chain.proceed(request);
                }
            }
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(interceptor)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://news-at.zhihu.com/")
                .build();
        mZHApi = retrofit.create(ZHApi.class);
    }

    /**
     * 获取闪屏页图片对象
     *
     * @param imageSize
     * @param subscriber
     */
    public void getSplashImage(String imageSize, Subscriber<SplashImage> subscriber) {
        mZHApi.getSplashImage(imageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 获取网络图片
     *
     * @param url
     */
    public Observable<ResponseBody> getImage(String url) {
        return mZHApi.getImage(url);
    }


    /**
     * 获取新闻的主题日报列表
     *
     * @param subscriber
     */
    public void getNewsThemes(Subscriber<NewsThemesBean> subscriber) {
        mZHApi.getNewsThemes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 获取最新新闻列表
     */
    public void getLatestNews(Subscriber<NewsListBean> subscriber) {
        mZHApi.getLatestNews()
                .map(new Func1<NewsListBean, NewsListBean>() {
                    @Override
                    public NewsListBean call(NewsListBean newsListBean) {
                        for (NewsListBean.StoryBean bean : newsListBean.stories) {
                            bean.publishDate = newsListBean.date;
                        }
                        return newsListBean;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 获取过往新闻
     *
     * @param date       每次新闻返回的date可以用来请求前一天新闻
     * @param subscriber
     */
    public void getBeforeNews(String date, Subscriber<NewsListBean> subscriber) {
        mZHApi.getBeforeNews(date)
                .map(new Func1<NewsListBean, NewsListBean>() {
                    @Override
                    public NewsListBean call(NewsListBean newsListBean) {
                        for (NewsListBean.StoryBean bean : newsListBean.stories) {
                            bean.publishDate = newsListBean.date;
                        }
                        return newsListBean;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 获取主题日报的新闻列表
     *
     * @param id
     * @param subscriber
     */
    public void getThemeNewsList(String id, Subscriber<ThemeNewsListBean> subscriber) {
        mZHApi.getThemeNewsList(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 获取主题日报获取过往新闻
     *
     * @param themeId
     * @param newsId
     * @param subscriber
     */
    public void getBeforeThemeNews(String themeId, String newsId,
                                   Subscriber<ThemeNewsListBean> subscriber) {
        mZHApi.getBeforeThemeNews(themeId, newsId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }

    /**
     * 获取新闻详情
     *
     * @param id
     * @param subscriber
     */
    public void getNewsDetail(String id, Subscriber<NewsDetailBean> subscriber) {
        mZHApi.getNewsDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 获取新闻对应的长评论
     *
     * @param newsId
     * @param subscriber
     */
    public void getLongComments(String newsId, Subscriber<CommentsListBean> subscriber) {
        mZHApi.getLongComments(newsId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 获取新闻对应的短评论
     *
     * @param newsId
     * @param subscriber
     */
    public void getShortComments(String newsId, Subscriber<CommentsListBean> subscriber) {
        mZHApi.getShortComments(newsId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }


    /**
     * 获取更多长评
     *
     * @param newsId
     * @param commentId
     * @param subscriber
     */
    public void getMoreLongComments(String newsId, String commentId,
                                    Subscriber<CommentsListBean> subscriber) {
        mZHApi.getMoreLongComments(newsId, commentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }

    /**
     * 获取更多短评
     *
     * @param newsId
     * @param commentId
     * @param subscriber
     */
    public void getMoreShortComments(String newsId, String commentId,
                                     Subscriber<CommentsListBean> subscriber) {
        mZHApi.getMoreShortComments(newsId, commentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }

    /**
     * 获取新闻的额外信息
     * 评论数量，所获的『赞』的数量等
     * @param newsId
     * @param subscriber
     */
    public void getNewsInfo(String newsId, Subscriber<NewsInfoBean> subscriber) {
        mZHApi.getNewsInfo(newsId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }


}
