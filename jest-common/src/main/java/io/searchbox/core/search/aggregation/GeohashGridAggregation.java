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
public class GeohashGridAggregation extends BucketAggregation{

    public static final String TYPE = "geohash_grid";

    private List<GeohashGrid> geohashGrids;

    public GeohashGridAggregation(String name, JsonObject geohashGridAggregation) {
        super(name, geohashGridAggregation);
        geohashGrids = new ArrayList<GeohashGrid>();
        for (JsonElement bucketv : geohashGridAggregation.get(String.valueOf(BUCKETS)).getAsJsonArray()) {
            JsonObject bucket = bucketv.getAsJsonObject();
            GeohashGrid geohashGrid = new GeohashGrid(
                    bucket,
                    bucket.get(String.valueOf(KEY)).getAsString(),
                    bucket.get(String.valueOf(DOC_COUNT)).getAsLong());
            geohashGrids.add(geohashGrid);
        }
    }

    public List<GeohashGrid> getBuckets() {
        return geohashGrids;
    }

    public static class GeohashGrid extends Bucket {
        private String key;

        public GeohashGrid(JsonObject bucket, String key, Long count) {
            super(bucket, count);
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof GeohashGrid)) {
                return false;
            }

            GeohashGrid rhs = (GeohashGrid) o;
            return new EqualsBuilder()
                    .append(getCount(), rhs.getCount())
                    .append(getKey(), rhs.getKey())
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .append(getKey())
                    .append(getCount())
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

        GeohashGridAggregation rhs = (GeohashGridAggregation) o;
        return new EqualsBuilder()
                .append(getBuckets(), rhs.getBuckets())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getBuckets())
                .toHashCode();
    }
}
