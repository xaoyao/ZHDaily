package org.eu.xaoyao.zhdaily;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by liu on 2016/9/1 0001.
 */
public class ActivityCollector {

    public static ArrayList<Activity> mActivities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        mActivities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        mActivities.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : mActivities) {
            activity.finish();
        }
    }


}
