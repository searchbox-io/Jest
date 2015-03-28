package io.searchbox.core.search.aggregation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.searchbox.core.search.aggregation.AggregationField.BUCKETS;

/**
 * @author cfstout
 */
public class FiltersAggregation extends BucketAggregation {

    public static final String TYPE = "filters";

    private String name;
    private Map<String, Bucket> bucketMap;
    private List<Bucket> bucketList;

    public FiltersAggregation(String name, JsonObject filtersAggregation) {
        super(name, filtersAggregation);
        bucketMap = new HashMap<String, Bucket>();
        bucketList = new ArrayList<Bucket>();
        if (filtersAggregation.get(String.valueOf(BUCKETS)).isJsonArray()) {
            int elementNumber = 0;
            for (JsonElement bucket : filtersAggregation.get(String.valueOf(BUCKETS)).getAsJsonArray()) {
                String filterName = "filter" + Integer.toString(elementNumber++);
                bucketMap.put(filterName, new FilterAggregation(filterName, bucket.getAsJsonObject()));
                bucketList.add(new FilterAggregation(filterName, bucket.getAsJsonObject()));
            }
        } else { // returned as a json object
            for (Map.Entry<String, JsonElement> bucket: filtersAggregation.get(String.valueOf(BUCKETS)).getAsJsonObject().entrySet()) {
                bucketMap.put(bucket.getKey(), new FilterAggregation(bucket.getKey(), bucket.getValue().getAsJsonObject()));
                bucketList.add(new FilterAggregation(bucket.getKey(), bucket.getValue().getAsJsonObject()));
            }
        }
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FiltersAggregation)) {
            return false;
        }

        FiltersAggregation rhs = (FiltersAggregation) o;
        return new EqualsBuilder()
                .append(getBuckets(), rhs.getBuckets())
                .append(getBucketMap(), rhs.getBucketMap())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getBuckets())
                .append(getBucketMap())
                .toHashCode();
    }
}

