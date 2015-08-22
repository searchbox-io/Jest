package io.searchbox.core;

import io.searchbox.action.SingleResultAbstractDocumentTargetedAction;


/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Get extends SingleResultAbstractDocumentTargetedAction {

    protected Get(Builder builder) {
        super(builder);
        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    @Override
    public String getPathToResult() {
        return "_source";
    }

    public static class Builder extends SingleResultAbstractDocumentTargetedAction.Builder<Get, Builder> {

        /**
         * <p>Index and ID parameters are mandatory but type is optional (_all will be used for type if left blank).
         * </p>
         * The get API allows for _type to be optional. Set it to _all in order to fetch the
         * first document matching the id across all types.
         */
        public Builder(String index, String id) {
            this.index(index);
            this.id(id);
            this.type("_all");
        }

        public Get build() {
            return new Get(this);
        }
    }
}
