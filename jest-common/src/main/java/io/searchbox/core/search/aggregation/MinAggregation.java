package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author cfstout
 */
public class MinAggregation extends SingleValueAggregation {

    public static final String TYPE = "min";

    public MinAggregation(String name, JsonObject minAggregation) {
        super(name, minAggregation);
    }

    /**
     * @return Min if it was found and not null, null otherwise
     */
    public Double getMin() {
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

        MinAggregation rhs = (MinAggregation) obj;
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
