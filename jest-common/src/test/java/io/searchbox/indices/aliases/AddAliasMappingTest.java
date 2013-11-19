package io.searchbox.indices.aliases;

import com.google.gson.Gson;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author cihat keser
 */
public class AddAliasMappingTest {

    @Test
    public void testBasicGetDataForJson() {
        AddAliasMapping addAliasMapping = new AddAliasMapping
                .Builder("tIndex", "tAlias")
                .build();
        String actualJson = new Gson().toJson(addAliasMapping.getData()).toString();
        String expectedJson = "[{\"add\":{\"index\":\"tIndex\",\"alias\":\"tAlias\"}}]";

        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void testGetDataForJsonWithFilter() {
        AddAliasMapping addAliasMapping = new AddAliasMapping
                .Builder("tIndex", "tAlias")
                .setFilter("my_query")
                .build();
        String actualJson = new Gson().toJson(addAliasMapping.getData()).toString();
        String expectedJson = "[{\"add\":{\"index\":\"tIndex\",\"alias\":\"tAlias\",\"filter\":\"my_query\"}}]";

        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void testGetDataForJsonWithFilterAndRouting() {
        AddAliasMapping addAliasMapping = new AddAliasMapping
                .Builder("tIndex", "tAlias")
                .setFilter("my_query")
                .addRouting("1")
                .build();
        String actualJson = new Gson().toJson(addAliasMapping.getData()).toString();
        String expectedJson = "[{\"add\":{\"search_routing\":\"1\",\"index\":\"tIndex\",\"alias\":\"tAlias\",\"index_routing\":\"1\",\"filter\":\"my_query\"}}]";

        assertEquals(expectedJson, actualJson);
    }

}
