package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;

import java.util.Objects;

/**
 * @author cfstout
 */
public abstract class Bucket extends MetricAggregation {

    protected Long count;

    public Bucket(JsonObject bucketRoot, Long count) {
        this("bucket", bucketRoot, count);
    }

    public Bucket(String name, JsonObject bucketRoot, Long count) {
        super(name, bucketRoot);
        this.count = count;
    }

    public Long getCount() {
        return count;
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
        Bucket rhs = (Bucket) obj;
        return super.equals(obj) && Objects.equals(count, rhs.count);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), count);
    }

}
