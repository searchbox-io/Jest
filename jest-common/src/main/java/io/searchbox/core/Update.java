package io.searchbox.core;

import com.google.gson.Gson;
import io.searchbox.action.BulkableAction;
import io.searchbox.action.GenericResultAbstractDocumentTargetedAction;
import io.searchbox.client.JestResult;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(payload)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        Update rhs = (Update) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(payload, rhs.payload)
                .isEquals();
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
