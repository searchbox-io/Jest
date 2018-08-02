package io.searchbox.core;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.searchbox.client.JestResult;

import java.util.List;
import java.util.Map;

/**
* @author Bartosz Polnik
*/
public class CatResult extends JestResult {

    public CatResult(CatResult catResult) {
        super(catResult);
    }

    public CatResult(Gson gson) {
        super(gson);
    }

    /**
     *
     * @return empty array if response is not present, otherwise column names as first row plus one additional row per single result
     */
    public String[][] getPlainText() {
        JsonObject jsonObject = getJsonObject();
        if (jsonObject != null && getPathToResult() != null && jsonObject.has(getPathToResult()) && jsonObject.get(getPathToResult()).isJsonArray()) {
            JsonArray esResultRows = jsonObject.get(getPathToResult()).getAsJsonArray();
            if(esResultRows.size() > 0 && esResultRows.get(0).isJsonObject()) {
                return parseResultArray(esResultRows);
            }
        }

        return new String[0][0];
    }

    private String[][] parseResultArray(JsonArray esResponse) {
        List<Map.Entry<String, JsonElement>> fieldsInFirstResponseRow = Lists.newArrayList(esResponse.get(0).getAsJsonObject().entrySet());
        String[][] result = new String[esResponse.size() + 1][fieldsInFirstResponseRow.size()];
        for(int i = 0; i < fieldsInFirstResponseRow.size(); i++) {
            result[0][i] = fieldsInFirstResponseRow.get(i).getKey();
        }

        int rowNum = 1;
        for(JsonElement row: esResponse) {
            JsonObject currentObj = row.getAsJsonObject();
            for (int colId = 0; colId < fieldsInFirstResponseRow.size(); colId++) {
                result[rowNum][colId] = currentObj.get(result[0][colId]).getAsString();
            }

            rowNum++;
        }

        return result;
    }
}
