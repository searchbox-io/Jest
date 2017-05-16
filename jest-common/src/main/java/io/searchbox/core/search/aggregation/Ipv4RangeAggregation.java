package io.searchbox.core.search.aggregation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static io.searchbox.core.search.aggregation.AggregationField.*;

/**
 * @author cfstout
 */
public class Ipv4RangeAggregation extends BucketAggregation{

    public static final String TYPE = "ip_range";

    private List<IpRange> ranges = new LinkedList<IpRange>();

    public Ipv4RangeAggregation(String name, JsonObject ipv4RangeAggregation) {
        super(name, ipv4RangeAggregation);
        if(ipv4RangeAggregation.has(String.valueOf(BUCKETS)) && ipv4RangeAggregation.get(String.valueOf(BUCKETS)).isJsonArray()) {
            parseBuckets(ipv4RangeAggregation.get(String.valueOf(BUCKETS)).getAsJsonArray());
        }
    }

    private void parseBuckets(JsonArray bucketsSource) {
        for (JsonElement bucketv : bucketsSource) {
            JsonObject bucket = bucketv.getAsJsonObject();
            IpRange range = new IpRange(
                    bucket,
                    bucket.has(String.valueOf(FROM)) ? bucket.get(String.valueOf(FROM)).getAsString() : null,
                    bucket.has(String.valueOf(TO)) ? bucket.get(String.valueOf(TO)).getAsString() : null,
                    bucket.has(String.valueOf(KEY)) ? bucket.get(String.valueOf(KEY)).getAsString() : null,
                    bucket.get(String.valueOf(DOC_COUNT)).getAsLong());
            ranges.add(range);
        }
    }

    public List<IpRange> getBuckets() {
        return ranges;
    }

    public class IpRange extends Bucket {
        private String from = "";
        private String to = "";
        private String key = "";

        public IpRange(JsonObject bucket, String from, String to, String key, Long count) {
            super(bucket, count);
            this.from = from;
            this.to = to;
            this.key = key;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }

        public String getKey() {
            return key;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            IpRange rhs = (IpRange) o;
            return Objects.equals(count, rhs.count)
                    && Objects.equals(from, rhs.from)
                    && Objects.equals(to, rhs.to)
                    && Objects.equals(key, rhs.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(count, from, to, key);
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
        return super.equals(obj) && Objects.equals(ranges, rhs.ranges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ranges);
    }
}
