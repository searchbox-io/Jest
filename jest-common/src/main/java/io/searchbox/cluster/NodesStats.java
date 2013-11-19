package io.searchbox.cluster;

import io.searchbox.AbstractAction;
import io.searchbox.AbstractMultiINodeActionBuilder;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class NodesStats extends AbstractAction {

    public NodesStats(Builder builder) {
        super(builder);
        setPathToResult("nodes");
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder(super.buildURI());
        sb.append("/_cluster/nodes/")
                .append(nodes)
                .append("/stats");
        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    public static class Builder extends AbstractMultiINodeActionBuilder<NodesStats, Builder> {

        public Builder settings(boolean value) {
            return setParameter("settings", value);
        }

        /**
         * Indices stats about size, document count, indexing and deletion times, search times, field cache size , merges and flushes
         */
        public Builder indices(boolean value) {
            return setParameter("indices", value);
        }

        /**
         * File system information, data path, free disk space, read/write stats
         */
        public Builder fs(boolean value) {
            return setParameter("fs", value);
        }

        /**
         * HTTP connection information
         */
        public Builder http(boolean value) {
            return setParameter("http", value);
        }

        /**
         * JVM stats, memory pool information, garbage collection, buffer pools
         */
        public Builder jvm(boolean value) {
            return setParameter("jvm", value);
        }

        /**
         * TCP information
         */
        public Builder network(boolean value) {
            return setParameter("network", value);
        }

        /**
         * Operating system stats, load average, cpu, mem, swap
         */
        public Builder os(boolean value) {
            return setParameter("os", value);
        }

        /**
         * Process statistics, memory consumption, cpu usage, open file descriptors
         */
        public Builder process(boolean value) {
            return setParameter("process", value);
        }

        /**
         * Statistics about each thread pool, including current size, queue and rejected tasks
         */
        public Builder threadPool(boolean value) {
            return setParameter("thread_pool", value);
        }

        /**
         * Transport statistics about sent and received bytes in cluster communication
         */
        public Builder transport(boolean value) {
            return setParameter("transport", value);
        }

        /**
         * Clears all the flags (first). Useful, if you only want to retrieve specific stats
         */
        public Builder clear(boolean value) {
            return setParameter("clear", value);
        }

        @Override
        public NodesStats build() {
            return new NodesStats(this);
        }
    }
}

