package com.example.leer.zhbj.HttpData;

import java.util.List;

/**
 * Created by Leer on 2017/4/28.
 */

public class PhotosData {
    public Data data;
    public class Data{
        public String more;
        public List<PhotoNews> news;
    }

    public class PhotoNews{
        public String id;
        public String listimage;
        public String title;

    }
}
