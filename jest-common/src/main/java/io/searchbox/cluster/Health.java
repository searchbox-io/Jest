package io.searchbox.cluster;

import io.searchbox.action.AbstractAction;
import io.searchbox.action.GenericResultAbstractAction;

/**
 * @author Dogukan Sonmez
 * @author Neil Gentleman
 */
public class Health extends GenericResultAbstractAction {

    protected Health(Builder builder) {
        super(builder);
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        return super.buildURI() + "/_cluster/health/";
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    public static class Builder extends AbstractAction.Builder<Health, Builder> {

        @Override
        public Health build() {
            return new Health(this);
        }
    }

}
