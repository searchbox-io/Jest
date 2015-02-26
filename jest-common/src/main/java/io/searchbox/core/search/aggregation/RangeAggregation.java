package io.searchbox.core.search.aggregation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
                .append(getRanges(), rhs.getRanges())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getRanges())
                .toHashCode();
    }
}
