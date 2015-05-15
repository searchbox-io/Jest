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
public class Ipv4RangeAggregation extends BucketAggregation{

    public static final String TYPE = "ip_range";

    private List<Ipv4Range> ranges = new LinkedList<Ipv4Range>();

    public Ipv4RangeAggregation(String name, JsonObject ipv4RangeAggregation) {
        super(name, ipv4RangeAggregation);
        if(ipv4RangeAggregation.has(String.valueOf(BUCKETS)) && ipv4RangeAggregation.get(String.valueOf(BUCKETS)).isJsonArray()) {
            parseBuckets(ipv4RangeAggregation.get(String.valueOf(BUCKETS)).getAsJsonArray());
        }
    }

    private void parseBuckets(JsonArray bucketsSource) {
        for (JsonElement bucketv : bucketsSource) {
            JsonObject bucket = bucketv.getAsJsonObject();
            Ipv4Range range = new Ipv4Range(
                    bucket,
                    bucket.has(String.valueOf(FROM)) ? bucket.get(String.valueOf(FROM)).getAsDouble() : null,
                    bucket.has(String.valueOf(TO)) ? bucket.get(String.valueOf(TO)).getAsDouble() : null,
                    bucket.get(String.valueOf(DOC_COUNT)).getAsLong(),
                    bucket.has(String.valueOf(FROM_AS_STRING)) ? bucket.get(String.valueOf(FROM_AS_STRING)).getAsString() : null,
                    bucket.has(String.valueOf(TO_AS_STRING)) ? bucket.get(String.valueOf(TO_AS_STRING)).getAsString() : null);
            ranges.add(range);
        }
    }

    public List<Ipv4Range> getBuckets() {
        return ranges;
    }

    public class Ipv4Range extends Range {
        private String fromAsString;
        private String toAsString;

        public Ipv4Range(JsonObject bucket, Double from, Double to, Long count, String fromString, String toString){
            super(bucket, from, to, count);
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

            Ipv4Range rhs = (Ipv4Range) obj;
            return new EqualsBuilder()
                    .appendSuper(super.equals(obj))
                    .append(toAsString, rhs.toAsString)
                    .append(fromAsString, rhs.fromAsString)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .appendSuper(super.hashCode())
                    .append(toAsString)
                    .append(fromAsString)
                    .toHashCode();
        }
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

        Ipv4RangeAggregation rhs = (Ipv4RangeAggregation) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(ranges, rhs.ranges)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(ranges)
                .toHashCode();
    }
}
