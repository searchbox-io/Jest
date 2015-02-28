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
public class Ipv4RangeAggregation extends Aggregation {

    public static final String TYPE = "ip_range";

    private List<Ipv4Range> ranges;

    public Ipv4RangeAggregation(String name, JsonObject ipv4RangeAggregation) {
        super(name, ipv4RangeAggregation);
        ranges = new ArrayList<Ipv4Range>();
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

            Ipv4Range rhs = (Ipv4Range) o;
            return new EqualsBuilder()
                    .append(getToAsString(), rhs.getToAsString())
                    .append(getFromAsString(), rhs.getFromAsString())
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .appendSuper(super.hashCode())
                    .append(getToAsString())
                    .append(getFromAsString())
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

        Ipv4RangeAggregation rhs = (Ipv4RangeAggregation) o;
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
