package io.searchbox.action;

import io.searchbox.client.JestResult;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author cihat keser
 */
public abstract class AbstractDocumentTargetedAction<T extends JestResult> extends AbstractAction<T> implements DocumentTargetedAction<T> {

    protected String id;

    public AbstractDocumentTargetedAction(Builder builder) {
        super(builder);
        indexName = builder.index;
        typeName = builder.type;
        id = builder.id;
    }

    @Override
    public String getIndex() {
        return indexName;
    }

    @Override
    public String getType() {
        return typeName;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder(super.buildURI());

        if (StringUtils.isNotBlank(id)) {
            try {
                sb.append("/").append(URLEncoder.encode(id, CHARSET));
            } catch (UnsupportedEncodingException e) {
                log.error("Error occurred while adding document id to uri.", e);
            }
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    protected abstract static class Builder<T extends AbstractDocumentTargetedAction, K> extends AbstractAction.Builder<T, K> {
        private String index;
        private String type;
        private String id;

        public K index(String index) {
            this.index = index;
            return (K) this;
        }

        public K type(String type) {
            this.type = type;
            return (K) this;
        }

        public K id(String id) {
            this.id = id;
            return (K) this;
        }

    }

}
