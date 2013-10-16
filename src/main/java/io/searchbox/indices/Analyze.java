package io.searchbox.indices;

import com.google.gson.Gson;
import io.searchbox.AbstractAction;

/**
 * Performs the analysis process on a text and return the tokens breakdown of the text.
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Analyze extends AbstractAction {

    private Object source;

    public Analyze(Builder builder) {
        super(builder);

        this.indexName = builder.index;
        this.source = builder.source;
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
    public Object getData(Gson gson) {
        return source;
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
