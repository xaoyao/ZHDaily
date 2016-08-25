package org.eu.xaoyao.zhdaily.bean;

import java.util.ArrayList;

/**
 * Created by liu on 2016/8/22 0022.
 */
public class NewsListBean {

    public String date;
    public ArrayList<StoryBean> stories;
    public ArrayList<TopStoryBean> top_stories;

    public class StoryBean {
        public String publishDate;
        public ArrayList<String> images;
        public int type;
        public String id;
        public String ga_prefix;
        public String title;
    }

    public class TopStoryBean {
        public String image;
        public int type;
        public String id;
        public String ga_prefix;
        public String title;
    }
}
