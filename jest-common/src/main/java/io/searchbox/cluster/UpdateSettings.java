package io.searchbox.cluster;

import io.searchbox.action.AbstractAction;
import io.searchbox.action.GenericResultAbstractAction;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Allows to update cluster wide specific settings. Settings updated can either be persistent (applied cross restarts)
 * or transient (will not survive a full cluster restart). The cluster responds with the settings updated.
 * <br/>
 * <br/>
 * There is a specific list of settings that can be updated, please see
 * <a href="http://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-update-settings.html#cluster-settings">Elasticsearch docs</a>
 * for more information.
 *
 * @author cihat keser
 */
public class UpdateSettings extends GenericResultAbstractAction {

    protected UpdateSettings(Builder builder) {
        super(builder);
        setURI(buildURI());
        this.payload = builder.source;
    }

    protected String buildURI() {
        return super.buildURI() + "/_cluster/settings";
    }

    @Override
    public String getRestMethodName() {
        return "PUT";
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .isEquals();
    }

    public static class Builder extends AbstractAction.Builder<UpdateSettings, Builder> {
        private final Object source;

        /**
         * There is a specific list of settings that can be updated, please see
         * <a href="http://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-update-settings.html#cluster-settings">Elasticsearch docs</a>
         * for more information.
         */
        public Builder(Object source) {
            this.source = source;
        }

        @Override
        public UpdateSettings build() {
            return new UpdateSettings(this);
        }
    }

}
