package io.searchbox.indices.aliases;

import com.google.gson.Gson;
import org.elasticsearch.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author cihat keser
 */
public class ModifyAliasesTest {

    Map<String, Object> userFilter = ImmutableMap.<String, Object>builder()
            .put("term", ImmutableMap.<String, String>builder()
                    .put("user", "kimchy")
                    .build())
            .build();
    AliasMapping addMapping = new AddAliasMapping.Builder("t_add_index", "t_add_alias").setFilter(userFilter).build();
    AliasMapping removeMapping = new RemoveAliasMapping.Builder("t_remove_index", "t_remove_alias").addRouting("1").build();

    @Test
    public void testBasicUriGeneration() {
        ModifyAliases modifyAliases = new ModifyAliases.Builder(addMapping).build();

        assertEquals("POST", modifyAliases.getRestMethodName());
        assertEquals("/_aliases", modifyAliases.getURI());
    }

    @Test
    public void testBasicGetData() {
        ModifyAliases modifyAliases = new ModifyAliases.Builder(addMapping).addAlias(removeMapping).build();

        assertEquals("{" +
                "\"data\":{\"actions\":[" +
                        "{\"add\":{\"index\":\"t_add_index\",\"alias\":\"t_add_alias\",\"filter\":{\"term\":{\"user\":\"kimchy\"}}}}," +
                "{\"remove\":{\"search_routing\":\"1\",\"index\":\"t_remove_index\",\"alias\":\"t_remove_alias\",\"index_routing\":\"1\"}}]}," +
                "\"headerMap\":{}," +
                "\"parameterMap\":{}," +
                "\"URI\":\"/_aliases\"," +
                "\"isBulkOperation\":false,"+
                "\"cleanApi\":false}",
                new Gson().toJson(modifyAliases)
        );
    }

    @Test
    public void equalsReturnsTrueForSameMappings() {
        ModifyAliases modifyAliases1 = new ModifyAliases.Builder(addMapping).addAlias(removeMapping).build();
        ModifyAliases modifyAliases1Duplicate = new ModifyAliases.Builder(addMapping).addAlias(removeMapping).build();

        assertEquals(modifyAliases1, modifyAliases1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentMappings() {
        ModifyAliases modifyAliases1 = new ModifyAliases.Builder(addMapping).addAlias(removeMapping).build();
        ModifyAliases modifyAliases2 = new ModifyAliases.Builder(addMapping).build();

        assertNotEquals(modifyAliases1, modifyAliases2);
    }

}
