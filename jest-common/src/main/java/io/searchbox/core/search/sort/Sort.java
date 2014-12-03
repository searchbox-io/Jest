package io.searchbox.core.search.sort;

import java.util.HashMap;
import java.util.Map;

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

    public Map<String, Object> toMap() {
        Map<String, Object> innerMap = new HashMap<String, Object>();

        if (order != null) {
            innerMap.put("order", order.toString());
        }
        if (missing != null) {
            innerMap.put("missing", missing.toString());
        }
        if (unmapped != null) {
            innerMap.put("ignore_unmapped", unmapped);
        }

        Map<String, Object> rootMap = new HashMap<String, Object>();
        rootMap.put(field, innerMap);
        return rootMap;
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