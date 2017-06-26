package com.example.leer.zhbj.HttpData;

import java.util.List;

/**
 * Created by Leer on 2017/4/21.
 */

public class NewsDetailsData {
    public NewsRootData data;


    public class NewsRootData{
        public String more;
        public List<TopNews> topnews;
        public List<Topic> topic;
        public List<News> news;

    }

    public class TopNews{
        public String id;
        public String title;//头条新闻的标题
        public String topimage;//头条新闻的图片
        public String url;//头条新闻具体的新闻内容
    }

    public class Topic{
        public String title;
        public String id;
        public String url;
        public String listimage;
        public String description;
    }

    public class News{
        public String id;
        public String title;
        public String listimage;
        public String url;
        public String pubdate;
    }


}
