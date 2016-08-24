package org.eu.xaoyao.zhdaily.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import android.widget.ImageView;

import com.jakewharton.disklrucache.DiskLruCache;

import org.eu.xaoyao.zhdaily.MyApplication;
import org.eu.xaoyao.zhdaily.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by liu on 2016/8/20 0020.
 */
public class ImageLoader {
    private LruCache<String, Bitmap> mMemoryCache;
    private DiskLruCache mDiskLruCache;

    private ZHApiManager mZHApiManager;

    private ImageLoader() {

        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };

        //本地缓存路径
        File cacheDir = Utils.getDiskCacheDir(MyApplication.getContext(), "imageloader");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        try {
            mDiskLruCache = DiskLruCache.open(cacheDir, 1, 1, 100 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mZHApiManager = ZHApiManager.getInstance();

    }

    private static class ImageLoaderInstance {
        private static final ImageLoader INSTANCE = new ImageLoader();
    }

    /**
     * 获取ImageLoader实例
     *
     * @return
     */
    public static ImageLoader getInstance() {
        return ImageLoaderInstance.INSTANCE;
    }


    /**
     * 想lrucache中添加一张图片
     *
     * @param url
     * @param bitmap
     */
    private void addBitmapToMemoryCache(String url, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(url) == null) {
            mMemoryCache.put(url, bitmap);
        }

    }

    /**
     * 从lrucache中读取图片
     *
     * @param url
     * @return
     */
    private Bitmap getBitmapFromMemoryCache(String url) {
        return mMemoryCache.get(url);
    }

    /**
     * 使用MD5算法对传入的key进行加密并返回。
     */
    public String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }


    public void loadImage(final String url, final ImageView imageView){
        imageView.setTag(url);

        loadImage(url, new Subscriber<Bitmap>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Bitmap bitmap) {
                if(imageView.getTag().equals(url)){
                    imageView.setImageBitmap(bitmap);
                }
            }
        });

    }

    /**
     * 加载图片，有缓存
     * @param url
     * @param subscriber
     */
    public void loadImage(final String url, Subscriber<Bitmap> subscriber) {

        Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(final Subscriber<? super Bitmap> subscriber) {
                Bitmap bitmap = null;
                if (!subscriber.isUnsubscribed()) {
                    bitmap = getBitmapFromMemoryCache(url);
                    if (bitmap != null) {
                        subscriber.onNext(bitmap);
                    } else {
                        getBitmapFromDiskCache(url, new Subscriber<Bitmap>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                subscriber.onError(e);
                            }

                            @Override
                            public void onNext(Bitmap bitmap) {
                                if(bitmap!=null){
                                    subscriber.onNext(bitmap);
                                }
                            }
                        });

                    }
                }

            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }


    /**
     * 将缓存记录同步到journal文件中
     */
    public void fluchCache(){
        if(mDiskLruCache!=null){
            try {
                mDiskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从本地读取图片，如果不存在从网络获取
     * @param url
     * @param subscriber
     */
    private void getBitmapFromDiskCache(final String url,Subscriber<Bitmap> subscriber) {
        Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(final Subscriber<? super Bitmap> subscriber) {

                InputStream inputStream=null;

                final String key = hashKeyForDisk(url);
                try {
                    DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
                    if (snapshot == null) {
                        final DiskLruCache.Editor edit = mDiskLruCache.edit(key);
                        OutputStream out = edit.newOutputStream(0);

                        //本地不存在，从网络获取数据
                        downFromNet(url, out, new Subscriber<Boolean>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                subscriber.onError(e);
                            }

                            @Override
                            public void onNext(Boolean success) {
                                try {
                                    if (success) {
                                        edit.commit();
                                    } else {
                                        edit.abort();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    subscriber.onError(e);
                                }
                            }
                        });
                        snapshot=mDiskLruCache.get(key);
                    }
                    if(snapshot!=null){
                        inputStream = snapshot.getInputStream(0);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        subscriber.onNext(bitmap);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }finally {
                    if(inputStream!=null){
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(subscriber);

    }

    /**
     * 从网络下载数据
     */
    private void downFromNet(String url, final OutputStream out, Subscriber<Boolean> subscriber) {

        mZHApiManager.getImage(url)
                .map(new Func1<ResponseBody, Boolean>() {
                    @Override
                    public Boolean call(ResponseBody responseBody) {
                        InputStream inputStream = null;
                        try {
                            inputStream = responseBody.byteStream();
                            byte[] b = new byte[1024];
                            int len = 0;
                            while ((len = inputStream.read(b)) != -1) {
                                out.write(b, 0, len);
                            }
                            return true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (inputStream != null) {
                                    inputStream.close();
                                }
                                if (out != null) {
                                    out.close();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                        return false;
                    }
                }).subscribe(subscriber);
    }


}
