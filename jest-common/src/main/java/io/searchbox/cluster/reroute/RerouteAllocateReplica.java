package io.searchbox.cluster.reroute;

import java.util.LinkedHashMap;
import java.util.Map;

public class RerouteAllocateReplica implements RerouteCommand {

    private final String index;
    private final int shard;
    private final String node;

    public RerouteAllocateReplica(String index, int shard, String node) {
        this.index = index;
        this.shard = shard;
        this.node = node;
    }

    @Override
    public String getType() {
        return "allocate_replica";
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> paramsMap = new LinkedHashMap<>();

        paramsMap.put("index", index);
        paramsMap.put("shard", shard);
        paramsMap.put("node", node);

        return paramsMap;
    }

}
