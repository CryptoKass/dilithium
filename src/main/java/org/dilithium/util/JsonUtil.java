package org.dilithium.util;

import com.google.gson.Gson;

public class JsonUtil {
    private static final Gson GSON = new Gson();

    public static String getJson(Object object){
        return GSON.toJson(object);
    }

}
