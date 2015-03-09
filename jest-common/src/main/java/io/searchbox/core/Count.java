package io.searchbox.core;

import com.google.gson.Gson;
import io.searchbox.action.AbstractAction;
import io.searchbox.action.AbstractMultiTypeActionBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Count extends AbstractAction<CountResult> {

    private String query;

    public Count(Builder builder) {
        super(builder);

        this.query = builder.query;
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
    public CountResult createNewElasticSearchResult(String responseBody, int statusCode, String reasonPhrase, Gson gson) {
        return createNewElasticSearchResult(new CountResult(gson), responseBody, statusCode, reasonPhrase, gson);
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

        Count rhs = (Count) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(query, rhs.query)
                .isEquals();
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
