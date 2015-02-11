package io.searchbox.core.search.aggregation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static io.searchbox.core.search.aggregation.AggregationField.BUCKETS;
import static io.searchbox.core.search.aggregation.AggregationField.COUNT;
import static io.searchbox.core.search.aggregation.AggregationField.DOC_COUNT;
import static io.searchbox.core.search.aggregation.AggregationField.FROM;
import static io.searchbox.core.search.aggregation.AggregationField.FROM_AS_STRING;
import static io.searchbox.core.search.aggregation.AggregationField.TO;
import static io.searchbox.core.search.aggregation.AggregationField.TO_AS_STRING;

/**
 * @author cfstout
 */
public class Ipv4RangeAggregation extends Aggregation<Ipv4RangeAggregation> {

    public static final String TYPE = "ip_range";

    private List<Ipv4Range> ranges;

    public Ipv4RangeAggregation(String name, JsonObject ipv4RangeAggregation) {
        super(name, ipv4RangeAggregation);
        ranges = new ArrayList<Ipv4Range>();
        //todo support keyed:true as well
        for (JsonElement bucketv : ipv4RangeAggregation.get(String.valueOf(BUCKETS)).getAsJsonArray()) {
            JsonObject bucket = bucketv.getAsJsonObject();
            Ipv4Range range = new Ipv4Range(
                    bucket.has(String.valueOf(FROM)) ? bucket.get(String.valueOf(FROM)).getAsDouble() : null,
                    bucket.has(String.valueOf(TO)) ? bucket.get(String.valueOf(TO)).getAsDouble() : null,
                    bucket.get(String.valueOf(DOC_COUNT)).getAsLong(),
                    bucket.has(String.valueOf(FROM_AS_STRING)) ? bucket.get(String.valueOf(FROM_AS_STRING)).getAsString() : null,
                    bucket.has(String.valueOf(TO_AS_STRING)) ? bucket.get(String.valueOf(TO_AS_STRING)).getAsString() : null);
            ranges.add(range);
        }
    }

    public List<Ipv4Range> getRanges() {
        return ranges;
    }

    public class Ipv4Range extends RangeAggregation.Range {
        private String fromAsString;
        private String toAsString;

        public Ipv4Range(Double from, Double to, Long count, String fromString, String toString){
            super(from, to, count);
            this.fromAsString = fromString;
            this.toAsString = toString;
        }

        public String getFromAsString() {
            return fromAsString;
        }

        public String getToAsString() {
            return toAsString;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }

            Ipv4Range ipv4Range = (Ipv4Range) o;

            if (fromAsString != null ? !fromAsString.equals(ipv4Range.fromAsString) : ipv4Range.fromAsString != null) {
                return false;
            }
            if (toAsString != null ? !toAsString.equals(ipv4Range.toAsString) : ipv4Range.toAsString != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (fromAsString != null ? fromAsString.hashCode() : 0);
            result = 31 * result + (toAsString != null ? toAsString.hashCode() : 0);
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

        Ipv4RangeAggregation that = (Ipv4RangeAggregation) o;

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
