package io.searchbox.indices;

import io.searchbox.AbstractAction;
import io.searchbox.Action;

/**
 * @author ferhat
 * @author cihat keser
 */


public class PutMapping extends AbstractAction implements Action {

    private PutMapping() {
    }

    private PutMapping(String indexName, String type, Object source) {
        setRestMethodName("PUT");
        setURI(buildPutURI(indexName, type));
        setData(source);
    }

    private String buildPutURI(String indexName, String type) {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI(indexName, type, null))
                .append("/")
                .append("_mapping");
        return sb.toString();
    }

    public static class Builder {
        private String indexName;
        private String indexType;
        private String mappingSource;

        public Builder setIndexName(String indexName) {
            this.indexName = indexName;
            return this;
        }

        public Builder setIndexType(String indexType) {
            this.indexType = indexType;
            return this;
        }

        public Builder setMappingSource(String mappingSource) {
            this.mappingSource = mappingSource;
            return this;
        }

        public PutMapping build() {
            if(indexName == null) {
                throw new IllegalArgumentException("Index name cannot be null");
            }

            if(indexType == null) {
                throw new IllegalArgumentException("Index type cannot be null");
            }

            if(mappingSource == null) {
                throw new IllegalArgumentException("Mapping source cannot be null");
            }

            return new PutMapping(indexName, indexType, mappingSource);
        }
    }

}
