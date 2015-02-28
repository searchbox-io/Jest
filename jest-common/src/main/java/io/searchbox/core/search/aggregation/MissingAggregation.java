package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static io.searchbox.core.search.aggregation.AggregationField.DOC_COUNT;

/**
 * @author cfstout
 */

public class MissingAggregation extends Aggregation {
    public static final String TYPE = "missing";

    private Long missing;

    public MissingAggregation(String name, JsonObject missingAggregation) {
        super(name, missingAggregation);
        missing = missingAggregation.get(String.valueOf(DOC_COUNT)).getAsLong();
    }

    public Long getMissing() {
        return missing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MissingAggregation rhs = (MissingAggregation) o;
        return new EqualsBuilder()
                .append(getMissing(), rhs.getMissing())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getMissing())
                .toHashCode();
    }
}
