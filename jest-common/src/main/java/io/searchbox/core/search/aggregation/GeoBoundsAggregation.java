package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;

import static io.searchbox.core.search.aggregation.AggregationField.BOTTOM_RIGHT;
import static io.searchbox.core.search.aggregation.AggregationField.BOUNDS;
import static io.searchbox.core.search.aggregation.AggregationField.LAT;
import static io.searchbox.core.search.aggregation.AggregationField.LON;
import static io.searchbox.core.search.aggregation.AggregationField.TOP_LEFT;

/**
 * @author cfstout
 */
public class GeoBoundsAggregation extends Aggregation<GeoBoundsAggregation> {

    public static final String TYPE = "geo_bounds";

    private Double topLeftLat;
    private Double topLeftLon;
    private Double bottomRightLat;
    private Double bottomRightLon;

    public GeoBoundsAggregation(String name, JsonObject geoBoundsAggregation) {
        super(name, geoBoundsAggregation);
        if( geoBoundsAggregation.has(String.valueOf(BOUNDS))) {
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GeoBoundsAggregation that = (GeoBoundsAggregation) o;

        if (bottomRightLat != null ? !bottomRightLat.equals(that.bottomRightLat) : that.bottomRightLat != null) {
            return false;
        }
        if (bottomRightLon != null ? !bottomRightLon.equals(that.bottomRightLon) : that.bottomRightLon != null) {
            return false;
        }
        if (topLeftLat != null ? !topLeftLat.equals(that.topLeftLat) : that.topLeftLat != null) {
            return false;
        }
        if (topLeftLon != null ? !topLeftLon.equals(that.topLeftLon) : that.topLeftLon != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = topLeftLat != null ? topLeftLat.hashCode() : 0;
        result = 31 * result + (topLeftLon != null ? topLeftLon.hashCode() : 0);
        result = 31 * result + (bottomRightLat != null ? bottomRightLat.hashCode() : 0);
        result = 31 * result + (bottomRightLon != null ? bottomRightLon.hashCode() : 0);
        return result;
    }
}
