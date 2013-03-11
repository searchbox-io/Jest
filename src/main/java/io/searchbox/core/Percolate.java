package io.searchbox.core;

import io.searchbox.AbstractAction;
import io.searchbox.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dogukan Sonmez
 */


public class Percolate extends AbstractAction implements Action {

    final static Logger log = LoggerFactory.getLogger(Percolate.class);

    public Percolate(String indexName, String type, Object query) {
        setURI(buildGetURI(indexName, type));
        setData(query);
        setRestMethodName("POST");
    }

    private String buildGetURI(String indexName, String type) {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI(indexName, type, null))
                .append("/")
                .append("_percolate");
        log.debug("Created URI for update action is :" + sb.toString());
        return sb.toString();
    }
}
