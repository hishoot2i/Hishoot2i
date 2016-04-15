package org.illegaller.ratabb.hishoot2i.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import android.support.annotation.Nullable;

public class GsonUtils {
    private static final Gson GSON = new Gson();

    private GsonUtils() {        //no instance
    }

    @Nullable public static <T> T fromJson(String jsonString, Class<T> clazz) {
        T result = null;
        try {
            result = GSON.fromJson(jsonString, clazz);
        } catch (JsonSyntaxException e) {
            CrashLog.logError("parsing json", e);
        }
        return result;
    }
}
