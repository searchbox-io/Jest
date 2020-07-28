package io.searchbox.indices;

import com.google.gson.Gson;
import io.searchbox.action.AbstractAction;
import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;

import java.util.*;

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
        this.payload = builder.body;
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_analyze";
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    public String getData(Gson gson) {
        return gson.toJson(payload, Map.class);
    }

    public static class Builder extends AbstractAction.Builder<Analyze, Builder> {
        private String index;
        private List<String> textToAnalyze = new ArrayList<>();
        private List<String> filters = new ArrayList<>();
        private Map<String, Object> body = new HashMap<String, Object>() {{
            put("text", textToAnalyze);
            put("filter", filters);
        }};

        public Builder index(String index) {
            this.index = index;
            return this;
        }

        public Builder text(String textToAnalyze) {
            this.textToAnalyze.add(textToAnalyze);
            return this;
        }

        public Builder text(Collection<? extends String> textToAnalyze) {
            this.textToAnalyze.addAll(textToAnalyze);
            return this;
        }

        public Builder analyzer(String analyzer) {
            body.put("analyzer", analyzer);
            return this;
        }

        /**
         * The analyzer can be derived based on a field mapping.
         */
        public Builder field(String field) {
            body.put("field", field);
            return this;
        }

        public Builder tokenizer(String tokenizer) {
            body.put("tokenizer", tokenizer);
            return this;
        }

        public Builder filter(String filter) {
            this.filters.add(filter);
            return this;
        }

        public Builder filter(Collection<? extends String> filters) {
            this.filters.addAll(filters);
            return this;
        }

        /**
         * By default, the format the tokens are returned in are in json and its called detailed.
         * The text format value provides the analyzed data in a text stream that is a bit more readable.
         */
//        public Builder format(String format) {
//            return setParameter("format", format);
//        }

        @Override
        public Analyze build() {
            return new Analyze(this);
        }
    }

}
