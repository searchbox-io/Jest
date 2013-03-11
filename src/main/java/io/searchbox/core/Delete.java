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


public class Delete extends AbstractAction implements Action {

    final static Logger log = LoggerFactory.getLogger(Delete.class);

    public static class Builder {
        private String index;
        private String type;
        private String id;

        public Builder() {
        }

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

        public Delete build() {
            return new Delete(this);
        }
    }

    private Delete(Builder builder) {
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
    public String getName() {
        return "DELETE";
    }

    @Override
    public String getRestMethodName() {
        return "DELETE";
    }

    @Override
    public String getPathToResult() {
        return "ok";
    }

    @Override
    public Boolean isOperationSucceed(Map result) {
        return ((Boolean) result.get("ok") && (Boolean) result.get("found"));
    }
}
