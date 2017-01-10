package io.searchbox.cluster.reroute;


import java.util.LinkedHashMap;
import java.util.Map;

public class RerouteMove implements RerouteCommand {

    private final String index;
    private final int shard;
    private final String fromNode;
    private final String toNode;

    public RerouteMove(String index, int shard, String fromNode, String toNode) {
        this.index = index;
        this.shard = shard;
        this.fromNode = fromNode;
        this.toNode = toNode;
    }

    @Override
    public String getType() {
        return "move";
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> paramsMap = new LinkedHashMap<String, Object>();
        paramsMap.put("index", index);
        paramsMap.put("shard", shard);
        paramsMap.put("from_node", fromNode);
        paramsMap.put("to_node", toNode);

        return paramsMap;
    }

}
