package io.searchbox.core.search.facet;

import java.util.Map;

/**
 * @author ferhat
 */
public class GeoDistanceFacet extends Facet {
    public static final String TYPE = "geo_distance";

    GeoDistanceFacet(String name, Map geoDistanceFacet) {
        this.name = name;
    }

}
