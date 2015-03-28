package io.searchbox.core.search.aggregation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

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
        for (JsonElement bucketv : rangeAggregation.get(String.valueOf(BUCKETS)).getAsJsonArray()) {
            JsonObject bucket = bucketv.getAsJsonObject();
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

    public static class Range extends Bucket {
        private Double from = Double.NEGATIVE_INFINITY;
        private Double to = Double.POSITIVE_INFINITY;

        public Range(JsonObject bucket, Double from, Double to, Long count) {
            super(bucket, count);
            this.from = from;
            this.to = to;
        }

        public Double getFrom() {
            return from;
        }

        public Double getTo() {
            return to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Range rhs = (Range) o;
            return new EqualsBuilder()
                    .append(getCount(), rhs.getCount())
                    .append(getFrom(), rhs.getFrom())
                    .append(getTo(), rhs.getTo())
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .append(getCount())
                    .append(getFrom())
                    .append(getTo())
                    .toHashCode();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RangeAggregation rhs = (RangeAggregation) o;
        return new EqualsBuilder()
                .append(getBuckets(), rhs.getBuckets())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getBuckets())
                .toHashCode();
    }
}
