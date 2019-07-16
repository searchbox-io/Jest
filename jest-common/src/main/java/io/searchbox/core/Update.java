package io.searchbox.core;

import com.google.gson.Gson;
import io.searchbox.action.BulkableAction;
import io.searchbox.action.SingleResultAbstractDocumentTargetedAction;
import io.searchbox.client.config.ElasticsearchVersion;
import io.searchbox.params.Parameters;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Update extends SingleResultAbstractDocumentTargetedAction implements BulkableAction<DocumentResult> {

    private enum Method {doc, script}

    private static final String DOC_UPDATE_TEMPLATE = "{\"%s\": %s}";
    private final Method method;

    protected Update(Builder builder) {
        super(builder);
        this.payload = builder.payload;
        this.method = builder.method;
    }

    @Override
    public String getBulkMethodName() {
        return "update";
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_update";
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    public String getPathToResult() {
        return "ok";
    }

    @Override
    public String getData(Gson gson) {
        String data = super.getData(gson);
        if (method == null) {
            return data;
        } else {
            return String.format(DOC_UPDATE_TEMPLATE, method.name(), data);
        }
    }

    public static class Builder extends SingleResultAbstractDocumentTargetedAction.Builder<Update, Builder> {

        private final Object payload;
        private final Method method;

        public Builder(Object payload) {
            this(payload, null);
        }

        private Builder(Object payload, Method method) {
            this.payload = payload;
            this.method = method;
        }

        public Update build() {
            return new Update(this);
        }
    }

    public static class VersionBuilder extends Builder {

        public VersionBuilder(Object payload, Long version) {
            super(payload);
            this.setParameter(Parameters.VERSION, version);
        }
    }

    public static class DocBuilder extends Builder {

        public DocBuilder(Object payload) {
            super(payload, Method.doc);
        }
    }

    public static class ScriptBuilder extends Builder {

        public ScriptBuilder(Object payload) {
            super(payload, Method.script);
        }
    }
}
