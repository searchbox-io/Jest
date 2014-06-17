package io.searchbox.indices.aliases;

import com.google.gson.Gson;
import org.elasticsearch.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author cihat keser
 */
public class ModifyAliasesTest {

    public static final Map<String, Object> USER_FILTER_JSON = ImmutableMap.<String, Object>builder()
            .put("term", ImmutableMap.<String, String>builder()
                    .put("user", "kimchy")
                    .build())
            .build();

    @Test
    public void testBasicGetData() {
        List<AliasMapping> aliasMappings = new LinkedList<AliasMapping>();
        aliasMappings.add(new AddAliasMapping.Builder("t_add_index", "t_add_alias").setFilter(USER_FILTER_JSON).build());
        aliasMappings.add(new RemoveAliasMapping.Builder("t_remove_index", "t_remove_alias").addRouting("1").build());
        ModifyAliases modifyAliases = new ModifyAliases.Builder(aliasMappings).build();
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
}
