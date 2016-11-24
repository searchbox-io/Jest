package io.searchbox.indices.aliases;

import java.util.Map;

import org.elasticsearch.common.collect.MapBuilder;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.google.gson.Gson;

/**
 * @author cihat keser
 */
public class AddAliasMappingTest {

    public static final Map<String, Object> USER_FILTER_JSON = new MapBuilder<String, Object>()
            .put("term", MapBuilder.newMapBuilder()
                    .put("user", "kimchy")
                    .immutableMap())
            .immutableMap();

    @Test
    public void testBasicGetDataForJson() throws JSONException {
        AddAliasMapping addAliasMapping = new AddAliasMapping
                .Builder("tIndex", "tAlias")
                .build();
        String actualJson = new Gson().toJson(addAliasMapping.getData());
        String expectedJson = "[{\"add\":{\"index\":\"tIndex\",\"alias\":\"tAlias\"}}]";

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void testGetDataForJsonWithFilter() throws JSONException {
        AddAliasMapping addAliasMapping = new AddAliasMapping
                .Builder("tIndex", "tAlias")
                .setFilter(USER_FILTER_JSON)
                .build();
        String actualJson = new Gson().toJson(addAliasMapping.getData());
        String expectedJson = "[{\"add\":{\"index\":\"tIndex\",\"alias\":\"tAlias\",\"filter\":{\"term\":{\"user\":\"kimchy\"}}}}]";

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void testGetDataForJsonWithFilterAndRouting() throws JSONException {
        AddAliasMapping addAliasMapping = new AddAliasMapping
                .Builder("tIndex", "tAlias")
                .setFilter(USER_FILTER_JSON)
                .addRouting("1")
                .build();
        String actualJson = new Gson().toJson(addAliasMapping.getData());
        String expectedJson = "[{\"add\":{\"index\":\"tIndex\",\"alias\":\"tAlias\"," +
                "\"filter\":{\"term\":{\"user\":\"kimchy\"}},\"search_routing\":\"1\",\"index_routing\":\"1\"}}]";

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

}
