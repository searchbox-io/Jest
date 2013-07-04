package io.searchbox.core;

import io.searchbox.AbstractDocumentTargetedAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class MoreLikeThis extends AbstractDocumentTargetedAction {

    final static Logger log = LoggerFactory.getLogger(MoreLikeThis.class);

    private MoreLikeThis(Builder builder) {
        super(builder);
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

    public static class Builder extends AbstractDocumentTargetedAction.Builder<MoreLikeThis, Builder> {
        private Object query;

        public Builder(String id) {
            this.id(id);
        }

        public Builder query(Object val) {
            query = val;
            return this;
        }

        public MoreLikeThis build() {
            return new MoreLikeThis(this);
        }

    }

}
