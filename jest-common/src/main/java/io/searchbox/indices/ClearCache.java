package io.searchbox.indices;

import io.searchbox.AbstractAction;
import io.searchbox.AbstractMultiIndexActionBuilder;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class ClearCache extends AbstractAction {

    public ClearCache(Builder builder) {
        super(builder);
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_cache/clear");
        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    public static class Builder extends AbstractMultiIndexActionBuilder<ClearCache, Builder> {

        public Builder filter(boolean filter) {
            setParameter("filter", filter);
            return this;
        }

        public Builder fieldData(boolean fieldData) {
            setParameter("field_data", fieldData);
            return this;
        }

        public Builder bloom(boolean bloom) {
            setParameter("bloom", bloom);
            return this;
        }

        @Override
        public ClearCache build() {
            return new ClearCache(this);
        }
    }
}
