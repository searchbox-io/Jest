package io.searchbox.indices.settings;

import io.searchbox.action.GenericResultAbstractAction;

/**
 * @author cihat keser
 */
public abstract class IndicesSettingsAbstractAction extends GenericResultAbstractAction {

    protected IndicesSettingsAbstractAction(Builder builder) {
        super(builder);
    }

    @Override
    protected String buildURI() {
        return super.buildURI() + "/_settings";
    }

}
