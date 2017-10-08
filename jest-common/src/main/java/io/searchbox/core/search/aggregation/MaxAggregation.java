package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author cfstout
 */
public class MaxAggregation extends SingleValueAggregation {

    public static final String TYPE = "max";

    public MaxAggregation(String name, JsonObject maxAggregation) {
        super(name, maxAggregation);
    }

    /**
     * @return Max if it was found and not null, null otherwise
     */
    public Double getMax() {
        return getValue();
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

        MaxAggregation rhs = (MaxAggregation) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(TYPE)
                .toHashCode();
    }

}
