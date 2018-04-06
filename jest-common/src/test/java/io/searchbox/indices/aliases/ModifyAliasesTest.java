package io.searchbox.indices.aliases;

import io.searchbox.client.config.ElasticsearchVersion;
import org.elasticsearch.common.collect.MapBuilder;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author cihat keser
 */
public class ModifyAliasesTest {

    Map<String, Object> userFilter = new MapBuilder<String, Object>()
            .put("term", MapBuilder.newMapBuilder()
                    .put("user", "kimchy")
                    .immutableMap())
            .immutableMap();
    AliasMapping addMapping = new AddAliasMapping.Builder("t_add_index", "t_add_alias").setFilter(userFilter).build();
    AliasMapping removeMapping = new RemoveAliasMapping.Builder("t_remove_index", "t_remove_alias").addRouting("1").build();

    @Test
    public void testBasicUriGeneration() {
        ModifyAliases modifyAliases = new ModifyAliases.Builder(addMapping).build();

        assertEquals("POST", modifyAliases.getRestMethodName());
        assertEquals("/_aliases", modifyAliases.getURI(ElasticsearchVersion.UNKNOWN));
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
