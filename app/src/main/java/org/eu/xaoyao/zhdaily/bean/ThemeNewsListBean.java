package org.eu.xaoyao.zhdaily.bean;

import java.util.ArrayList;

/**
 * Created by liu on 2016/8/26 0026.
 */
public class ThemeNewsListBean {
    public ArrayList<ThemeNewsBean> stories;
    public String description;
    public String background;
    public int color;
    public String name;
    public String image;
    public ArrayList<EditorBean> editors;

    public class ThemeNewsBean {
        public ArrayList<String> images;
        public int type;
        public String id;
        public String title;
    }

    public class EditorBean {
        public String url;
        public String bio;
        public String id;
        public String avatar;
        public String name;
    }

}
