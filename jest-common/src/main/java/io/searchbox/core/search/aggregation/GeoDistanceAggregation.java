package io.searchbox.core.search.aggregation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static io.searchbox.core.search.aggregation.AggregationField.*;

/**
 * @author cfstout
 */
public class GeoDistanceAggregation extends BucketAggregation {

    public static final String TYPE = "geo_distance";

    private List<Range> geoDistances = new LinkedList<Range>();

    public GeoDistanceAggregation(String name, JsonObject geoDistanceAggregation) {
        super(name, geoDistanceAggregation);
        if (geoDistanceAggregation.has(String.valueOf(BUCKETS)) && geoDistanceAggregation.get(String.valueOf(BUCKETS)).isJsonArray()) {
            parseBuckets(geoDistanceAggregation.get(String.valueOf(BUCKETS)).getAsJsonArray());
        }
    }

    private void parseBuckets(JsonArray buckets) {
        //todo support keyed:true as well
        for (JsonElement bucketv : buckets) {
            JsonObject bucket = bucketv.getAsJsonObject();
            Range geoDistance = new Range(
                    bucket,
                    bucket.has(String.valueOf(FROM)) ? bucket.get(String.valueOf(FROM)).getAsDouble() : null,
                    bucket.has(String.valueOf(TO)) ? bucket.get(String.valueOf(TO)).getAsDouble() : null,
                    bucket.has(String.valueOf(DOC_COUNT)) ? bucket.get(String.valueOf(DOC_COUNT)).getAsLong() : null);
            geoDistances.add(geoDistance);
        }
    }

    public List<Range> getBuckets() {
        return geoDistances;
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

        GeoDistanceAggregation rhs = (GeoDistanceAggregation) obj;
        return super.equals(obj) && Objects.equals(geoDistances, rhs.geoDistances);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), geoDistances);
    }
}
