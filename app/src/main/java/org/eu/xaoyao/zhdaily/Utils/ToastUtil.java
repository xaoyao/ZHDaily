package org.eu.xaoyao.zhdaily.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by liu on 2016/8/20 0020.
 */
public class ToastUtil {
    private static Toast mToast;

    /**
     * 显示一个toast
     * @param context
     * @param content
     */
    public static void showToast(Context context, String content) {
        if (mToast == null) {
            mToast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(content);
        }
        mToast.show();
    }

}
