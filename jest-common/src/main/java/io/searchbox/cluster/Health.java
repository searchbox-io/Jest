package io.searchbox.cluster;

import com.google.common.base.Preconditions;
import io.searchbox.action.AbstractMultiIndexActionBuilder;
import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;
import io.searchbox.strings.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author Dogukan Sonmez
 * @author Neil Gentleman
 */
public class Health extends GenericResultAbstractAction {
    public enum Status {
        RED("red"), YELLOW("yellow"), GREEN("green");

        private final String key;

        Status(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    public enum Level {
        CLUSTER("cluster"), INDICES("indices"), SHARDS("shards");

        private final String key;

        Level(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    protected Health(Builder builder) {
        super(builder);
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        StringBuilder sb = new StringBuilder("/_cluster/health/");

        try {
            if (StringUtils.isNotBlank(indexName)) {
                sb.append(URLEncoder.encode(indexName, CHARSET));
            }
        } catch (UnsupportedEncodingException e) {
            // unless CHARSET is overridden with a wrong value in a subclass,
            // this exception won't be thrown.
            log.error("Error occurred while adding index to uri", e);
        }

        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    public static class Builder extends AbstractMultiIndexActionBuilder<Health, Builder> {
        public Builder waitForNoRelocatingShards() {
            return waitForNoRelocatingShards(true);
        }

        public Builder waitForNoRelocatingShards(boolean wait) {
            return setParameter("wait_for_no_relocating_shards", wait);
        }

        public Builder waitForStatus(Status status) {
            return waitForStatus(status.getKey());
        }

        private Builder waitForStatus(String status) {
            return setParameter("wait_for_status", status);
        }

        public Builder level(Level level) {
            return level(level.getKey());
        }

        private Builder level(String level) {
            return setParameter("level", level);
        }

        public Builder local(boolean local) {
            return setParameter("local", local);
        }

        public Builder local() {
            return local(true);
        }

        public Builder timeout(int seconds) {
            Preconditions.checkArgument(seconds >= 0, "seconds must not be negative");
            return setParameter("timeout", seconds + "s");
        }

        @Override
        public Health build() {
            return new Health(this);
        }
    }

}
