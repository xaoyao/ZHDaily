package org.eu.xaoyao.zhdaily.bean;

import java.util.ArrayList;

/**
 * Created by liu on 2016/9/2 0002.
 */
public class CommentsListBean {

    public ArrayList<CommentBean> comments;

    public class CommentBean {
        public String author;
        public String content;
        public String avatar;
        public long time;
        public ReplayToBean reply_to;
        public String id;
        public int likes;
    }

    public class ReplayToBean {
        public String content;
        public int status;
        public String id;
        public String author;
    }
}
