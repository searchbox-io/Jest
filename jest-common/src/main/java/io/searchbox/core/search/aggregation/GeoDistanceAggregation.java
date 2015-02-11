package io.searchbox.core.search.aggregation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static io.searchbox.core.search.aggregation.AggregationField.*;

/**
 * @author cfstout
 */
public class GeoDistanceAggregation extends Aggregation<GeoDistanceAggregation> {

    public static final String TYPE = "geo_distance";

    private List<GeoDistance> geoDistances;

    public GeoDistanceAggregation(String name, JsonObject geoDistanceAggregation) {
        super(name, geoDistanceAggregation);
        geoDistances = new ArrayList<GeoDistance>();
        //todo support keyed:true as well
        for (JsonElement bucketv : geoDistanceAggregation.get(String.valueOf(BUCKETS)).getAsJsonArray()) {
            JsonObject bucket = bucketv.getAsJsonObject();
            GeoDistance geoDistance = new GeoDistance(
                    bucket.has(String.valueOf(FROM)) ? bucket.get(String.valueOf(FROM)).getAsDouble() : null,
                    bucket.has(String.valueOf(TO)) ? bucket.get(String.valueOf(TO)).getAsDouble() : null,
                    bucket.has(String.valueOf(DOC_COUNT)) ? bucket.get(String.valueOf(DOC_COUNT)).getAsLong() : null);
            geoDistances.add(geoDistance);
        }
    }

    public List<GeoDistance> getGeoDistances() {
        return geoDistances;
    }

    public class GeoDistance extends RangeAggregation.Range {

        public GeoDistance(Double from, Double to, Long count) {
            super(from, to, count);
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

        GeoDistanceAggregation that = (GeoDistanceAggregation) o;

        if (!geoDistances.equals(that.geoDistances)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return geoDistances.hashCode();
    }
}
