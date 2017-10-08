package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Place holder class used to represent the root aggregation
 * returned to the user for processing.
 *
 * @author cfstout
 */
public class RootAggregation extends MetricAggregation {

    public RootAggregation(String name, JsonObject root) {
        super(name, root);
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

        RootAggregation rhs = (RootAggregation) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append("root")
                .toHashCode();
    }
}
