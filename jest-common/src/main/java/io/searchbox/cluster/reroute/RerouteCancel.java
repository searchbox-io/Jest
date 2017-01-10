package io.searchbox.cluster.reroute;

import java.util.LinkedHashMap;
import java.util.Map;

public class RerouteCancel implements RerouteCommand {

    private final String index;
    private final int shard;
    private final String node;
    private final boolean allowPrimary;

    public RerouteCancel(String index, int shard, String node, boolean allowPrimary) {
        this.index = index;
        this.shard = shard;
        this.node = node;
        this.allowPrimary = allowPrimary;
    }

    @Override
    public String getType() {
        return "cancel";
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> paramsMap = new LinkedHashMap<>();

        paramsMap.put("index", index);
        paramsMap.put("shard", shard);
        paramsMap.put("node", node);
        paramsMap.put("allow_primary", allowPrimary);

        return paramsMap;
    }
}
