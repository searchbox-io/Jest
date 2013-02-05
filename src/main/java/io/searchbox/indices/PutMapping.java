package io.searchbox.indices;

import io.searchbox.AbstractAction;
import io.searchbox.Action;

/**
 * @author ferhat
 */


public class PutMapping extends AbstractAction implements Action {

    public PutMapping(String indexName, String type, Object source) {
        setURI(buildPutURI(indexName, type));
        setData(source);
        setRestMethodName("PUT");
    }

    private String buildPutURI(String indexName, String type) {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI(indexName, type, null))
                .append("/")
                .append("_mapping");
        return sb.toString();
    }

    @Override
    public String getName() {
        return "PUT_MAPPING";
    }
}
