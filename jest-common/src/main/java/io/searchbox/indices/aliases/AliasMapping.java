package io.searchbox.indices.aliases;

import com.google.common.base.Joiner;

import java.util.*;

/**
 * @author cihat keser
 */
public abstract class AliasMapping {

    protected List<String> indices = new LinkedList<String>();
    protected String alias;
    protected Map<String, Object> filter;
    protected List<String> searchRouting = new LinkedList<String>();
    protected List<String> indexRouting = new LinkedList<String>();

    public abstract String getType();

    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> retList = new LinkedList<Map<String, Object>>();

        for (String index : indices) {
            Map<String, Object> paramsMap = new LinkedHashMap<String, Object>();
            paramsMap.put("index", index);
            paramsMap.put("alias", alias);

            if (filter != null) {
                paramsMap.put("filter", filter);
            }

            if (searchRouting.size() > 0) {
                paramsMap.put("search_routing", Joiner.on(',').join(searchRouting));
            }

            if (indexRouting.size() > 0) {
                paramsMap.put("index_routing", Joiner.on(',').join(indexRouting));
            }

            Map<String, Object> actionMap = new LinkedHashMap<String, Object>();
            actionMap.put(getType(), paramsMap);
            retList.add(actionMap);
        }

        return retList;
    }

}
