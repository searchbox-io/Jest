package io.searchbox.indices.settings;

import io.searchbox.action.GenericResultAbstractAction;

/**
 * @author cihat keser
 */
public abstract class IndicesSettingsAbstractAction extends GenericResultAbstractAction {

    protected IndicesSettingsAbstractAction(Builder builder) {
        super(builder);
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_settings");
        return sb.toString();
    }

}
