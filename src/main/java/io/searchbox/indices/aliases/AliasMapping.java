package io.searchbox.indices.aliases;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author cihat keser
 */
public abstract class AliasMapping {

    protected List<String> indices = new LinkedList<String>();
    protected String alias;
    protected String filter;
    protected List<String> searchRouting = new LinkedList<String>();
    protected List<String> indexRouting = new LinkedList<String>();

    public abstract String getType();

    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> retList = new LinkedList<Map<String, Object>>();

        for (String index : indices) {
            Map<String, Object> paramsMap = new HashMap<String, Object>();
            paramsMap.put("index", index);
            paramsMap.put("alias", alias);

            if (StringUtils.isNotEmpty(filter)) {
                paramsMap.put("filter", filter);
            }

            if (searchRouting.size() > 0) {
                paramsMap.put("search_routing", StringUtils.join(searchRouting, ","));
            }

            if (indexRouting.size() > 0) {
                paramsMap.put("index_routing", StringUtils.join(indexRouting, ","));
            }

            Map<String, Object> actionMap = new HashMap<String, Object>();
            actionMap.put(getType(), paramsMap);
            retList.add(actionMap);
        }

        return retList;
    }

}
