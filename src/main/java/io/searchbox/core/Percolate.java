package io.searchbox.core;

import io.searchbox.AbstractAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dogukan Sonmez
 */


public class Percolate extends AbstractAction {

    final static Logger log = LoggerFactory.getLogger(Percolate.class);

    public Percolate(String indexName, String type, Object query) {
        this.indexName = indexName;
        this.typeName = type;
        setURI(buildGetURI(indexName, type));
        setData(query);
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    private String buildGetURI(String indexName, String type) {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI())
                .append("/")
                .append("_percolate");
        log.debug("Created URI for update action is :" + sb.toString());
        return sb.toString();
    }
}
