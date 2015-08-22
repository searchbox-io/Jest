package io.searchbox.core.search.aggregation;

import static io.searchbox.core.search.aggregation.AggregationField.BUCKETS;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author cfstout
 */
public class FiltersAggregation extends BucketAggregation {

    private final static Logger log = Logger.getLogger(FiltersAggregation.class.getName());
    public static final String TYPE = "filters";

    private String name;
    private Map<String, Bucket> bucketMap = new HashMap<String, Bucket>();
    private List<Bucket> bucketList = new LinkedList<Bucket>();

    public FiltersAggregation(String name, JsonObject filtersAggregation) {
        super(name, filtersAggregation);
        if (filtersAggregation.has(String.valueOf(BUCKETS))) {
            parseBuckets(filtersAggregation.get(String.valueOf(BUCKETS)));
        }
    }

    private void parseBuckets(JsonElement buckets) {
        if (buckets.isJsonArray()) {
            int elementNumber = 0;
            for (JsonElement bucket : buckets.getAsJsonArray()) {
                addBucket("filter" + Integer.toString(elementNumber++), bucket.getAsJsonObject());
            }
        } else if (buckets.isJsonObject()) {
            for (Map.Entry<String, JsonElement> bucket : buckets.getAsJsonObject().entrySet()) {
                addBucket(bucket.getKey(), bucket.getValue().getAsJsonObject());
            }
        } else {
            log.finest("Skipped bucket parsing because Buckets element of JSON was neither Object nor Array.");
        }
    }

    private void addBucket(String filterName, JsonObject bucketSource) {
        FilterAggregation bucket = new FilterAggregation(filterName, bucketSource);
        bucketMap.put(filterName, bucket);
        bucketList.add(bucket);
    }

    /**
     * Method for getting counts when filters when using anonymous filtering
     * @return A list of counts in the order that the filters were passed in
     */
    public List<Bucket> getBuckets() {
        return bucketList;
    }

    /**
     * Method for getting counts using named filters
     * @return A map filter names to associated counts
     */
    public Map<String, Bucket> getBucketMap() {
        return bucketMap;
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

        FiltersAggregation rhs = (FiltersAggregation) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(bucketMap, rhs.bucketMap)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(bucketMap)
                .toHashCode();
    }
}

