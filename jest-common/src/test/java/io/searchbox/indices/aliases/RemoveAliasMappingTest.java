package io.searchbox.indices.aliases;

import com.google.gson.Gson;
import org.elasticsearch.common.collect.MapBuilder;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author cihat keser
 */
public class RemoveAliasMappingTest {

    public static final Map<String, Object> USER_FILTER_JSON = new MapBuilder<String, Object>()
            .put("term", MapBuilder.newMapBuilder()
                    .put("user", "kimchy")
                    .immutableMap())
            .immutableMap();

    @Test
    public void testBasicGetDataForJson() {
        RemoveAliasMapping addAliasMapping = new RemoveAliasMapping
                .Builder("tIndex", "tAlias")
                .build();
        String actualJson = new Gson().toJson(addAliasMapping.getData()).toString();
        String expectedJson = "[{\"remove\":{\"index\":\"tIndex\",\"alias\":\"tAlias\"}}]";

        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void testGetDataForJsonWithFilter() {
        RemoveAliasMapping addAliasMapping = new RemoveAliasMapping
                .Builder("tIndex", "tAlias")
                .setFilter(USER_FILTER_JSON)
                .build();
        String actualJson = new Gson().toJson(addAliasMapping.getData()).toString();
        String expectedJson = "[{\"remove\":{\"index\":\"tIndex\",\"alias\":\"tAlias\",\"filter\":{\"term\":{\"user\":\"kimchy\"}}}}]";

        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void testGetDataForJsonWithFilterAndRouting() {
        RemoveAliasMapping addAliasMapping = new RemoveAliasMapping
                .Builder("tIndex", "tAlias")
                .setFilter(USER_FILTER_JSON)
                .addRouting("1")
                .build();
        String actualJson = new Gson().toJson(addAliasMapping.getData()).toString();
        String expectedJson = "[{\"remove\":{\"index\":\"tIndex\",\"alias\":\"tAlias\",\"filter\":{\"term\":{\"user\":\"kimchy\"}},\"search_routing\":\"1\",\"index_routing\":\"1\"}}]";

        assertEquals(expectedJson, actualJson);
    }

}
