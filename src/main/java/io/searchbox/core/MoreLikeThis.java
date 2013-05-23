package io.searchbox.core;

import io.searchbox.AbstractAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */


public class MoreLikeThis extends AbstractAction {

    final static Logger log = LoggerFactory.getLogger(MoreLikeThis.class);

    public static class Builder {
        private final String id;
        private String index;
        private String type;
        private Object query;

        public Builder(String id) {
            this.id = id;
        }

        public Builder index(String val) {
            index = val;
            return this;
        }

        public Builder type(String val) {
            type = val;
            return this;
        }

        public Builder query(Object val) {
            query = val;
            return this;
        }

        public MoreLikeThis build() {
            return new MoreLikeThis(this);
        }

    }

    private MoreLikeThis(Builder builder) {
        indexName = builder.index;
        typeName = builder.type;
        id = builder.id;
        setData(builder.query);
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder(super.buildURI());
        sb.append("/_mlt");
        log.debug("Created URI for update action is :" + sb.toString());
        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return (getData() != null) ? "POST" : "GET";
    }

}
