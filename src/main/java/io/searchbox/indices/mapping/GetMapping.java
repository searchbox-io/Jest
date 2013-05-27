package io.searchbox.indices.mapping;

import io.searchbox.AbstractAction;
import io.searchbox.AbstractMultiIndexActionBuilder;

/**
 * @author ferhat
 * @author cihat keser
 */
public class GetMapping extends AbstractAction {

    private GetMapping() {
    }

    private GetMapping(String indexName, String type) {
        this.indexName = indexName;
        this.typeName = type;
        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_mapping");
        return sb.toString();
    }

    public static class Builder extends AbstractMultiIndexActionBuilder<GetMapping, Builder> {

        @Override
        public GetMapping build() {
            return new GetMapping(getJoinedIndices(), getJoinedTypes());
        }
    }

}
