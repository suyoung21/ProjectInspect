package com.glink.inspect.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * @author jiangshuyang
 */
public class GsonUtil {

    /**
     * jsong -- ArrayList
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> ArrayList<T> jsonToArrayList(String json, Class<T> clazz) {
        Type type = new TypeToken<ArrayList<JsonObject>>() {
        }.getType();
        ArrayList<JsonObject> jsonObjects = new Gson().fromJson(json, type);

        ArrayList<T> arrayList = new ArrayList<>();
        for (JsonObject jsonObject : jsonObjects) {
            arrayList.add(new Gson().fromJson(jsonObject, clazz));
        }
        return arrayList;
    }

    /**
     * json-object
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T>T jsonToObject(String json,Class<T> clazz){
        try {
            return new Gson().fromJson(json,clazz);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Object--->>String
     * @param object
     * @return
     */
    public static String toJsonString(Object object){
        return new Gson().toJson(object);
    }
}
