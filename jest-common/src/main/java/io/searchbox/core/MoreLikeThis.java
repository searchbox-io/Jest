package io.searchbox.core;

import com.google.gson.Gson;
import io.searchbox.action.GenericResultAbstractDocumentTargetedAction;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class MoreLikeThis extends GenericResultAbstractDocumentTargetedAction {

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

        MoreLikeThis rhs = (MoreLikeThis) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(query, rhs.query)
                .isEquals();
    }

    public static class Builder extends GenericResultAbstractDocumentTargetedAction.Builder<MoreLikeThis, Builder> {
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
