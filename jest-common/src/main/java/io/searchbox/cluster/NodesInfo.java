package io.searchbox.cluster;

import io.searchbox.action.AbstractMultiINodeActionBuilder;
import io.searchbox.action.GenericResultAbstractAction;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class NodesInfo extends GenericResultAbstractAction {

    public NodesInfo(Builder builder) {
        super(builder);
        setPathToResult("nodes");
        setURI(buildURI(builder.requestedInfo));
    }

    protected String buildURI(Collection<String> requestedInfo) {
        StringBuilder sb = new StringBuilder(super.buildURI());
        sb.append("/_nodes").append("/").append(nodes);
        if (!requestedInfo.isEmpty()) {
            sb.append("/").append(StringUtils.join(requestedInfo, ","));
        }
        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    public static class Builder extends AbstractMultiINodeActionBuilder<NodesInfo, Builder> {

        Set<String> requestedInfo = new HashSet<String>();

        public Builder settings(boolean value) {
            return setRequestedInfoSection("settings", value);
        }

        public Builder os(boolean value) {
            return setRequestedInfoSection("os", value);
        }

        public Builder process(boolean value) {
            return setRequestedInfoSection("process", value);
        }

        public Builder jvm(boolean value) {
            return setRequestedInfoSection("jvm", value);
        }

        public Builder threadPool(boolean value) {
            return setRequestedInfoSection("thread_pool", value);
        }

        public Builder network(boolean value) {
            return setRequestedInfoSection("network", value);
        }

        public Builder transport(boolean value) {
            return setRequestedInfoSection("transport", value);
        }

        public Builder http(boolean value) {
            return setRequestedInfoSection("http", value);
        }

        public Builder plugins(boolean value) {
            return setRequestedInfoSection("plugins", value);
        }

        @Override
        public NodesInfo build() {
            return new NodesInfo(this);
        }

        private Builder setRequestedInfoSection(String section, boolean set) {
            if (set) {
                requestedInfo.add(section);
            } else if (requestedInfo.contains(section)) {
                requestedInfo.remove(section);
            }
            return this;
        }
    }
}

