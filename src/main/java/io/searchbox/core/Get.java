package io.searchbox.core;

import io.searchbox.AbstractAction;
import io.searchbox.Action;
import org.apache.commons.lang.StringUtils;

import java.util.Map;


/**
 * @author Dogukan Sonmez
 */


public class Get extends AbstractAction implements Action {

    public static class Builder {
        private String index = null;
        private String type = null;
        private final String id;

        public Builder(String id) {
            this.id = id;
        }

        public Builder index(String val) {
            index = val;
            return this;
        }

        public Builder type(String val) {
            type = val;
            return this;
        }

        public Get build() {
            return new Get(this);
        }
    }

    private Get(Builder builder) {
        indexName = builder.index;
        typeName = builder.type;
        id = builder.id;
    }

    @Override
    public String getURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(buildURI(indexName, typeName, id));
        String queryString = buildQueryString();
        if (StringUtils.isNotBlank(queryString)) sb.append(queryString);
        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    @Override
    public String getPathToResult() {
        return "_source";
    }

    @Override
    public Boolean isOperationSucceed(Map result) {
        return (Boolean) result.get("exists");
    }
}
