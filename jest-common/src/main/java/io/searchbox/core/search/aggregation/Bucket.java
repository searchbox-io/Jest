package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;

/**
 * @author cfstout
 */
public abstract class Bucket extends MetricAggregation {

    private Long count;

    public Bucket(JsonObject bucketRoot, Long count) {
        super("bucket", bucketRoot);
        this.count = count;
    }

    public Long getCount() {
        return count;
    }
}
