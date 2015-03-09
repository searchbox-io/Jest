package io.searchbox.core;

import com.google.gson.Gson;
import io.searchbox.action.GenericResultAbstractAction;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Validate extends GenericResultAbstractAction {

    private Object query;

    private Validate(Builder builder) {
        super(builder);

        this.indexName = builder.index;
        this.typeName = builder.type;
        this.query = builder.query;
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder(super.buildURI());
        sb.append("/_validate/query");
        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    public Object getData(Gson gson) {
        return query;
    }

    @Override
    public String getPathToResult() {
        return "valid";
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

        Validate rhs = (Validate) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(query, rhs.query)
                .isEquals();
    }

    public static class Builder extends GenericResultAbstractAction.Builder<Validate, Builder> {
        private final Object query;
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

        public Validate build() {
            return new Validate(this);
        }
    }
}
