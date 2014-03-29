package io.searchbox.core;

import com.google.gson.Gson;
import io.searchbox.action.BulkableAction;
import io.searchbox.action.GenericResultAbstractDocumentTargetedAction;
import io.searchbox.client.JestResult;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Update extends GenericResultAbstractDocumentTargetedAction implements BulkableAction<JestResult> {

    private Object payload;

    private Update(Builder builder) {
        super(builder);

        this.payload = builder.payload;
        setURI(buildURI());
    }

    @Override
    public String getBulkMethodName() {
        return "update";
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder(super.buildURI());
        sb.append("/_update");
        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    public Object getData(Gson gson) {
        return payload;
    }

    @Override
    public String getPathToResult() {
        return "ok";
    }

    public static class Builder extends GenericResultAbstractDocumentTargetedAction.Builder<Update, Builder> {
        private final Object payload;

        public Builder(Object payload) {
            this.payload = payload;
        }

        public Update build() {
            return new Update(this);
        }
    }

}
