package com.google.gson;

/**
 * This class is just a workaround for the non-public deepCopy methods in Gson.
 */
public class GsonUtils {
    public static JsonObject deepCopy(JsonObject jsonObject) {
        return jsonObject.deepCopy();
    }

    public static JsonElement deepCopy(JsonElement jsonElement) {
        return jsonElement.deepCopy();
    }
}
