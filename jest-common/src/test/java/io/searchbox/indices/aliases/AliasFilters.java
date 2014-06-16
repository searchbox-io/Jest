package io.searchbox.indices.aliases;

import java.util.HashMap;
import java.util.Map;

public class AliasFilters {

    private AliasFilters() {
        throw new AssertionError("Utility class");
    }

    public static final Map<String, Object> USER_KIMCHY_FILTER_JSON = new HashMap<String, Object>();
    static {
        Map<String, Object> term = new HashMap<String, Object>();
        term.put("user", "kimchy");
        USER_KIMCHY_FILTER_JSON.put("term", term);
    }

    public static final String USER_KIMCHY_FILTER_STRING = "{\"term\":{\"user\":\"kimchy\"}}";
}
