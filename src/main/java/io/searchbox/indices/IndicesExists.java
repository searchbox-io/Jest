package io.searchbox.indices;

import io.searchbox.AbstractAction;
import io.searchbox.Action;

/**
 * @author Dogukan Sonmez
 */


public class IndicesExists extends AbstractAction implements Action {

    public IndicesExists(String indexName) {
        setURI(buildURI(indexName, null, null));
        setRestMethodName("HEAD");
    }

    @Override
    public String getName() {
        return "INDICES_EXISTS";
    }
}
