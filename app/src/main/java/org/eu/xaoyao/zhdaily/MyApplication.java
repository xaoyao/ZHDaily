package org.eu.xaoyao.zhdaily;

import android.app.Application;
import android.content.Context;

/**
 * Created by liu on 2016/8/20 0020.
 */
public class MyApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext=getApplicationContext();

    }


    /**
     * 获取Context对象
     * @return
     */
    public static Context getContext(){
        return mContext;
    }
}
