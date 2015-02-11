package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;

import static io.searchbox.core.search.aggregation.AggregationField.DOC_COUNT;

/**
 * @author cfstout
 */

public class MissingAggregation extends Aggregation<MissingAggregation> {
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

        MissingAggregation that = (MissingAggregation) o;

        if (missing != null ? !missing.equals(that.missing) : that.missing != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return missing != null ? missing.hashCode() : 0;
    }
}
