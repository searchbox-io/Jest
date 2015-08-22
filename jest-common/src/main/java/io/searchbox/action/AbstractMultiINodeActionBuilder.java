package io.searchbox.action;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author cihat keser
 */
@SuppressWarnings("unchecked")
public abstract class AbstractMultiINodeActionBuilder<T extends Action, K> extends AbstractAction.Builder<T, K> {
    private Collection<String> nodes = new LinkedList<String>();

    /**
     * <p>Most cluster level APIs allow to specify which nodes to execute on (for example, getting the node stats for a node).
     * Nodes can be identified in the APIs either using their internal node id, the node name, address, custom attributes,
     * or just the _local node receiving the request. For example, here are some sample values for node:
     * </p>
     * <pre>
     *    # Local   -&gt;  _local
     *
     *    # Address -&gt;  10.0.0.3,10.0.0.4
     *              -&gt;  10.0.0.*
     *
     *    # Names   -&gt;  node_name_goes_here
     *              -&gt;  node_name_goes_*
     *
     *    # Attributes (set something like node.rack: 2 in the config)
     *              -&gt;  rack:2
     *                  -&gt;  ra*:2
     *              -&gt;  ra*:2*
     * </pre>
     */
    public K addNode(String node) {
        nodes.add(node);
        return (K) this;
    }

    /**
     * <p>Most cluster level APIs allow to specify which nodes to execute on (for example, getting the node stats for a node).
     * Nodes can be identified in the APIs either using their internal node id, the node name, address, custom attributes,
     * or just the _local node receiving the request. For example, here are some sample values for node:
     * </p>
     * <pre>
     *    # Local   -&gt;  _local
     *
     *    # Address -&gt;  10.0.0.3,10.0.0.4
     *              -&gt;  10.0.0.*
     *
     *    # Names   -&gt;  node_name_goes_here
     *              -&gt;  node_name_goes_*
     *
     *    # Attributes (set something like node.rack: 2 in the config)
     *              -&gt;  rack:2
     *                  -&gt;  ra*:2
     *              -&gt;  ra*:2*
     * </pre>
     */
    public K addNode(Collection<? extends String> nodes) {
        this.nodes.addAll(nodes);
        return (K) this;
    }

    public String getJoinedNodes() {
        if (nodes.size() > 0) {
            return StringUtils.join(nodes, ",");
        } else {
            return "_all";
        }
    }

    abstract public T build();
}
