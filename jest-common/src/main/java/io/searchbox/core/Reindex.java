package io.searchbox.core;

import com.google.gson.Gson;
import io.searchbox.action.AbstractAction;
import io.searchbox.client.JestResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ozlevka on 5/16/16.
 */
public class Reindex extends AbstractAction<JestResult> {

    protected Reindex(Builder builder) {
        super(builder);
        setURI(buildURI());
        this.payload = builder.source;
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    public JestResult createNewElasticSearchResult(String responseBody, int statusCode, String reasonPhrase, Gson gson) {
        return createNewElasticSearchResult(new JestResult(gson), responseBody, statusCode, reasonPhrase, gson);
    }

    @Override
    protected String buildURI() {
        return super.buildURI() + "/_reindex?wait_for_completion=true";
    }


    public static class Builder extends AbstractAction.Builder<Reindex, Builder> {
        private Map<String, Object> source;

        @SuppressWarnings("unchecked")
        public Builder(String srcIndex, String destIndex) {
            source = new HashMap<String, Object>();
            source.put("source", new HashMap<String, Object>());
            source.put("dest", new HashMap<String, Object>());

            Map<String, Object> mp = (Map<String, Object>) source.get("source");
            mp.put("index", srcIndex);

            mp = (Map<String, Object>)source.get("dest");
            mp.put("index", destIndex);
        }

        @SuppressWarnings("unchecked")
        public Builder(String[] srcIndexes, String destIndex) {
            source = new HashMap<String, Object>();
            source.put("source", new HashMap<String, Object>());
            source.put("dest", new HashMap<String, Object>());

            Map<String, Object> mp = (Map<String, Object>)source.get("source");
            mp.put("index", srcIndexes);

            mp = (Map<String, Object>)source.get("dest");
            mp.put("index", destIndex);
        }

        @SuppressWarnings("unchecked")
        public Builder versionType(String vt) throws Exception {
            if(vt == null || vt.equals("")) {
                throw new Exception("version type can't be empty");
            }

            ((Map<String, Object>)source.get("dest")).put("version_type", vt);
            return this;
        }

        @SuppressWarnings("unchecked")
        public Builder operationType(String op) throws Exception {
            if(op == null || op.equals("")) {
                throw new Exception("operation type can't be empty");
            }

            ((Map<String, Object>)source.get("dest")).put("op_type", op);
            return this;
        }

        @SuppressWarnings("unchecked")
        public Builder srcDocumentType(String doc_type) throws Exception {
            if(doc_type == null || doc_type.equals("")) {
                throw new Exception("document type can't be empty");
            }

            ((Map<String, Object>)source.get("source")).put("type", doc_type);
            return this;
        }

        @SuppressWarnings("unchecked")
        public Builder srcDocumentType(String[] doc_types) throws Exception {
            if(doc_types == null || doc_types.length == 0) {
                throw new Exception("document type can't be empty");
            }

            ((Map<String, Object>)source.get("source")).put("type", doc_types);
            return this;
        }

        @SuppressWarnings("unchecked")
        public Builder query(String query) {
            Map<String, Object> q = (new Gson()).fromJson(query, Map.class);
            ((Map<String, Object>)source.get("source")).put("query", q);

            return this;
        }

        public Builder size(int s) throws Exception {
            if(s <= 0) {
                throw new Exception("size must be positive number");
            }

            source.put("size", s);

            return this;
        }

        public Builder conflicts(String s) throws Exception {
            if(s == null || s.equals("")) {
                String[] arg = new String[1];
                arg[0] = "conflicts value can't be empty";
                throw new Exception("conflicts value can't be empty");
            }

            source.put("conflicts", s);

            return this;
        }

        @Override
        public Reindex build() {
            return new Reindex(this);
        }
    }
}
