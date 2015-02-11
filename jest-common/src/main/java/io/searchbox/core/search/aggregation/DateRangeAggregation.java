package io.searchbox.core.search.aggregation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static io.searchbox.core.search.aggregation.AggregationField.BUCKETS;
import static io.searchbox.core.search.aggregation.AggregationField.DOC_COUNT;
import static io.searchbox.core.search.aggregation.AggregationField.FROM;
import static io.searchbox.core.search.aggregation.AggregationField.FROM_AS_STRING;
import static io.searchbox.core.search.aggregation.AggregationField.TO;
import static io.searchbox.core.search.aggregation.AggregationField.TO_AS_STRING;

/**
 * @author cfstout
 */
public class DateRangeAggregation extends Aggregation<DateRangeAggregation> {

    public static final String TYPE = "date_range";

    private List<DateRange> ranges;

    public <T extends Aggregation> DateRangeAggregation(String name, JsonObject dateRangeAggregation) {
        super(name, dateRangeAggregation);
        ranges = new ArrayList<DateRange>();
        //todo support keyed:true as well
        for (JsonElement bucketv : dateRangeAggregation.get(String.valueOf(BUCKETS)).getAsJsonArray()) {
            JsonObject bucket = bucketv.getAsJsonObject();
            DateRange range = new DateRange(
                    bucket.has(String.valueOf(FROM)) ? bucket.get(String.valueOf(FROM)).getAsDouble() : null,
                    bucket.has(String.valueOf(TO)) ? bucket.get(String.valueOf(TO)).getAsDouble() : null,
                    bucket.get(String.valueOf(DOC_COUNT)).getAsLong(),
                    bucket.has(String.valueOf(FROM_AS_STRING)) ? bucket.get(String.valueOf(FROM_AS_STRING)).getAsString() : null,
                    bucket.has(String.valueOf(TO_AS_STRING)) ? bucket.get(String.valueOf(TO_AS_STRING)).getAsString() : null);
            ranges.add(range);
        }
    }

    public List<DateRange> getRanges() {
        return ranges;
    }

    public class DateRange extends RangeAggregation.Range {
        private String fromAsString;
        private String toAsString;

        public DateRange(Double from, Double to, Long count, String fromString, String toString){
            super(from, to, count);
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
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DateRangeAggregation)) {
            return false;
        }

        DateRangeAggregation that = (DateRangeAggregation) o;

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
