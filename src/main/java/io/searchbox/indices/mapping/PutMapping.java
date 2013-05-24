package io.searchbox.indices.mapping;

import io.searchbox.AbstractAction;
import io.searchbox.Action;

/**
 * @author ferhat
 * @author cihat keser
 */


public class PutMapping extends AbstractAction implements Action {

    private PutMapping() {
    }

    public PutMapping(String indexName, String type, Object source) {
        this.indexName = indexName;
        this.typeName = type;
        setURI(buildURI());
        setData(source);
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_mapping");
        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return "PUT";
    }

}
