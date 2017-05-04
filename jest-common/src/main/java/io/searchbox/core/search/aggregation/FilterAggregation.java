package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;

import java.util.Objects;

import static io.searchbox.core.search.aggregation.AggregationField.DOC_COUNT;

/**
 * @author cfstout
 */
public class FilterAggregation extends Bucket {

    public static final String TYPE = "filter";

    public FilterAggregation(String name, JsonObject filterAggregation) {
        super(name, filterAggregation, filterAggregation.get(String.valueOf(DOC_COUNT)).getAsLong());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), TYPE);
    }

}
