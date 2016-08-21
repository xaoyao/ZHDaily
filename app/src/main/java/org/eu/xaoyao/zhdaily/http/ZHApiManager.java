package org.eu.xaoyao.zhdaily.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import org.eu.xaoyao.zhdaily.MyApplication;
import org.eu.xaoyao.zhdaily.Utils.Utils;
import org.eu.xaoyao.zhdaily.bean.SplashImage;
import org.json.JSONException;
import org.json.JSONObject;

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
     * @param url
     */
    public Observable<ResponseBody> getImage(String url){
        return mZHApi.getImage(url);
    }


}
