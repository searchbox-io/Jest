package io.searchbox.core.search.aggregation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static io.searchbox.core.search.aggregation.AggregationField.BUCKETS;
import static io.searchbox.core.search.aggregation.AggregationField.DOC_COUNT;
import static io.searchbox.core.search.aggregation.AggregationField.FROM;
import static io.searchbox.core.search.aggregation.AggregationField.TO;

/**
 * @author cfstout
 */
public class RangeAggregation extends Aggregation<RangeAggregation> {

    public static final String TYPE = "range";

    private List<Range> ranges;

    public RangeAggregation(String name, JsonObject rangeAggregation) {
        super(name, rangeAggregation);
        ranges = new ArrayList<Range>();
        //todo support keyed:true as well
        for (JsonElement bucketv : rangeAggregation.get(String.valueOf(BUCKETS)).getAsJsonArray()) {
            JsonObject bucket = bucketv.getAsJsonObject();
            Range range = new Range(
                    bucket.has(String.valueOf(FROM)) ? bucket.get(String.valueOf(FROM)).getAsDouble() : null,
                    bucket.has(String.valueOf(TO)) ? bucket.get(String.valueOf(TO)).getAsDouble() : null,
                    bucket.get(String.valueOf(DOC_COUNT)).getAsLong());
            ranges.add(range);
        }
    }

    public List<Range> getRanges() {
        return ranges;
    }

    public static class Range {
        private Double from = Double.NEGATIVE_INFINITY;
        private Double to = Double.POSITIVE_INFINITY;
        private Long count;

        public Range(Double from, Double to, Long count) {
            this.count = count;
            this.from = from;
            this.to = to;
        }

        public Double getFrom() {
            return from;
        }

        public Double getTo() {
            return to;
        }

        public Long getCount() {
            return count;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Range range = (Range) o;

            if (!count.equals(range.count)) {
                return false;
            }
            if (from != null ? !from.equals(range.from) : range.from != null) {
                return false;
            }
            if (to != null ? !to.equals(range.to) : range.to != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = from != null ? from.hashCode() : 0;
            result = 31 * result + (to != null ? to.hashCode() : 0);
            result = 31 * result + count.hashCode();
            return result;
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

        RangeAggregation that = (RangeAggregation) o;

        if (!ranges.equals(that.ranges)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return ranges.hashCode();
    }
}
