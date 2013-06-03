package io.searchbox.indices;

import io.searchbox.AbstractAction;

/**
 * @author cihat keser
 */
public class OpenIndex extends AbstractAction {
    private OpenIndex() {
    }

    public OpenIndex(String index) {
        this.indexName = index;
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_open");
        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }
}
