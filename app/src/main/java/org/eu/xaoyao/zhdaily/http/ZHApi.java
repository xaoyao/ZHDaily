package org.eu.xaoyao.zhdaily.http;

import android.graphics.Bitmap;

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

    @GET("api/4/start-image/{imageSize}")
    Observable<SplashImage> getSplashImage(@Path("imageSize") String imageSize);

}
