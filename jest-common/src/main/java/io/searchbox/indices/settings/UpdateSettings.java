package io.searchbox.indices.settings;

import io.searchbox.action.AbstractMultiIndexActionBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Change specific index level settings in real time.
 *
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class UpdateSettings extends IndicesSettingsAbstractAction {

    protected UpdateSettings(Builder builder) {
        super(builder);
        this.payload = builder.source;
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

    public static class Builder extends AbstractMultiIndexActionBuilder<UpdateSettings, Builder> {
        private final Object source;

        /**
         * Please see the <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/indices-update-settings.html#indices-update-settings">related page on Elasticsearch guide</a>
         * for the list of settings that can be changed using this action/API.
         *
         * @param source body of request that includes updated settings
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
