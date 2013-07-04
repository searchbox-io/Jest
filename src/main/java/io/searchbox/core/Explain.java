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
        super(builder);
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

    public static class Builder extends AbstractDocumentTargetedAction.Builder<Explain, Builder> {
        private final Object query;

        public Builder(Object query) {
            this.query = query;
        }

        public Explain build() {
            return new Explain(this);
        }

    }

}
