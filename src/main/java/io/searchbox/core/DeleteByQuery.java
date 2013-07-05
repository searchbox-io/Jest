package io.searchbox.core;

import io.searchbox.AbstractAction;
import io.searchbox.AbstractMultiTypeActionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class DeleteByQuery extends AbstractAction {

    final static Logger log = LoggerFactory.getLogger(DeleteByQuery.class);

    public DeleteByQuery(Builder builder) {
        super(builder);
        setData(builder.query);
        setURI(buildURI());
    }

    @Override
    public String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_query");
        log.debug("Created URI for delete by query action is : {}", sb.toString());
        return sb.toString();
    }

    protected String createQueryString(LinkedHashSet<String> set) {
        StringBuilder sb = new StringBuilder();
        String tmp = "";
        for (String index : set) {
            sb.append(tmp);
            sb.append(index);
            tmp = ",";
        }
        return sb.toString();
    }

    @Override
    public String getPathToResult() {
        return "ok";
    }

    @Override
    public String getRestMethodName() {
        return "DELETE";
    }

    public static class Builder extends AbstractMultiTypeActionBuilder<DeleteByQuery, Builder> {

        private String query;

        public Builder(String query) {
            this.query = query;
        }

        @Override
        public DeleteByQuery build() {
            return new DeleteByQuery(this);
        }
    }

}
