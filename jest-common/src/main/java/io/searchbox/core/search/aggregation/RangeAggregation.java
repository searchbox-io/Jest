package io.searchbox.core.search.aggregation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.searchbox.core.search.aggregation.AggregationField.*;

/**
 * @author cfstout
 */
public class RangeAggregation extends BucketAggregation {

    public static final String TYPE = "range";

    private List<Range> ranges;

    public RangeAggregation(String name, JsonObject rangeAggregation) {
        super(name, rangeAggregation);
        ranges = new ArrayList<Range>();
        //todo support keyed:true as well
        for (JsonElement bucketElement : rangeAggregation.get(String.valueOf(BUCKETS)).getAsJsonArray()) {
            JsonObject bucket = bucketElement.getAsJsonObject();
            Range range = new Range(
                    bucket,
                    bucket.has(String.valueOf(FROM)) ? bucket.get(String.valueOf(FROM)).getAsDouble() : null,
                    bucket.has(String.valueOf(TO)) ? bucket.get(String.valueOf(TO)).getAsDouble() : null,
                    bucket.get(String.valueOf(DOC_COUNT)).getAsLong());
            ranges.add(range);
        }
    }

    public List<Range> getBuckets() {
        return ranges;
    }

    public String getName() {
        return name;
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

        RangeAggregation rhs = (RangeAggregation) obj;
        return super.equals(obj) && Objects.equals(ranges, rhs.ranges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ranges);
    }
}
