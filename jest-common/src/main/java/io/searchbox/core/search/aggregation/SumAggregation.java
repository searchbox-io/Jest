package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author cfstout
 */
public class SumAggregation extends SingleValueAggregation {

    public static final String TYPE = "sum";

    public SumAggregation(String name, JsonObject sumAggregation) {
        super(name, sumAggregation);
    }

    public Double getSum() {
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

        SumAggregation rhs = (SumAggregation) obj;
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
