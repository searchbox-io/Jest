package io.searchbox.core;

import com.google.gson.Gson;
import io.searchbox.action.GenericResultAbstractDocumentTargetedAction;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Explain extends GenericResultAbstractDocumentTargetedAction {

    private Object query;

    private Explain(Builder builder) {
        super(builder);
        setURI(buildURI());
        this.query = builder.query;
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    @Override
    public Object getData(Gson gson) {
        return query;
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder(super.buildURI());
        sb.append("/_explain");
        return sb.toString();
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

        Explain rhs = (Explain) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(query, rhs.query)
                .isEquals();
    }

    public static class Builder extends GenericResultAbstractDocumentTargetedAction.Builder<Explain, Builder> {
        private final Object query;

        public Builder(String index, String type, String id, Object query) {
            this.index(index);
            this.type(type);
            this.id(id);
            this.query = query;
        }

        public Explain build() {
            return new Explain(this);
        }

    }

}
