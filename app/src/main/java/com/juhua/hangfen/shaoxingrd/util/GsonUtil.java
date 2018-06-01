package com.juhua.hangfen.shaoxingrd.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;

/**
 * Created by congj on 2017/9/13.
 */

public class GsonUtil {
    // 将Json数据解析成相应的映射对象
    public static <T> T parseJsonWithGson(String jsonData, Class<T> type) {
        Gson gson = new Gson();
        T result = gson.fromJson(jsonData, type);
        return result;
    }

    // 将Json数组解析成相应的映射对象列表
    public static <T> List<T> parseJsonArrayWithGson(String jsonData,
                                                     Class<T> type) {
        Gson gson = new Gson();
        List<T> result = gson.fromJson(jsonData, new TypeToken<List<T>>() {
        }.getType());
        return result;
    }

    public static HashMap<String, Object> parseJsonObject(String jsonData){
        Gson gson = new Gson();
        HashMap<String, Object> result = gson.fromJson(jsonData, HashMap.class);
        return result;

    }

    public static String beanToJSONString(Object bean) {
        return new Gson().toJson(bean);
    }
}
