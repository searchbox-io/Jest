package io.searchbox.core.search.aggregation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.LinkedList;
import java.util.List;

import static io.searchbox.core.search.aggregation.AggregationField.*;

/**
 * @author cfstout
 */
public class DateRangeAggregation extends BucketAggregation {

    public static final String TYPE = "date_range";

    private List<DateRange> ranges = new LinkedList<DateRange>();

    public DateRangeAggregation(String name, JsonObject dateRangeAggregation) {
        super(name, dateRangeAggregation);
        if (dateRangeAggregation.has(String.valueOf(BUCKETS)) && dateRangeAggregation.get(String.valueOf(BUCKETS)).isJsonArray()) {
            parseBuckets(dateRangeAggregation.get(String.valueOf(BUCKETS)).getAsJsonArray());
        }
    }

    private void parseBuckets(JsonArray buckets) {
        //todo support keyed:true as well
        for (JsonElement bucketv : buckets) {
            JsonObject bucket = bucketv.getAsJsonObject();
            DateRange range = new DateRange(
                    bucket,
                    bucket.has(String.valueOf(FROM)) ? bucket.get(String.valueOf(FROM)).getAsDouble() : null,
                    bucket.has(String.valueOf(TO)) ? bucket.get(String.valueOf(TO)).getAsDouble() : null,
                    bucket.get(String.valueOf(DOC_COUNT)).getAsLong(),
                    bucket.has(String.valueOf(FROM_AS_STRING)) ? bucket.get(String.valueOf(FROM_AS_STRING)).getAsString() : null,
                    bucket.has(String.valueOf(TO_AS_STRING)) ? bucket.get(String.valueOf(TO_AS_STRING)).getAsString() : null);
            ranges.add(range);
        }
    }

    public List<DateRange> getBuckets() {
        return ranges;
    }

    public class DateRange extends Range {
        private String fromAsString;
        private String toAsString;

        public DateRange(JsonObject bucket, Double from, Double to, Long count, String fromString, String toString) {
            super(bucket, from, to, count);
            this.fromAsString = fromString;
            this.toAsString = toString;
        }

        /**
         * @return From time as a string, or null if not specified
         */
        public String getFromAsString() {
            return fromAsString;
        }

        /**
         * @return To time as a string, or null if not specified
         */
        public String getToAsString() {
            return toAsString;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof DateRange)) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }

            DateRange rhs = (DateRange) o;
            return new EqualsBuilder()
                    .append(fromAsString, rhs.fromAsString)
                    .append(toAsString, rhs.toAsString)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .appendSuper(super.hashCode())
                    .append(fromAsString)
                    .append(toAsString)
                    .toHashCode();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DateRangeAggregation)) {
            return false;
        }

        DateRangeAggregation rhs = (DateRangeAggregation) o;
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
