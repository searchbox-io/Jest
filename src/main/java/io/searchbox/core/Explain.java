package io.searchbox.core;

import io.searchbox.AbstractDocumentTargetedAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Explain extends AbstractDocumentTargetedAction {

    final static Logger log = LoggerFactory.getLogger(Explain.class);

    private Explain(Builder builder) {
        indexName = builder.index;
        typeName = builder.type;
        id = builder.id;
        setURI(buildURI());
        setData(builder.query);
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder(super.buildURI());
        sb.append("/_explain");
        log.debug("Created URI for explain action is :" + sb.toString());
        return sb.toString();
    }

    public static class Builder {
        private final Object query;
        private String id;
        private String index;
        private String type;

        public Builder(Object query) {
            this.query = query;
        }

        public Builder index(String val) {
            index = val;
            return this;
        }

        public Builder type(String val) {
            type = val;
            return this;
        }

        public Builder id(String val) {
            id = val;
            return this;
        }

        public Explain build() {
            return new Explain(this);
        }

    }

}
