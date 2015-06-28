package io.searchbox.indices;

import io.searchbox.action.AbstractAction;
import io.searchbox.action.GenericResultAbstractAction;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Performs the analysis process on a text and return the tokens breakdown of the text.
 *
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Analyze extends GenericResultAbstractAction {

    protected Analyze(Builder builder) {
        super(builder);

        this.indexName = builder.index;
        this.payload = builder.source;
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_analyze");
        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
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

        Analyze rhs = (Analyze) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .isEquals();
    }

    public static class Builder extends AbstractAction.Builder<Analyze, Builder> {
        private String index;
        private Object source;

        public Builder index(String index) {
            this.index = index;
            return this;
        }

        public Builder source(Object source) {
            this.source = source;
            return this;
        }

        public Builder analyzer(String analyzer) {
            return setParameter("analyzer", analyzer);
        }

        /**
         * The analyzer can be derived based on a field mapping.
         */
        public Builder field(String field) {
            return setParameter("field", field);
        }

        public Builder tokenizer(String tokenizer) {
            return setParameter("tokenizer", tokenizer);
        }

        public Builder filter(String filter) {
            return setParameter("filters", filter);
        }

        /**
         * By default, the format the tokens are returned in are in json and its called detailed.
         * The text format value provides the analyzed data in a text stream that is a bit more readable.
         */
        public Builder format(String format) {
            return setParameter("format", format);
        }

        @Override
        public Analyze build() {
            return new Analyze(this);
        }
    }

}
