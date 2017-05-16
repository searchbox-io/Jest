package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;

import java.util.Objects;

import static io.searchbox.core.search.aggregation.AggregationField.*;

/**
 * @author cfstout
 */
public class GeoBoundsAggregation extends MetricAggregation {

    public static final String TYPE = "geo_bounds";

    private Double topLeftLat;
    private Double topLeftLon;
    private Double bottomRightLat;
    private Double bottomRightLon;

    public GeoBoundsAggregation(String name, JsonObject geoBoundsAggregation) {
        super(name, geoBoundsAggregation);
        if (geoBoundsAggregation.has(String.valueOf(BOUNDS))) {
            JsonObject bounds = geoBoundsAggregation.getAsJsonObject(String.valueOf(BOUNDS));
            JsonObject topLeft = bounds.getAsJsonObject(String.valueOf(TOP_LEFT));
            JsonObject bottomRight = bounds.getAsJsonObject(String.valueOf(BOTTOM_RIGHT));

            topLeftLat = topLeft.get(String.valueOf(LAT)).getAsDouble();
            topLeftLon = topLeft.get(String.valueOf(LON)).getAsDouble();
            bottomRightLat = bottomRight.get(String.valueOf(LAT)).getAsDouble();
            bottomRightLon = bottomRight.get(String.valueOf(LON)).getAsDouble();
        }
    }

    /**
     * @return Top left latitude if bounds exist, null otherwise
     */
    public Double getTopLeftLat() {
        return topLeftLat;
    }

    /**
     * @return Top left longitude if bounds exist, null otherwise
     */
    public Double getTopLeftLon() {
        return topLeftLon;
    }

    /**
     * @return Bottom right latitude if bounds exist, null otherwise
     */
    public Double getBottomRightLat() {
        return bottomRightLat;
    }

    /**
     * @return Bottom right longitude if bounds exist, null otherwise
     */
    public Double getBottomRightLon() {
        return bottomRightLon;
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

        GeoBoundsAggregation rhs = (GeoBoundsAggregation) obj;
        return super.equals(obj)
                && Objects.equals(topLeftLat, rhs.topLeftLat)
                && Objects.equals(topLeftLon, rhs.topLeftLon)
                && Objects.equals(bottomRightLat, rhs.bottomRightLat)
                && Objects.equals(bottomRightLon, rhs.bottomRightLon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                bottomRightLat,
                bottomRightLon,
                topLeftLat,
                topLeftLon);
    }
}
