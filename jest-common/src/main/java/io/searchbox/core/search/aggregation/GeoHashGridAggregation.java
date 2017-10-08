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
public class GeoHashGridAggregation extends BucketAggregation{

    public static final String TYPE = "geohash_grid";

    private List<GeoHashGrid> geoHashGrids = new LinkedList<GeoHashGrid>();

    public GeoHashGridAggregation(String name, JsonObject geohashGridAggregation) {
        super(name, geohashGridAggregation);
        if(geohashGridAggregation.has(String.valueOf(BUCKETS)) && geohashGridAggregation.get(String.valueOf(BUCKETS)).isJsonArray()) {
            parseBuckets(geohashGridAggregation.get(String.valueOf(BUCKETS)).getAsJsonArray());
        }
    }

    private void parseBuckets(JsonArray bucketsSource) {
        for (JsonElement bucketElement : bucketsSource) {
            JsonObject bucket = bucketElement.getAsJsonObject();
            GeoHashGrid geoHashGrid = new GeoHashGrid(
                    bucket,
                    bucket.get(String.valueOf(KEY)).getAsString(),
                    bucket.get(String.valueOf(DOC_COUNT)).getAsLong());
            geoHashGrids.add(geoHashGrid);
        }
    }

    public List<GeoHashGrid> getBuckets() {
        return geoHashGrids;
    }

    public static class GeoHashGrid extends Bucket {
        private String key;

        public GeoHashGrid(JsonObject bucket, String key, Long count) {
            super(bucket, count);
            this.key = key;
        }

        public String getKey() {
            return key;
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

            GeoHashGrid rhs = (GeoHashGrid) obj;
            return new EqualsBuilder()
                    .appendSuper(super.equals(obj))
                    .append(key, rhs.key)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .appendSuper(super.hashCode())
                    .append(key)
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

        GeoHashGridAggregation rhs = (GeoHashGridAggregation) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(geoHashGrids, rhs.geoHashGrids)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(geoHashGrids)
                .toHashCode();
    }
}
