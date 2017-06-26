package com.example.leer.zhbj.HttpData;

/**
 * Created by Leer on 2017/4/6.
 */

public class NewsData {
    public int retcode;
    public NewsGroup[] data;

    @Override
    public String toString() {
        return "解析后的json数据: "+retcode + data.length;
    }
}
