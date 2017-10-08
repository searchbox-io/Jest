package io.searchbox.core;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class BulkResultTest {

    static String indexFailedResult = "{\n" +
            "    \"took\": 10,\n" +
            "    \"errors\": true,\n" +
            "    \"items\": [\n" +
            "        {\n" +
            "            \"index\": {\n" +
            "                \"_index\": \"index-name\",\n" +
            "                \"_type\": \"type-name\",\n" +
            "                \"_id\": null,\n" +
            "                \"status\": 400,\n" +
            "                \"error\": \"MapperParsingException[mapping [type-name]]; nested: MapperParsingException[No type specified for property [field-name]]; \"\n" +
            "            }\n" +
            "        }\n" +
            "    ]\n" +
            "}";

    @Test
    public void bulkResultWithFailures() {
        BulkResult bulkResult = new BulkResult(new GsonBuilder().serializeNulls().create());
        bulkResult.setJsonString(indexFailedResult);
        bulkResult.setJsonMap(new Gson().fromJson(indexFailedResult, Map.class));
        bulkResult.setSucceeded(false);

        assertEquals(1, bulkResult.getItems().size());
        assertEquals(1, bulkResult.getFailedItems().size());
    }
}
