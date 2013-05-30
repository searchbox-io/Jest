package io.searchbox.indices;

import io.searchbox.AbstractAction;
import io.searchbox.AbstractMultiIndexActionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ferhat
 * @author cihat keser
 */
public class Status extends AbstractAction {

    final static Logger log = LoggerFactory.getLogger(Status.class);

    private Status() {
    }

    private Status(Builder builder) {
        this.indexName = builder.getJoinedIndices();
        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_status");
        log.debug("Created URI for status action is :" + sb.toString());
        return sb.toString();
    }

    public static class Builder extends AbstractMultiIndexActionBuilder<Status, Builder> {

        @Override
        public Status build() {
            return new Status(this);
        }
    }
}