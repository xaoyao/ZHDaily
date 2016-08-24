package org.eu.xaoyao.zhdaily.bean;

import java.util.ArrayList;

/**
 * Created by liu on 2016/8/24 0024.
 */
public class NewsThemesBean {

    public int limit;
    public ArrayList<NewsThemeBean> others;


    public class NewsThemeBean{
        public int color;
        public String thumbnail;
        public String description;
        public int id;
        public String name;
    }

}
