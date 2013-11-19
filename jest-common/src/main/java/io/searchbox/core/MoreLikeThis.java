package io.searchbox.core;

import com.google.gson.Gson;
import io.searchbox.AbstractDocumentTargetedAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class MoreLikeThis extends AbstractDocumentTargetedAction {

    final static Logger log = LoggerFactory.getLogger(MoreLikeThis.class);
    private Object query;

    private MoreLikeThis(Builder builder) {
        super(builder);

        this.query = builder.query;
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
        return (query != null) ? "POST" : "GET";
    }

    @Override
    public Object getData(Gson gson) {
        return query;
    }

    public static class Builder extends AbstractDocumentTargetedAction.Builder<MoreLikeThis, Builder> {
        private Object query;

        public Builder(String index, String type, String id, Object query) {
            this.index(index);
            this.type(type);
            this.id(id);
            this.query = query;
        }

        public MoreLikeThis build() {
            return new MoreLikeThis(this);
        }

    }

}
