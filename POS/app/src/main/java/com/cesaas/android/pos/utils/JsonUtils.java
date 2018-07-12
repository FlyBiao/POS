
package com.cesaas.android.pos.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Java对象和JSON字符串相互转化工具类 
 * 
 * @author FGB
 */
public class JsonUtils {

	private JsonUtils(){}

    /**
     * JSON 转对象
     * @param str json字符
     * @param type 需要转换的对象类型
     * @param <T> 泛型
     * @return json对象
     */
    public static <T> T fromJson(String str, Class<T> type) {
        Gson gson = new Gson();
        return gson.fromJson(str, type);
    }


    /**
     * 将对象转换为json字符�?
     * 
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    /**
     * 将json字符串转为指定的对象
     * 
     * @param json json字符�?
     * @param cls 目标对象
     * @return
     */
    public static <T> T objectFromJson(String json, Class<T> cls) {
        Gson gson = new Gson();
        return gson.fromJson(json, cls);
    }
    
    /**
	 * 将给定的 {@code JSON} 字符串转换成指定的类型对象�??
	 * 
	 * @param <T>
	 *            要转换的目标类型�?
	 * @param json
	 *            给定�? {@code JSON} 字符串�??
	 * @param
	 *            {@code com.google.gson.reflect.TypeToken} 的类型指示类对象�?
	 * @param
	 *
	 * @return 给定�? {@code JSON} 字符串表示的指定的类型对象�??
	 */
	public static <T> T fromJson(String json, Type type) {
		try {
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			return gson.fromJson(json, type);
		} catch (Exception e) {
		}
		return null;
		
	}

    /**
     * 将json字符串转为指定的ArrayList
     * 
     * @param json
     * @return
     */
    public static <T> ArrayList<T> listFromJson(String json, Class<T> cls) throws JSONException {
        ArrayList<T> list = new ArrayList<T>();
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        JSONArray array = new JSONArray(json);
        int len = 0;
        T t;
        if (array != null && (len = array.length()) > 0) {
            for (int i = 0; i < len; i++) {
                t = JsonUtils.objectFromJson(array.getString(i), cls);
                if (t != null) {
                    list.add(t);
                }
            }
        }

        return list;
    }

}
