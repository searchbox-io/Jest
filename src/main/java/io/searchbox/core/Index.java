package io.searchbox.core;

import io.searchbox.AbstractAction;
import io.searchbox.Action;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class Index extends AbstractAction implements Action {

    final static Logger log = LoggerFactory.getLogger(Index.class);

    public static class Builder {
        private String index = null;
        private String type = null;
        private String id = null;
        private final Object source;

        public Builder(Object source) {
            this.source = source;
        }

        public Builder index(String val) {
            index = val;
            return this;
        }

        public Builder type(String val) {
            type = val;
            return this;
        }

        public Builder id(String val) {
            id = val;
            return this;
        }

        public Index build() {
            return new Index(this);
        }
    }

    private Index(Builder builder) {
        setData(builder.source);
        String id = StringUtils.isNotBlank(builder.id) ? builder.id : this.getIdFromSource(builder.source);
        prepareIndex(builder.index, builder.type, id);
    }

    private void prepareIndex(String indexName, String typeName, String id) {
        super.indexName = indexName;
        super.typeName = typeName;
        if (id != null) {
            setRestMethodName("PUT");
        } else {
            setRestMethodName("POST");
        }
        super.id = id;
    }

    /* Need to call buildURI method each time to check if new parameter added*/
    @Override
    public String getURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(buildURI(indexName, typeName, id));
        String queryString = buildQueryString();
        if (StringUtils.isNotBlank(queryString)) sb.append(queryString);
        return sb.toString();
    }

    @Override
    public String getName() {
        return "INDEX";
    }

    @Override
    public String getPathToResult() {
        return "ok";
    }

    @Override
    public Boolean isOperationSucceed(Map result) {
        return (Boolean) result.get("ok");
    }
}
