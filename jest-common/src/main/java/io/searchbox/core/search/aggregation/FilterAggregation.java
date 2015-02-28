package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static io.searchbox.core.search.aggregation.AggregationField.DOC_COUNT;

/**
 * @author cfstout
 */
public class FilterAggregation extends Aggregation {

    public static final String TYPE = "filter";

    private Long count;

    public FilterAggregation(String name, JsonObject filterAggregation) {
        super(name, filterAggregation);
        this.count = filterAggregation.get(String.valueOf(DOC_COUNT)).getAsLong();
    }

    public Long getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FilterAggregation)) {
            return false;
        }

        FilterAggregation rhs = (FilterAggregation) o;
        return new EqualsBuilder()
                .append(getCount(), rhs.getCount())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getCount())
                .toHashCode();
    }
}
