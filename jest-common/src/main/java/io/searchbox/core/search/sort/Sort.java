package io.searchbox.core.search.sort;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * @author Riccardo Tasso
 * @author cihat keser
 */
public class Sort {
    // TODO:
    // * Geo Distance Sorting (Lat Lon as Properties, Lat Lon as String, Geohash, Lat Lon as Array)
    // * Script Based Sorting
    // * Track Scores (it should be in the Search object)

    private String field;
    private Sorting order;
    private Object missing;
    private Boolean unmapped;
    private String unmappedType;

    public Sort(String field) {
        this.field = field;
    }

    public Sort(String field, Sorting order) {
        this.field = field;
        this.order = order;
    }

    /**
     * @param m should be a Missing object (LAST or FIRST) or a custom value
     *          (String, Integer, Double, ...) that will be used for missing docs as the sort value
     */
    public void setMissing(Object m) {
        this.missing = m;
    }

    public void setIgnoreUnmapped() {
        this.unmapped = true;
    }

    public void setUnmappedType(String unmappedType) {
        this.unmappedType = unmappedType;
    }

    public JsonObject toJsonObject() {
        JsonObject sortDefinition = new JsonObject();
        if (order != null) {
            sortDefinition.add("order", new JsonPrimitive(order.toString()));
        }
        if (missing != null) {
            sortDefinition.add("missing", new JsonPrimitive(missing.toString()));
        }
        if (unmapped != null) {
            sortDefinition.add("ignore_unmapped", new JsonPrimitive(unmapped));
        }
        if(unmappedType != null) {
            sortDefinition.add("unmapped_type", new JsonPrimitive(unmappedType));
        }

        JsonObject sortObject = new JsonObject();
        sortObject.add(field, sortDefinition);

        return sortObject;
    }

    public enum Sorting {
        ASC("asc"),
        DESC("desc");

        private final String name;

        private Sorting(String s) {
            name = s;
        }

        public String toString() {
            return name;
        }
    }

    public enum Missing {
        LAST("_last"),
        FIRST("_first");

        private final String name;

        private Missing(String s) {
            name = s;
        }

        public String toString() {
            return name;
        }
    }

}