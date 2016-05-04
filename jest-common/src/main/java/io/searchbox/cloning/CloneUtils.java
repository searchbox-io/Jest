package io.searchbox.cloning;

import com.google.gson.*;

import java.util.Map;

/**
 * This class is just a workaround for the non-public deepCopy methods in Gson.
 */
public class CloneUtils {
    public static JsonElement deepClone(JsonElement jsonElement) {
        if (jsonElement instanceof JsonObject) {
            return deepCloneObject(jsonElement);
        } else if (jsonElement instanceof JsonArray) {
            return deepCloneArray(jsonElement);
        } else if (jsonElement instanceof JsonPrimitive) {
            return jsonElement;
        }

        return JsonNull.INSTANCE;
    }

    private static JsonElement deepCloneObject(JsonElement jsonElement) {
        JsonObject jsonObject = (JsonObject) jsonElement;
        JsonObject result = new JsonObject();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            result.add(entry.getKey(), deepClone(entry.getValue()));
        }

        return result;
    }

    private static JsonElement deepCloneArray(JsonElement jsonElement) {
        JsonArray jsonArray = (JsonArray) jsonElement;
        JsonArray result = new JsonArray();

        for (JsonElement element : jsonArray) {
            result.add(deepClone(element));
        }

        return result;
    }
}
