package com.juhua.hangfen.bzrd.tools;

/**
 * Created by JiaJin Kuai on 2016/9/29.
 */
import java.io.Serializable;
import java.net.URLDecoder;
import java.net.URLEncoder;


/**
 * 接口URL实体类
 * @version 1.0
 * @created 2012-3-21
 */
public class URLs implements Serializable {

    public final static String HOST = "m.bitauto.com";
    public final static String HTTP = "http://";
    public final static String HTTPS = "https://";
    private final static String URL_SPLITTER = "/";
    private final static String URL_UNDERLINE = "_";

    private final static String URL_API_HOST = HTTP + HOST + URL_SPLITTER;


    public final static String NEWS_LIST = URL_API_HOST+"/appapi/News/List.ashx/";




    public final static int URL_OBJ_TYPE_OTHER = 0x000;
    public final static int URL_OBJ_TYPE_NEWS = 0x001;
    public final static int URL_OBJ_TYPE_SOFTWARE = 0x002;
    public final static int URL_OBJ_TYPE_QUESTION = 0x003;
    public final static int URL_OBJ_TYPE_ZONE = 0x004;
    public final static int URL_OBJ_TYPE_BLOG = 0x005;
    public final static int URL_OBJ_TYPE_TWEET = 0x006;
    public final static int URL_OBJ_TYPE_QUESTION_TAG = 0x007;

    private int objId;
    private String objKey = "";
    private int objType;

    public int getObjId() {
        return objId;
    }
    public void setObjId(int objId) {
        this.objId = objId;
    }
    public String getObjKey() {
        return objKey;
    }
    public void setObjKey(String objKey) {
        this.objKey = objKey;
    }
    public int getObjType() {
        return objType;
    }
    public void setObjType(int objType) {
        this.objType = objType;
    }

    /**
     * 解析url获得objId
     * @param path
     * @param url_type
     * @return
     */
    private final static String parseObjId(String path, String url_type){
        String objId = "";
        int p = 0;
        String str = "";
        String[] tmp = null;
        p = path.indexOf(url_type) + url_type.length();
        str = path.substring(p);
        if(str.contains(URL_SPLITTER)){
            tmp = str.split(URL_SPLITTER);
            objId = tmp[0];
        }else{
            objId = str;
        }
        return objId;
    }

    /**
     * 解析url获得objKey
     * @param path
     * @param url_type
     * @return
     */
    private final static String parseObjKey(String path, String url_type){
        path = URLDecoder.decode(path);
        String objKey = "";
        int p = 0;
        String str = "";
        String[] tmp = null;
        p = path.indexOf(url_type) + url_type.length();
        str = path.substring(p);
        if(str.contains("?")){
            tmp = str.split("[?]");
            objKey = tmp[0];
        }else{
            objKey = str;
        }
        return objKey;
    }

    /**
     * 对URL进行格式处理
     * @param path
     * @return
     */
    private final static String formatURL(String path) {
        if(path.startsWith("http://") || path.startsWith("https://"))
            return path;
        return "http://" + URLEncoder.encode(path);
    }
}