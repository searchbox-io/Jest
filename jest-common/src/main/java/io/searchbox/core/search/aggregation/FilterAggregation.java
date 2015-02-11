package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;

import static io.searchbox.core.search.aggregation.AggregationField.DOC_COUNT;

/**
 * @author cfstout
 */
public class FilterAggregation extends Aggregation<FilterAggregation> {

    public static final String TYPE = "filter";

    private Long count;

    public <T extends Aggregation> FilterAggregation(String name, JsonObject filterAggregation) {
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

        FilterAggregation that = (FilterAggregation) o;

        if (!count.equals(that.count)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return count.hashCode();
    }
}
