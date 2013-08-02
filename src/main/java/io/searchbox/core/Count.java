package io.searchbox.core;

import io.searchbox.AbstractAction;
import io.searchbox.AbstractMultiTypeActionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Count extends AbstractAction {

    final static Logger log = LoggerFactory.getLogger(Count.class);

    public Count(Builder builder) {
        super(builder);
        setData(builder.query);
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_count");
        return sb.toString();
    }

    @Override
    public String getPathToResult() {
        return "count";
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    public static class Builder extends AbstractMultiTypeActionBuilder<Count, Builder> {
        private String query;

        public Builder query(String query) {
            this.query = query;
            return this;
        }

        @Override
        public Count build() {
            return new Count(this);
        }
    }
}
