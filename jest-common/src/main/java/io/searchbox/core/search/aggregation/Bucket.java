package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(count, rhs.count)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(count)
                .toHashCode();
    }

}
