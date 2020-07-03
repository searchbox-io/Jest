package io.searchbox.core.search.aggregation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

public class CompositeAggregationTest {

    final String afterKey = "'after_key':{'field':'valB'}";
    final String bucketsArr = "'buckets':[" +
            "{  'key':{'field':'val'}," +
            "   'doc_count':1," +
            "   'sub_agg':{}" +
            "}," +
            "{  'key':{'field':'valA'}," +
            "   'doc_count':2," +
            "   'sub_agg':{}" +
            "}" + "]";
    final Gson gson = new Gson();

    @Test
    public void testParseBuckets() {
        for (String input : Arrays.asList("{" + afterKey + ", " + bucketsArr + "}", "{" + bucketsArr + "}")) {
            JsonObject compositeAggregationJson = gson.fromJson(
                    input.replace('\'', '\"'), JsonObject.class);
            CompositeAggregation compositeAggregation = new CompositeAggregation("composite", compositeAggregationJson);
            List<CompositeAggregation.Entry> buckets = compositeAggregation.getBuckets();
            assertNotNull(buckets);
            assertEquals(2, buckets.size());

            assertEquals("val", buckets.get(0).getKey().get("field").getAsString());
            assertEquals(Long.valueOf(1L), buckets.get(0).getCount());
            assertTrue(buckets.get(0).getTermsAggregation("sub_agg").getBuckets().isEmpty());

            assertEquals("valA", buckets.get(1).getKey().get("field").getAsString());
            assertEquals(Long.valueOf(2L), buckets.get(1).getCount());
            assertTrue(buckets.get(1).getTermsAggregation("sub_agg").getBuckets().isEmpty());
        }
    }

    @Test
    public void testParseAfterKey() {
        for (String input : Arrays.asList("{" + afterKey + ", " + bucketsArr + "}", "{" + afterKey + "}")) {
            JsonObject compositeAggregationJson = gson.fromJson(
                    input.replace('\'', '\"'), JsonObject.class);
            CompositeAggregation compositeAggregation = new CompositeAggregation("composite", compositeAggregationJson);
            assertEquals("valB", compositeAggregation.getAfterKey().get("field").getAsString());
        }
    }

    @Test
    public void testNoAfterKey() {
        for (String input : Arrays.asList("{"  + bucketsArr + "}", "{" + "}")) {
            JsonObject compositeAggregationJson = gson.fromJson(
                    input.replace('\'', '\"'), JsonObject.class);
            CompositeAggregation compositeAggregation = new CompositeAggregation("composite", compositeAggregationJson);
            assertNull(compositeAggregation.getAfterKey());
        }
    }
}