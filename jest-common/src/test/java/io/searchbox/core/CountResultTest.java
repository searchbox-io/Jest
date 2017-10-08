package io.searchbox.core;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author cihat keser
 */
public class CountResultTest {

    String json = "{\n" +
            "    \"count\" : 1,\n" +
            "    \"_shards\" : {\n" +
            "        \"total\" : 5,\n" +
            "        \"successful\" : 5,\n" +
            "        \"failed\" : 0\n" +
            "    }\n" +
            "}";

    @Test
    public void testGetCount() {
        CountResult countResult = new CountResult(new Gson());
        countResult.setSucceeded(true);
        countResult.setJsonString(json);
        countResult.setJsonObject(new JsonParser().parse(json).getAsJsonObject());
        countResult.setPathToResult("count");

        Double count = countResult.getCount();
        assertNotNull(count);
    }

    @Test
    public void testGetCountWhenOperationFails() {
        CountResult countResult = new CountResult(new Gson());
        countResult.setSucceeded(false);

        Double count = countResult.getCount();
        assertNull(count);
    }

}
