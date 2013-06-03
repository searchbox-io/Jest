package io.searchbox.indices;

import io.searchbox.AbstractAction;

/**
 * @author cihat keser
 */
public class CloseIndex extends AbstractAction {
    private CloseIndex() {
    }

    public CloseIndex(String index) {
        this.indexName = index;
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_close");
        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }
}
