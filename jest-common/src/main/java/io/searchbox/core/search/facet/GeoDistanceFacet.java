package io.searchbox.core.search.facet;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author ferhat
 */
public class GeoDistanceFacet extends Facet {
    public static final String TYPE = "geo_distance";

    private List<Range> ranges;

    public GeoDistanceFacet(String name, JsonObject geoDistanceFacet) {
        this.name = name;
        ranges = new ArrayList<Range>();
        for (JsonElement termv : geoDistanceFacet.get("ranges").getAsJsonArray()) {
          JsonObject term = (JsonObject) termv;
            Range range = new Range(
                term.get("from")!=null?term.get("from").getAsDouble():null,
                term.get("to")!=null?term.get("to").getAsDouble():null,
                term.get("total_count").getAsLong(),  term.get("total").getAsDouble(),
                term.get("min").getAsDouble(), term.get("max").getAsDouble(), term.get("mean").getAsDouble());
            ranges.add(range);
        }
    }

    public List<Range> getRanges() {
        return ranges;
    }

    public class Range {
        private Double from;
        private Double to;
        private Long totalCount;
        private Double total;
        private Double min;
        private Double max;
        private Double mean;

        Range(Double from, Double to, Long totalCount, Double total,
              Double min, Double max, Double mean) {
            this.from = from;
            this.to = to;
            this.totalCount = totalCount;
            this.total = total;
            this.min = min;
            this.max = max;
            this.mean = mean;
        }

        public Double getFrom() {
            return from;
        }

        public Double getTo() {
            return to;
        }

        public Long getTotalCount() {
            return totalCount;
        }

        public Double getTotal() {
            return total;
        }

        public Double getMin() {
            return min;
        }

        public Double getMax() {
            return max;
        }

        public Double getMean() {
            return mean;
        }
    }

}
