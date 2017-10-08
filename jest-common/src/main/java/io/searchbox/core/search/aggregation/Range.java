package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents data range defined by two limits (a lower limit called from and an upper limit called to) in a bucket.
 */
public class Range extends Bucket {
    private Double from = Double.NEGATIVE_INFINITY;
    private Double to = Double.POSITIVE_INFINITY;

    public Range(JsonObject bucket, Double from, Double to, Long count) {
        super(bucket, count);
        this.from = from;
        this.to = to;
    }

    public Double getFrom() {
        return from;
    }

    public Double getTo() {
        return to;
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
                .append(count, rhs.count)
                .append(from, rhs.from)
                .append(to, rhs.to)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(count)
                .append(from)
                .append(to)
                .toHashCode();
    }
}