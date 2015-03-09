package io.searchbox.core;

import com.google.gson.Gson;
import io.searchbox.action.AbstractMultiTypeActionBuilder;
import io.searchbox.action.GenericResultAbstractAction;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class DeleteByQuery extends GenericResultAbstractAction {

    private String query;

    public DeleteByQuery(Builder builder) {
        super(builder);

        this.query = builder.query;
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_query");
        return sb.toString();
    }

    @Override
    public String getPathToResult() {
        return "ok";
    }

    @Override
    public String getRestMethodName() {
        return "DELETE";
    }

    @Override
    public Object getData(Gson gson) {
        return query;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(query)
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

        DeleteByQuery rhs = (DeleteByQuery) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(query, rhs.query)
                .isEquals();
    }

    public static class Builder extends AbstractMultiTypeActionBuilder<DeleteByQuery, Builder> {

        private String query;

        public Builder(String query) {
            this.query = query;
        }

        @Override
        public DeleteByQuery build() {
            return new DeleteByQuery(this);
        }
    }

}
