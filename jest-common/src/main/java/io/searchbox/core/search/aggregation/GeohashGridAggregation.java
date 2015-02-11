package io.searchbox.core.search.aggregation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static io.searchbox.core.search.aggregation.AggregationField.BUCKETS;
import static io.searchbox.core.search.aggregation.AggregationField.DOC_COUNT;
import static io.searchbox.core.search.aggregation.AggregationField.KEY;

/**
 * @author cfstout
 */
public class GeohashGridAggregation extends Aggregation<GeoBoundsAggregation> {

    public static final String TYPE = "geohash_grid";

    private List<GeohashGrid> geohashGrids;

    public <T extends Aggregation> GeohashGridAggregation(String name, JsonObject geohashGridAggregation) {
        super(name, geohashGridAggregation);
        geohashGrids = new ArrayList<GeohashGrid>();
        for (JsonElement bucketv : geohashGridAggregation.get(String.valueOf(BUCKETS)).getAsJsonArray()) {
            JsonObject bucket = bucketv.getAsJsonObject();
            GeohashGrid geohashGrid = new GeohashGrid(
                    bucket.get(String.valueOf(KEY)).getAsString(),
                    bucket.get(String.valueOf(DOC_COUNT)).getAsLong());
            geohashGrids.add(geohashGrid);
        }
    }

    public List<GeohashGrid> getGeohashGrids() {
        return geohashGrids;
    }

    public static class GeohashGrid {
        private String key;
        private Long count;

        public GeohashGrid(String key, Long count) {
            this.key = key;
            this.count = count;
        }

        public String getKey() {
            return key;
        }

        public Long getCount() {
            return count;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof GeohashGrid)) {
                return false;
            }

            GeohashGrid that = (GeohashGrid) o;

            if (!count.equals(that.count)) {
                return false;
            }
            if (!key.equals(that.key)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = key.hashCode();
            result = 31 * result + count.hashCode();
            return result;
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

        GeohashGridAggregation that = (GeohashGridAggregation) o;

        if (geohashGrids != null ? !geohashGrids.equals(that.geohashGrids) : that.geohashGrids != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return geohashGrids != null ? geohashGrids.hashCode() : 0;
    }
}
