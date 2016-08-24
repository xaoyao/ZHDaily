package org.eu.xaoyao.zhdaily;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.eu.xaoyao.zhdaily.bean.SplashImage;
import org.eu.xaoyao.zhdaily.http.ImageLoader;
import org.eu.xaoyao.zhdaily.http.ZHApiManager;
import org.eu.xaoyao.zhdaily.ui.HomeActivity;

import rx.Subscriber;

public class SplashActivity extends AppCompatActivity {

    private RelativeLayout mRlRoot;
    private ZHApiManager mZHApiManager;
    private ImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        transparent();

        mRlRoot = (RelativeLayout) findViewById(R.id.rl_root);
        mZHApiManager=ZHApiManager.getInstance();
        mImageLoader=ImageLoader.getInstance();

        initSplashImage();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(getApplicationContext(),"进入",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        },2000);

    }


    /**
     * 使4.4以上状态栏透明虚拟按键隐藏
     */
    private void transparent() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            );
        }
        //5.0状态栏全透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void initSplashImage(){
        WindowManager wm = getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        String imageSize = "720*1184";
        if (width >= 1080) {
            imageSize = "1080*1776";
        } else if (width >= 720) {
            imageSize = "720*1184";
        } else if (width >= 480) {
            imageSize = "480*728";
        } else if (width >= 320) {
            imageSize = "320*432";
        }
        mZHApiManager.getSplashImage(imageSize, new Subscriber<SplashImage>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(SplashImage splashImage) {
                mImageLoader.loadImage(splashImage.img, new Subscriber<Bitmap>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        mRlRoot.setBackgroundDrawable(new BitmapDrawable(getResources(),bitmap));
                    }
                });
            }
        });


    }
}
