package io.searchbox.cluster;

import io.searchbox.AbstractAction;
import io.searchbox.Action;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Dogukan Sonmez
 */

public class NodesInfo extends AbstractAction implements Action {

    final static Logger log = LoggerFactory.getLogger(NodesInfo.class);

    final protected LinkedHashSet<String> nodeSet = new LinkedHashSet<String>();

    final private LinkedHashSet<String> infoSet = new LinkedHashSet<String>();

    public void addNode(String node) {
        if (StringUtils.isNotBlank(node)) nodeSet.add(node);
    }

    public void addInfo(String info) {
        if (StringUtils.isNotBlank(info)) infoSet.add(info);
    }

    public boolean removeNode(String node) {
        return nodeSet.remove(node);
    }

    public boolean removeInfo(String info) {
        return infoSet.remove(info);
    }

    public void clearAllNode() {
        nodeSet.clear();
    }

    public void clearAllInfo() {
        infoSet.clear();
    }

    public void addNode(Collection<String> node) {
        nodeSet.addAll(node);
    }

    public void addInfo(Collection<String> info) {
        infoSet.addAll(info);
    }

    public boolean isNodeExist(String node) {
        return nodeSet.contains(node);
    }

    public boolean isInfoExist(String info) {
        return infoSet.contains(info);
    }

    public int nodeSize() {
        return nodeSet.size();
    }

    public int infoSize() {
        return infoSet.size();
    }

    public String getURI() {
        StringBuilder sb = new StringBuilder("_cluster/nodes");
        String nodes = concatenateString(nodeSet);
        if (nodes.length() > 0) {
            sb.append("/").append(nodes);
        }
        String info = buildQueryStringForInfo(infoSet);
        if (info.length() > 0) {
            sb.append("?").append(info);
        }

        log.debug("Created URI for Nodes Info action is : " + sb.toString());
        return sb.toString();
    }

    protected String buildQueryStringForInfo(Set<String> infoSet) {
        StringBuilder sb = new StringBuilder();
        String tmp = "";
        for (String info : infoSet) {
            sb.append(tmp);
            sb.append(info).append("=true");
            tmp = "&";
        }
        return sb.toString();
    }

    protected String concatenateString(Set<String> set) {
        StringBuilder sb = new StringBuilder();
        String tmp = "";
        for (String node : set) {
            sb.append(tmp);
            sb.append(node);
            tmp = ",";
        }
        return sb.toString();
    }

    @Override
    public String getPathToResult() {
        return "nodes";
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }
}

