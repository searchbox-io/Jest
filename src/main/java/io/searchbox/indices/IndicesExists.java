package io.searchbox.indices;

import io.searchbox.AbstractAction;
import io.searchbox.Action;

/**
 * @author Dogukan Sonmez
 */


public class IndicesExists extends AbstractAction implements Action {

    public IndicesExists(String indexName) {
        this.indexName = indexName;
        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "HEAD";
    }

}
