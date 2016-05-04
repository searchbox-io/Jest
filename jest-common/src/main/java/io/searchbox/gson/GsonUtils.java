package io.searchbox.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rits.cloning.Cloner;

/**
 * This class is just a workaround for the non-public deepCopy methods in Gson.
 */
public class GsonUtils {
    static Cloner cloner = new Cloner();

    public static JsonObject deepCopy(JsonObject jsonObject) {
        return cloner.deepClone(jsonObject);
    }

    public static JsonElement deepCopy(JsonElement jsonElement) {
        return cloner.deepClone(jsonElement);
    }
}
