package io.searchbox.core;

import io.searchbox.AbstractAction;
import io.searchbox.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dogukan Sonmez
 */


public class Update extends AbstractAction implements Action {

    final static Logger log = LoggerFactory.getLogger(Update.class);

    private Object script;

    public static class Builder {
        private String index;
        private String type;
        private String id = null;
        private final Object script;

        public Builder(Object script) {
            this.script = script;
        }

        public Builder id(String val) {
            id = val;
            return this;
        }

        public Builder index(String val) {
            index = val;
            return this;
        }

        public Builder type(String val) {
            type = val;
            return this;
        }

        public Update build() {
            return new Update(this);
        }
    }

    private Update(Builder builder) {
        indexName = builder.index;
        typeName = builder.type;
        id = builder.id;
        setData(builder.script);
    }

    @Override
    public String getURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI(indexName, typeName, id))
                .append("/_update");
        log.debug("Created URI for update action is :" + sb.toString());
        return sb.toString();
    }

    @Override
    public String getName() {
        return "UPDATE";
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    public String getPathToResult() {
        return "ok";
    }

}
